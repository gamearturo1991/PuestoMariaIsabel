package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import puesto.maria.entity.Producto;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.ArrayList;
import java.util.List;

public class ProductoDAO extends AbstractDAO<Producto> {
    private final EntityManager entityManager;

    public ProductoDAO(EntityManager em) {
        super(Producto.class);
        this.entityManager = em;
    }

    /** Todos los productos (activos y deshabilitados) ordenados por nombre. */
    public List<Producto> listarTodos() {
        return execute(em -> em
                .createQuery("SELECT p FROM Producto p ORDER BY p.nombre", Producto.class)
                .getResultList());
    }

    /** Busca un producto por su código corto exacto (sin distinguir mayúsculas). */
    public Producto buscarPorCodigo(String codigo, boolean soloActivos) {
        String jpql = "SELECT p FROM Producto p WHERE LOWER(p.codigoCorto) = :codigo"
                + (soloActivos ? " AND p.activo = true" : "");
        return execute(em -> {
            try {
                return em.createQuery(jpql, Producto.class)
                        .setParameter("codigo", codigo.toLowerCase())
                        .setMaxResults(1)
                        .getSingleResult();
            } catch (NoResultException ex) {
                return null;
            }
        });
    }

    /** True si ya existe otro producto con ese código (se ignora el producto con idExcluido). */
    public boolean existeCodigo(String codigo, Integer idExcluido) {
        Producto existente = buscarPorCodigo(codigo, false);
        return existente != null && !existente.getId().equals(idExcluido);
    }

    /**
     * Búsqueda para la barra de búsqueda: el código exacto va primero y después
     * los productos cuyo nombre o código contienen el texto.
     *
     * @param texto        texto escrito (desde el primer carácter)
     * @param soloActivos  true en ventas y productos; false en entradas (permite reactivar)
     * @param max          máximo de resultados
     */
    public List<Producto> buscar(String texto, boolean soloActivos, int max) {
        List<Producto> resultado = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return resultado;
        }
        String t = texto.trim();

        Producto exacto = buscarPorCodigo(t, soloActivos);
        if (exacto != null) {
            resultado.add(exacto);
        }

        String patron = "%" + t.toLowerCase().replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
        String jpql = "SELECT p FROM Producto p WHERE (LOWER(p.nombre) LIKE :patron ESCAPE '!' "
                + "OR LOWER(p.codigoCorto) LIKE :patron ESCAPE '!')"
                + (soloActivos ? " AND p.activo = true" : "")
                + " ORDER BY p.nombre";
        List<Producto> coincidencias = execute(em -> em
                .createQuery(jpql, Producto.class)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList());

        for (Producto p : coincidencias) {
            if (resultado.size() >= max) {
                break;
            }
            if (!resultado.contains(p)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
