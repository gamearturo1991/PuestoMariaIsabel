package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Cliente;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.List;

public class ClienteDAO extends AbstractDAO<Cliente> {
    private final EntityManager entityManager;

    public ClienteDAO(EntityManager em) {
        super(Cliente.class);
        this.entityManager = em;
    }

    public List<Cliente> listarActivos() {
        return execute(em -> em
                .createQuery("SELECT c FROM Cliente c ORDER BY c.nombre", Cliente.class)
                .getResultList());
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
