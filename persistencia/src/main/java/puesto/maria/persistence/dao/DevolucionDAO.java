package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import puesto.maria.entity.DetalleDevolucion;
import puesto.maria.entity.DetalleVenta;
import puesto.maria.entity.DetalleVentaLote;
import puesto.maria.entity.Devolucion;
import puesto.maria.entity.Lote;
import puesto.maria.entity.Merma;
import puesto.maria.entity.Usuario;
import puesto.maria.entity.Venta;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Devoluciones: las piezas revendibles regresan al lote del que salieron al venderse; las no revendibles
 * se guardan como merma ligada a la devolución (merma.id_devolucion).
 */
public class DevolucionDAO extends AbstractDAO<Devolucion> {

    /** Motivo de las mermas que salen de piezas devueltas que no se pueden revender. */
    public static final String MOTIVO_MERMA = "Devolución no revendible";

    private final EntityManager entityManager;

    public DevolucionDAO(EntityManager em) {
        super(Devolucion.class);
        this.entityManager = em;
    }

    /** Renglones de una venta, en el orden en que se capturaron, con su producto. */
    public List<DetalleVenta> detallesDeVenta(Integer idVenta) {
        return execute(em -> em.createQuery(
                        "SELECT d FROM DetalleVenta d JOIN FETCH d.idProducto "
                                + "WHERE d.idVenta.id = :id ORDER BY d.id", DetalleVenta.class)
                .setParameter("id", idVenta)
                .getResultList());
    }

    /** Piezas ya devueltas de cada renglón de la venta: id del renglón -> piezas. */
    public Map<Integer, Long> devueltasPorDetalle(Integer idVenta) {
        return execute(em -> devueltasPorDetalle(em, idVenta));
    }

    /**
     * Registra una devolución en UNA sola transacción (todo o nada): valida contra lo vendido y lo ya
     * devuelto, guarda la devolución y su detalle por lote, regresa al inventario las piezas revendibles
     * y manda a merma las que no.
     *
     * @throws IllegalArgumentException si la venta no admite devoluciones o se piden más piezas de las vendidas
     */
    public Devolucion registrarDevolucion(Integer idVenta, Integer idUsuario, String motivo,
                                          List<LineaDevolucion> lineas) {
        try {
            return execute(em -> {
                // Se bloquea la venta para que dos devoluciones de la misma venta no se crucen.
                Venta venta = em.find(Venta.class, idVenta, LockModeType.PESSIMISTIC_WRITE);
                if (venta == null) {
                    throw new IllegalArgumentException("La venta ya no existe.");
                }
                if (!ReporteDAO.COMPLETADA.equals(venta.getEstado())) {
                    throw new IllegalArgumentException("La venta " + idVenta + " está en estado "
                            + venta.getEstado() + " y no admite devoluciones.");
                }

                Map<Integer, Long> yaDevueltas = devueltasPorDetalle(em, idVenta);
                BigDecimal monto = BigDecimal.ZERO;
                List<DetalleDevolucion> filas = new ArrayList<>();
                Set<Integer> renglones = new HashSet<>();

                for (LineaDevolucion linea : lineas) {
                    if (!renglones.add(linea.getIdDetalle())) {
                        throw new IllegalArgumentException("Un renglón de la venta viene repetido.");
                    }
                    DetalleVenta detalle = em.find(DetalleVenta.class, linea.getIdDetalle());
                    if (detalle == null || !detalle.getIdVenta().getId().equals(idVenta)) {
                        throw new IllegalArgumentException("Un renglón no pertenece a la venta " + idVenta + ".");
                    }
                    long devuelta = yaDevueltas.getOrDefault(detalle.getId(), 0L);
                    long disponible = detalle.getCantidad() - devuelta;
                    if (linea.getCantidad() > disponible) {
                        throw new IllegalArgumentException("Solo se pueden devolver " + disponible
                                + " pieza(s) de " + detalle.getIdProducto().getNombre() + " en esta venta.");
                    }
                    monto = monto.add(detalle.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(linea.getCantidad())));
                    filas.addAll(repartirPorLote(em, detalle, linea));
                }

                Instant ahora = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Usuario usuario = em.getReference(Usuario.class, idUsuario);

                Devolucion devolucion = new Devolucion();
                devolucion.setIdVenta(venta);
                devolucion.setIdUsuario(usuario);
                devolucion.setFechaHora(ahora);
                devolucion.setMontoReembolsado(monto);
                devolucion.setMotivo(motivo);
                em.persist(devolucion);
                em.flush();

                for (DetalleDevolucion fila : filas) {
                    fila.setIdDevolucion(devolucion);
                    em.persist(fila);

                    Lote lote = fila.getIdLote();
                    // Si dos renglones comparten lote, se guarda primero el cambio anterior para que el
                    // refresh no lo descarte.
                    em.flush();
                    em.refresh(lote, LockModeType.PESSIMISTIC_WRITE);
                    if (Boolean.TRUE.equals(fila.getRevendible())) {
                        lote.setCantidadDisponible(lote.getCantidadDisponible() + fila.getCantidad());
                    } else {
                        Merma merma = new Merma();
                        merma.setIdLote(lote);
                        merma.setIdUsuario(usuario);
                        merma.setIdDevolucion(devolucion.getId());
                        merma.setFechaHora(ahora);
                        merma.setCantidad(fila.getCantidad());
                        merma.setMotivo(MOTIVO_MERMA);
                        merma.setCostoUnitario(lote.getCostoUnitario());
                        merma.setObservaciones(motivo);
                        em.persist(merma);
                    }
                }
                return devolucion;
            });
        } catch (RuntimeException ex) {
            // Tras un rollback se descartan los objetos en memoria para no arrastrar cambios a medias.
            entityManager.clear();
            throw ex;
        }
    }

    /**
     * Reparte las piezas de una línea entre los lotes de los que salieron al venderse (del más antiguo
     * al más reciente), descontando lo que ya se devolvió de cada lote.
     */
    private List<DetalleDevolucion> repartirPorLote(EntityManager em, DetalleVenta detalle, LineaDevolucion linea) {
        List<DetalleVentaLote> origen = em.createQuery(
                        "SELECT dvl FROM DetalleVentaLote dvl JOIN FETCH dvl.idLote "
                                + "WHERE dvl.id.idDetalle = :id ORDER BY dvl.id.idLote", DetalleVentaLote.class)
                .setParameter("id", detalle.getId())
                .getResultList();
        if (origen.isEmpty()) {
            throw new IllegalArgumentException("No hay registro de los lotes de los que salió "
                    + detalle.getIdProducto().getNombre() + " en esta venta; no se puede devolver.");
        }

        Map<Integer, Long> devueltoPorLote = new HashMap<>();
        List<Object[]> devueltos = em.createQuery(
                        "SELECT dd.idLote.id, SUM(dd.cantidad) FROM DetalleDevolucion dd "
                                + "WHERE dd.idDetalle.id = :id GROUP BY dd.idLote.id", Object[].class)
                .setParameter("id", detalle.getId())
                .getResultList();
        for (Object[] d : devueltos) {
            devueltoPorLote.put((Integer) d[0], ((Number) d[1]).longValue());
        }

        List<DetalleDevolucion> filas = new ArrayList<>();
        int restante = linea.getCantidad();
        for (DetalleVentaLote dvl : origen) {
            if (restante == 0) {
                break;
            }
            long libres = dvl.getCantidad() - devueltoPorLote.getOrDefault(dvl.getIdLote().getId(), 0L);
            int tomar = (int) Math.min(restante, libres);
            if (tomar <= 0) {
                continue;
            }
            DetalleDevolucion fila = new DetalleDevolucion();
            fila.setIdDetalle(detalle);
            fila.setIdLote(dvl.getIdLote());
            fila.setCantidad(tomar);
            fila.setRevendible(linea.isRevendible());
            filas.add(fila);
            restante -= tomar;
        }
        if (restante > 0) {
            throw new IllegalArgumentException("No se pudieron ubicar " + restante + " pieza(s) de "
                    + detalle.getIdProducto().getNombre() + " en los lotes de la venta.");
        }
        return filas;
    }

    private static Map<Integer, Long> devueltasPorDetalle(EntityManager em, Integer idVenta) {
        List<Object[]> filas = em.createQuery(
                        "SELECT dd.idDetalle.id, SUM(dd.cantidad) FROM DetalleDevolucion dd "
                                + "WHERE dd.idDetalle.idVenta.id = :id GROUP BY dd.idDetalle.id", Object[].class)
                .setParameter("id", idVenta)
                .getResultList();
        Map<Integer, Long> mapa = new HashMap<>();
        for (Object[] f : filas) {
            mapa.put((Integer) f[0], ((Number) f[1]).longValue());
        }
        return mapa;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}