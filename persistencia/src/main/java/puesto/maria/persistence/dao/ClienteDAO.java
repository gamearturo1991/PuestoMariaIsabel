package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Bitacora;
import puesto.maria.entity.Cliente;
import puesto.maria.entity.FacturacionCliente;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ClienteDAO extends AbstractDAO<Cliente> {
    private final EntityManager entityManager;

    public ClienteDAO(EntityManager em) {
        super(Cliente.class);
        this.entityManager = em;
    }

    /** Clientes activos ordenados por nombre (los que se pueden elegir en una venta). */
    public List<Cliente> listarActivos() {
        return execute(em -> em
                .createQuery("SELECT c FROM Cliente c WHERE c.activo = true ORDER BY c.nombre", Cliente.class)
                .getResultList());
    }

    /** Todos los clientes (activos e inactivos) ordenados por nombre. */
    public List<Cliente> listarTodos() {
        return execute(em -> em
                .createQuery("SELECT c FROM Cliente c ORDER BY c.nombre", Cliente.class)
                .getResultList());
    }

    /** True si existe otro cliente con ese RFC. */
    public boolean existeRfc(String rfc, Integer idExcluido) {
        Long n = execute(em -> em
                .createQuery("SELECT COUNT(c) FROM Cliente c WHERE c.rfc = :rfc AND c.id <> :id", Long.class)
                .setParameter("rfc", rfc)
                .setParameter("id", idExcluido == null ? -1 : idExcluido)
                .getSingleResult());
        return n > 0;
    }

    /** Altas y modificaciones de un cliente en la bitácora, de la más antigua a la más reciente. */
    public List<Bitacora> movimientos(Integer idCliente) {
        return execute(em -> em
                .createQuery("SELECT b FROM Bitacora b WHERE b.tablaAfectada = 'cliente' AND b.idRegistro = :id "
                        + "AND b.accion IN ('CREAR', 'MODIFICAR') ORDER BY b.fechaHora ASC, b.id ASC", Bitacora.class)
                .setParameter("id", idCliente)
                .getResultList());
    }

    /** Compras completadas del periodo por cliente. Columnas: idCliente, número de tickets, suma de totales. */
    public List<Object[]> totalesPorCliente(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT v.idCliente.id, COUNT(v), SUM(v.total) FROM Venta v "
                                + "WHERE v.estado = 'COMPLETADA' AND v.idCliente IS NOT NULL "
                                + "AND v.fechaHora >= :desde AND v.fechaHora < :hasta GROUP BY v.idCliente.id",
                        Object[].class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /** Total de compras completadas de un cliente en el periodo (0 si no compró). */
    public BigDecimal totalDeCliente(Integer idCliente, Instant desde, Instant hasta) {
        BigDecimal t = execute(em -> em.createQuery(
                        "SELECT SUM(v.total) FROM Venta v WHERE v.estado = 'COMPLETADA' AND v.idCliente.id = :id "
                                + "AND v.fechaHora >= :desde AND v.fechaHora < :hasta", BigDecimal.class)
                .setParameter("id", idCliente)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult());
        return t == null ? BigDecimal.ZERO : t;
    }

    /** Meses marcados como facturados en el año y mes indicados. */
    public List<FacturacionCliente> facturacionesDelMes(int anio, int mes) {
        return execute(em -> em.createQuery(
                        "SELECT f FROM FacturacionCliente f JOIN FETCH f.idCliente WHERE f.anio = :anio AND f.mes = :mes",
                        FacturacionCliente.class)
                .setParameter("anio", anio)
                .setParameter("mes", mes)
                .getResultList());
    }

    /** La facturación de un cliente en un mes, o null si no está facturado. */
    public FacturacionCliente buscarFacturacion(Integer idCliente, int anio, int mes) {
        List<FacturacionCliente> r = execute(em -> em.createQuery(
                        "SELECT f FROM FacturacionCliente f WHERE f.idCliente.id = :id AND f.anio = :anio AND f.mes = :mes",
                        FacturacionCliente.class)
                .setParameter("id", idCliente)
                .setParameter("anio", anio)
                .setParameter("mes", mes)
                .getResultList());
        return r.isEmpty() ? null : r.get(0);
    }

    public void guardarFacturacion(FacturacionCliente f) {
        execute(em -> {
            em.persist(f);
            return null;
        });
    }

    public void eliminarFacturacion(Integer idFacturacion) {
        execute(em -> {
            FacturacionCliente f = em.find(FacturacionCliente.class, idFacturacion);
            if (f != null) {
                em.remove(f);
            }
            return null;
        });
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
