package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Lote;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.List;

public class LoteDAO extends AbstractDAO<Lote> {
    private final EntityManager entityManager;

    public LoteDAO(EntityManager em) {
        super(Lote.class);
        this.entityManager = em;
    }


    public void descontarStock(Integer idProducto, Integer cantidad) {
        execute(em -> {

            List<Lote> lotes = em.createQuery(
                            "SELECT l FROM Lote l WHERE l.idProducto.id = :id AND l.cantidadDisponible > 0 " +
                                    "ORDER BY l.idEntrada.fechaHora ASC, l.id ASC", Lote.class)
                    .setParameter("id", idProducto)
                    .getResultList();

            int restante = cantidad;
            for (Lote lote : lotes) {
                if (restante <= 0) break;

                int disponible = lote.getCantidadDisponible();
                if (disponible >= restante) {
                    lote.setCantidadDisponible(disponible - restante);
                    restante = 0;
                } else {
                    lote.setCantidadDisponible(0);
                    restante -= disponible;
                }
            }

            if (restante > 0) {
                throw new IllegalArgumentException("No hay suficiente stock para el producto: " + idProducto + ". Faltan " + restante + " piezas.");
            }
            return null;
        });
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}