package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.CorteCaja;
import puesto.maria.entity.Usuario;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class CorteCajaDAO extends AbstractDAO<CorteCaja> {
    private final EntityManager entityManager;

    public CorteCajaDAO(EntityManager em) {
        super(CorteCaja.class);
        this.entityManager = em;
    }

    public CorteCaja buscarPorFecha(LocalDate fecha) {
        List<CorteCaja> resultado = execute(em -> em.createQuery(
                        "SELECT c FROM CorteCaja c WHERE c.fecha = :fecha", CorteCaja.class)
                .setParameter("fecha", fecha)
                .getResultList());
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public List<CorteCaja> listarPorRango(LocalDate desde, LocalDate hasta) {
        return execute(em -> em.createQuery(
                        "SELECT c FROM CorteCaja c WHERE c.fecha BETWEEN :desde AND :hasta ORDER BY c.fecha",
                        CorteCaja.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    public void guardarCorte(Integer idUsuario, CorteCaja corte) {
        execute(em -> {
            Usuario user = em.find(Usuario.class, idUsuario);
            if (user == null) {
                throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
            }
            corte.setIdUsuario(user);
            em.persist(corte);
            return null;
        });
    }

    public List<Object[]> sumarPagosPorMetodo(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT p.metodo, SUM(p.monto), SUM(p.comision) " +
                                "FROM PagoVenta p JOIN p.idVenta v " +
                                "WHERE v.estado = :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta " +
                                "GROUP BY p.metodo", Object[].class)
                .setParameter("estado", ReporteDAO.COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }


    public BigDecimal sumarDevoluciones(Instant desde, Instant hasta) {
        BigDecimal total = execute(em -> em.createQuery(
                        "SELECT SUM(d.montoReembolsado) FROM Devolucion d " +
                                "WHERE d.fechaHora >= :desde AND d.fechaHora < :hasta", BigDecimal.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult());
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}