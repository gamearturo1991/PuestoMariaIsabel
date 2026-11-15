package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.EntradaInventario;
import puesto.maria.entity.Lote;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Proveedor;
import puesto.maria.entity.Usuario;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EntradaInventarioDAO extends AbstractDAO<EntradaInventario> {
    private final EntityManager entityManager;

    public EntradaInventarioDAO(EntityManager em) {
        super(EntradaInventario.class);
        this.entityManager = em;
    }

    /**
     * Registra una entrada de mercancía en UNA sola transacción (todo o nada): crea la entrada y, por cada
     * línea, un lote con su costo y la actualización del producto (costo, precio de venta y reactivación
     * si estaba deshabilitado).
     *
     * @return los productos ya actualizados, en el mismo orden que las líneas
     */
    public List<Producto> registrarEntrada(Integer idProveedor, Integer idUsuario,
                                           List<LineaEntrada> lineas, String observaciones) {
        try {
            return execute(em -> {
                EntradaInventario entrada = new EntradaInventario();
                entrada.setIdProveedor(em.getReference(Proveedor.class, idProveedor));
                entrada.setIdUsuario(em.getReference(Usuario.class, idUsuario));
                entrada.setFechaHora(Instant.now());
                entrada.setObservaciones(observaciones);
                em.persist(entrada);

                List<Producto> actualizados = new ArrayList<>();
                for (LineaEntrada l : lineas) {
                    Producto producto = em.find(Producto.class, l.getIdProducto());
                    if (producto == null) {
                        throw new IllegalArgumentException("Un producto de la entrada ya no existe.");
                    }
                    Lote lote = new Lote();
                    lote.setIdEntrada(entrada);
                    lote.setIdProducto(producto);
                    lote.setCantidadRecibida(l.getCantidad());
                    lote.setCantidadDisponible(l.getCantidad());
                    lote.setCostoUnitario(l.getCosto());
                    em.persist(lote);

                    producto.setCosto(l.getCosto());
                    producto.setPrecioVenta(l.getPrecioVenta());
                    producto.setActivo(true);
                    actualizados.add(producto);
                }
                return actualizados;
            });
        } catch (RuntimeException ex) {
            // Tras un rollback se descartan los objetos en memoria para no arrastrar cambios a medias.
            entityManager.clear();
            throw ex;
        }
    }

    /** Existencia total de un producto: suma de la cantidad disponible de sus lotes. */
    public int existenciaTotal(Integer idProducto) {
        Long total = execute(em -> em
                .createQuery("SELECT COALESCE(SUM(l.cantidadDisponible), 0) FROM Lote l WHERE l.idProducto.id = :id",
                        Long.class)
                .setParameter("id", idProducto)
                .getSingleResult());
        return Math.toIntExact(total);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
