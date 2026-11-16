package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.*;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.List;

public class VentaDAO extends AbstractDAO<Venta> {
    private final EntityManager entityManager;

    public VentaDAO(EntityManager em) {
        super(Venta.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Integer obtenerSiguienteFolio() {
        return execute(em -> {
            Integer maxId = em.createQuery("SELECT MAX(v.id) FROM Venta v", Integer.class).getSingleResult();
            return (maxId == null) ? 1 : maxId + 1;
        });
    }

    public void registrarVentaCompleta(Venta venta, List<DetalleVenta> detalles, List<PagoVenta> pagos) {
        execute(em -> {
            // 1. Guardar Venta
            em.persist(venta);
            em.flush();

            for (DetalleVenta detalle : detalles) {
                detalle.setIdVenta(venta);
                em.persist(detalle);
                em.flush();

                List<Lote> lotes = em.createQuery(
                                "SELECT l FROM Lote l WHERE l.idProducto.id = :id AND l.cantidadDisponible > 0 " +
                                        "ORDER BY l.idEntrada.fechaHora ASC, l.id ASC", Lote.class)
                        .setParameter("id", detalle.getIdProducto().getId())
                        .getResultList();

                int restante = detalle.getCantidad();
                for (Lote lote : lotes) {
                    if (restante <= 0) break;

                    int disponible = lote.getCantidadDisponible();
                    int aTomar = Math.min(disponible, restante);

                    lote.setCantidadDisponible(disponible - aTomar);
                    em.merge(lote);

                    DetalleVentaLoteId dvlId = new DetalleVentaLoteId();
                    dvlId.setIdDetalle(detalle.getId());
                    dvlId.setIdLote(lote.getId());

                    DetalleVentaLote dvl = new DetalleVentaLote();
                    dvl.setId(dvlId);
                    dvl.setIdDetalle(detalle);
                    dvl.setIdLote(lote);
                    dvl.setCantidad(aTomar);
                    em.persist(dvl);

                    restante -= aTomar;
                }

                if (restante > 0) {
                    throw new IllegalArgumentException("Stock insuficiente para: " + detalle.getIdProducto().getNombre());
                }
            }

            for (PagoVenta pago : pagos) {
                pago.setIdVenta(venta);
                em.persist(pago);
            }
            return null;
        });
    }
}