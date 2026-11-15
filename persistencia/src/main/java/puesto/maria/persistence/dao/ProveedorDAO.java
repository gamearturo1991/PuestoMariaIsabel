package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Proveedor;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

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

    /** Todos los proveedores (activos e inactivos) ordenados por nombre. */
    public List<Proveedor> listarTodos() {
        return execute(em -> em
                .createQuery("SELECT p FROM Proveedor p ORDER BY p.nombre", Proveedor.class)
                .getResultList());
    }

    /** True si existe otro proveedor con ese nombre (sin distinguir mayúsculas). */
    public boolean existeNombre(String nombre, Integer idExcluido) {
        Long n = execute(em -> em
                .createQuery("SELECT COUNT(p) FROM Proveedor p WHERE LOWER(p.nombre) = :nombre AND p.id <> :id", Long.class)
                .setParameter("nombre", nombre.toLowerCase())
                .setParameter("id", idExcluido == null ? -1 : idExcluido)
                .getSingleResult());
        return n > 0;
    }

    /** Productos de un proveedor (activos y deshabilitados). */
    public long contarProductos(Integer idProveedor) {
        return execute(em -> em
                .createQuery("SELECT COUNT(p) FROM Producto p WHERE p.idProveedor.id = :id", Long.class)
                .setParameter("id", idProveedor)
                .getSingleResult());
    }

    /** Entradas de mercancía de un proveedor. */
    public long contarEntradas(Integer idProveedor) {
        return execute(em -> em
                .createQuery("SELECT COUNT(e) FROM EntradaInventario e WHERE e.idProveedor.id = :id", Long.class)
                .setParameter("id", idProveedor)
                .getSingleResult());
    }

    /** Productos por proveedor en una sola consulta: id de proveedor -> cantidad. */
    public Map<Integer, Long> productosPorProveedor() {
        return execute(em -> {
            Map<Integer, Long> mapa = new HashMap<>();
            List<Object[]> filas = em.createQuery(
                    "SELECT p.idProveedor.id, COUNT(p) FROM Producto p GROUP BY p.idProveedor.id",
                    Object[].class).getResultList();
            for (Object[] f : filas) {
                mapa.put((Integer) f[0], ((Number) f[1]).longValue());
            }
            return mapa;
        });
    }

    public void eliminarPorId(Integer idProveedor) {
        execute(em -> {
            Proveedor p = em.find(Proveedor.class, idProveedor);
            if (p != null) {
                em.remove(p);
            }
            return null;
        });
    }


    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
