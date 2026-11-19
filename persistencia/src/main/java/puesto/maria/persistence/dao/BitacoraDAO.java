package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import puesto.maria.entity.Bitacora;
import puesto.maria.entity.Usuario;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Acceso a la tabla bitacora. Solo se inserta y se consulta: la bitácora nunca se edita ni se borra. */
public class BitacoraDAO extends AbstractDAO<Bitacora> {

    private final EntityManager entityManager;

    public BitacoraDAO(EntityManager em) {
        super(Bitacora.class);
        this.entityManager = em;
    }

    /** Inserta un movimiento. La fecha y hora la pone el sistema en este momento. */
    public void registrar(Integer idUsuario, String accion, String tabla, Integer idRegistro,
                          String valorAnterior, String valorNuevo, String motivo) {
        try {
            execute(em -> {
                Bitacora b = new Bitacora();
                b.setIdUsuario(em.getReference(Usuario.class, idUsuario));
                b.setFechaHora(Instant.now().truncatedTo(ChronoUnit.SECONDS));
                b.setAccion(accion);
                b.setTablaAfectada(tabla);
                b.setIdRegistro(idRegistro);
                b.setValorAnterior(valorAnterior);
                b.setValorNuevo(valorNuevo);
                b.setMotivo(motivo);
                em.persist(b);
                return null;
            });
        } catch (RuntimeException ex) {
            // Un registro fallido no debe quedar pendiente en el EntityManager.
            entityManager.clear();
            throw ex;
        }
    }

    /**
     * Movimientos del periodo [desde, hasta), los más recientes primero. Los filtros en null no se aplican.
     * Se limita a {@code max} filas para que la pantalla no se cargue con miles de registros.
     */
    public List<Bitacora> consultar(Instant desde, Instant hasta, Integer idUsuario, String accion,
                                    String tabla, int max) {
        StringBuilder jpql = new StringBuilder(
                "SELECT b FROM Bitacora b JOIN FETCH b.idUsuario u "
                        + "WHERE b.fechaHora >= :desde AND b.fechaHora < :hasta");
        if (idUsuario != null) {
            jpql.append(" AND u.id = :usuario");
        }
        if (accion != null) {
            jpql.append(" AND b.accion = :accion");
        }
        if (tabla != null) {
            jpql.append(" AND b.tablaAfectada = :tabla");
        }
        jpql.append(" ORDER BY b.fechaHora DESC, b.id DESC");

        return execute(em -> {
            TypedQuery<Bitacora> q = em.createQuery(jpql.toString(), Bitacora.class)
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta);
            if (idUsuario != null) {
                q.setParameter("usuario", idUsuario);
            }
            if (accion != null) {
                q.setParameter("accion", accion);
            }
            if (tabla != null) {
                q.setParameter("tabla", tabla);
            }
            return q.setMaxResults(max).getResultList();
        });
    }

    /** Acciones que ya aparecen en la bitácora (para el filtro de la pantalla). */
    public List<String> accionesRegistradas() {
        return execute(em -> em.createQuery(
                "SELECT DISTINCT b.accion FROM Bitacora b ORDER BY b.accion", String.class).getResultList());
    }

    /** Tablas que ya aparecen en la bitácora (para el filtro de la pantalla). */
    public List<String> tablasRegistradas() {
        return execute(em -> em.createQuery(
                "SELECT DISTINCT b.tablaAfectada FROM Bitacora b ORDER BY b.tablaAfectada",
                String.class).getResultList());
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
