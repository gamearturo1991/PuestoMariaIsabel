package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import puesto.maria.entity.Lote;
import puesto.maria.entity.Merma;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Usuario;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acceso a datos de las mermas (PBI-MER), usando solo la tabla merma del diseño original.
 * <ul>
 *   <li>Una merma que abarca piezas de varias entradas se guarda como varias filas (una por lote), todas
 *   con la misma fecha y hora.</li>
 *   <li>Una anulación (rescate de piezas) NO borra nada: se guarda como una fila nueva con las piezas en
 *   negativo, el mismo lote y costo, el motivo {@link #MOTIVO_ANULACION} y en observaciones la referencia a
 *   la fila original y la descripción del error ("ANULA#id|descripción").</li>
 * </ul>
 */
public class MermaDAO extends AbstractDAO<Merma> {

    /** Motivo con el que se guardan las filas de anulación. No es un motivo que se pueda elegir al mermar. */
    public static final String MOTIVO_ANULACION = "Anulación de merma";

    private static final String PREFIJO_ANULACION = "ANULA#";

    private final EntityManager entityManager;

    public MermaDAO(EntityManager em) {
        super(Merma.class);
        this.entityManager = em;
    }

    /** True si la fila es una anulación (piezas en negativo que apuntan a una merma original). */
    public static boolean esAnulacion(Merma m) {
        return m.getCantidad() != null && m.getCantidad() < 0
                && m.getObservaciones() != null && m.getObservaciones().startsWith(PREFIJO_ANULACION)
                && m.getObservaciones().indexOf('|') > PREFIJO_ANULACION.length();
    }

    /** Id de la fila de merma original que anula esta fila de anulación. */
    public static Integer idMermaAnulada(Merma anulacion) {
        String obs = anulacion.getObservaciones();
        return Integer.valueOf(obs.substring(PREFIJO_ANULACION.length(), obs.indexOf('|')));
    }

    /** Descripción del error capturada al anular. */
    public static String descripcionAnulacion(Merma anulacion) {
        String obs = anulacion.getObservaciones();
        return obs.substring(obs.indexOf('|') + 1);
    }

    /**
     * Registra una merma en UNA sola transacción (todo o nada): descuenta las piezas de las entradas más
     * antiguas y guarda, por cada una, una fila con el costo con el que entraron.
     *
     * @return las filas de merma creadas (una por lote afectado)
     * @throws IllegalArgumentException si no hay existencia suficiente
     */
    public List<Merma> registrarMerma(Integer idProducto, int cantidad, String motivo, String observaciones,
                                      Integer idUsuario) {
        try {
            return execute(em -> {
                Producto producto = em.find(Producto.class, idProducto);
                if (producto == null) {
                    throw new IllegalArgumentException("El producto ya no existe.");
                }
                List<Lote> lotes = em.createQuery(
                                "SELECT l FROM Lote l JOIN l.idEntrada e "
                                        + "WHERE l.idProducto.id = :id AND l.cantidadDisponible > 0 "
                                        + "ORDER BY e.fechaHora, l.id", Lote.class)
                        .setParameter("id", idProducto)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .getResultList();

                // Se relee cada lote para no trabajar con cantidades desactualizadas.
                long disponible = 0;
                for (Lote l : lotes) {
                    em.refresh(l);
                    disponible += l.getCantidadDisponible();
                }
                if (cantidad > disponible) {
                    throw new IllegalArgumentException("Solo hay " + disponible + " pieza(s) de "
                            + producto.getNombre() + " en existencia.");
                }

                Instant ahora = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Usuario usuario = em.getReference(Usuario.class, idUsuario);
                List<Merma> filas = new ArrayList<>();
                int restante = cantidad;
                for (Lote lote : lotes) {
                    if (restante == 0) {
                        break;
                    }
                    int tomar = Math.min(restante, lote.getCantidadDisponible());
                    if (tomar <= 0) {
                        continue;
                    }
                    lote.setCantidadDisponible(lote.getCantidadDisponible() - tomar);

                    Merma m = new Merma();
                    m.setIdLote(lote);
                    m.setIdUsuario(usuario);
                    m.setFechaHora(ahora);
                    m.setCantidad(tomar);
                    m.setMotivo(motivo);
                    m.setCostoUnitario(lote.getCostoUnitario());
                    m.setObservaciones(observaciones);
                    em.persist(m);
                    filas.add(m);
                    restante -= tomar;
                }
                return filas;
            });
        } catch (RuntimeException ex) {
            // Tras un rollback se descartan los objetos en memoria para no arrastrar cambios a medias.
            entityManager.clear();
            throw ex;
        }
    }

    /**
     * Anula (rescata) piezas de una merma, total o parcialmente. Las piezas regresan a los lotes de donde
     * salieron y la merma original se conserva; cada rescate queda como una fila de anulación.
     *
     * @param idsMerma filas de merma que forman la merma que se anula
     * @throws IllegalArgumentException si se piden más piezas de las que quedan sin rescatar
     */
    public void anularMerma(List<Integer> idsMerma, int cantidad, String descripcion, Integer idUsuario) {
        try {
            execute(em -> {
                List<Merma> filas = em.createQuery(
                                "SELECT m FROM Merma m WHERE m.id IN :ids AND m.cantidad > 0 ORDER BY m.id",
                                Merma.class)
                        .setParameter("ids", idsMerma)
                        .getResultList();
                if (filas.isEmpty()) {
                    throw new IllegalArgumentException("La merma ya no existe.");
                }
                Map<Integer, Long> rescatadas = rescatadasPorMerma(anulacionesDe(em));
                long pendientes = 0;
                for (Merma m : filas) {
                    pendientes += m.getCantidad() - rescatadas.getOrDefault(m.getId(), 0L);
                }
                if (cantidad > pendientes) {
                    throw new IllegalArgumentException("Solo quedan " + pendientes
                            + " pieza(s) por rescatar de esta merma.");
                }

                Instant ahora = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Usuario usuario = em.getReference(Usuario.class, idUsuario);
                int restante = cantidad;
                for (Merma m : filas) {
                    if (restante == 0) {
                        break;
                    }
                    long capacidad = m.getCantidad() - rescatadas.getOrDefault(m.getId(), 0L);
                    int devolver = (int) Math.min(capacidad, restante);
                    if (devolver <= 0) {
                        continue;
                    }
                    Lote lote = m.getIdLote();
                    em.refresh(lote, LockModeType.PESSIMISTIC_WRITE);
                    lote.setCantidadDisponible(lote.getCantidadDisponible() + devolver);

                    Merma a = new Merma();
                    a.setIdLote(lote);
                    a.setIdUsuario(usuario);
                    a.setFechaHora(ahora);
                    a.setCantidad(-devolver);
                    a.setMotivo(MOTIVO_ANULACION);
                    a.setCostoUnitario(m.getCostoUnitario());
                    a.setObservaciones(PREFIJO_ANULACION + m.getId() + "|" + descripcion);
                    em.persist(a);
                    restante -= devolver;
                }
                return null;
            });
        } catch (RuntimeException ex) {
            entityManager.clear();
            throw ex;
        }
    }

    /** Filas de merma originales del periodo [desde, hasta), las más recientes primero, con producto y usuario. */
    public List<Merma> consultar(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT m FROM Merma m JOIN FETCH m.idLote l JOIN FETCH l.idProducto p "
                                + "JOIN FETCH m.idUsuario u "
                                + "WHERE m.cantidad > 0 AND m.fechaHora >= :desde AND m.fechaHora < :hasta "
                                + "ORDER BY m.fechaHora DESC, m.id", Merma.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /** Todas las filas de anulación (de cualquier fecha), de la más antigua a la más reciente. */
    public List<Merma> listarAnulaciones() {
        return execute(em -> anulacionesDe(em));
    }

    /** Lotes con piezas disponibles de un producto, del más antiguo al más reciente (sin bloquear). */
    public List<Lote> lotesDisponibles(Integer idProducto) {
        return execute(em -> em.createQuery(
                        "SELECT l FROM Lote l JOIN l.idEntrada e "
                                + "WHERE l.idProducto.id = :id AND l.cantidadDisponible > 0 "
                                + "ORDER BY e.fechaHora, l.id", Lote.class)
                .setParameter("id", idProducto)
                .getResultList());
    }

    /** Existencia total de un producto: suma de la cantidad disponible de sus lotes. */
    public int existenciaTotal(Integer idProducto) {
        Long total = execute(em -> em
                .createQuery("SELECT COALESCE(SUM(l.cantidadDisponible), 0) FROM Lote l WHERE l.idProducto.id = :id",
                        Long.class)
                .setParameter("id", idProducto)
                .getSingleResult());
        return Math.toIntExact(total);
    }

    /** Piezas rescatadas por fila de merma original: id de la merma original -> piezas. */
    public static Map<Integer, Long> rescatadasPorMerma(List<Merma> anulaciones) {
        Map<Integer, Long> mapa = new HashMap<>();
        for (Merma a : anulaciones) {
            if (esAnulacion(a)) {
                mapa.merge(idMermaAnulada(a), (long) -a.getCantidad(), Long::sum);
            }
        }
        return mapa;
    }

    private static List<Merma> anulacionesDe(EntityManager em) {
        return em.createQuery(
                        "SELECT m FROM Merma m JOIN FETCH m.idUsuario "
                                + "WHERE m.cantidad < 0 ORDER BY m.fechaHora, m.id", Merma.class)
                .getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
