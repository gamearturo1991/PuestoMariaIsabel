package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Categoria;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** True si existe otra categoría con ese nombre (sin distinguir mayúsculas). */
    public boolean existeNombre(String nombre, Integer idExcluido) {
        Long n = execute(em -> em
                .createQuery("SELECT COUNT(c) FROM Categoria c WHERE LOWER(c.nombre) = :nombre"
                        + (idExcluido == null ? "" : " AND c.id <> :id"), Long.class)
                .setParameter("nombre", nombre.toLowerCase())
                .setParameter("id", idExcluido == null ? -1 : idExcluido)
                .getSingleResult());
        return n > 0;
    }

    /** Cantidad de productos (activos y deshabilitados) de una categoría. */
    public long contarProductos(Integer idCategoria) {
        return execute(em -> em
                .createQuery("SELECT COUNT(p) FROM Producto p WHERE p.idCategoria.id = :id", Long.class)
                .setParameter("id", idCategoria)
                .getSingleResult());
    }

    /** Productos por categoría en una sola consulta: id de categoría -> cantidad. */
    public Map<Integer, Long> productosPorCategoria() {
        return execute(em -> {
            Map<Integer, Long> mapa = new HashMap<>();
            List<Object[]> filas = em.createQuery(
                    "SELECT p.idCategoria.id, COUNT(p) FROM Producto p GROUP BY p.idCategoria.id",
                    Object[].class).getResultList();
            for (Object[] f : filas) {
                mapa.put((Integer) f[0], ((Number) f[1]).longValue());
            }
            return mapa;
        });
    }

    /** Elimina una categoría por id. */
    public void eliminarPorId(Integer idCategoria) {
        execute(em -> {
            Categoria c = em.find(Categoria.class, idCategoria);
            if (c != null) {
                em.remove(c);
            }
            return null;
        });
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
