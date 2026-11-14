package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Categoria;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.List;

public class CategoriaDAO extends AbstractDAO<Categoria> {
    private final EntityManager entityManager;

    public CategoriaDAO(EntityManager em) {
        super(Categoria.class);
        this.entityManager = em;
    }

    /** Todas las categorías ordenadas por nombre. */
    public List<Categoria> listarOrdenadas() {
        return execute(em -> em
                .createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class)
                .getResultList());
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
