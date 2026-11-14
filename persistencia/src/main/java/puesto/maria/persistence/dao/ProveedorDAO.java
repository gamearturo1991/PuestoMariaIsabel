package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Proveedor;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.List;

public class ProveedorDAO extends AbstractDAO<Proveedor> {
    private final EntityManager entityManager;

    public ProveedorDAO(EntityManager em) {
        super(Proveedor.class);
        this.entityManager = em;
    }

    /** Proveedores activos ordenados por nombre. */
    public List<Proveedor> listarActivos() {
        return execute(em -> em
                .createQuery("SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.nombre", Proveedor.class)
                .getResultList());
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
