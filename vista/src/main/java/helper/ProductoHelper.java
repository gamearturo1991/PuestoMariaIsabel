package helper;

import Puesto.maria.isabel.facade.FacadeProducto;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.Categoria;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Proveedor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Puente entre ProductoBeanUI y la fachada de productos.
 */
public class ProductoHelper implements Serializable {

    private FacadeProducto facade() {
        return ServiceFacadeLocator.getInstanceFacadeProducto();
    }

    public void guardar(Producto producto) {
        facade().saveProducto(producto);
    }

    public void modificar(Producto producto) {
        facade().updateProducto(producto);
    }

    public void deshabilitar(Producto producto) {
        facade().deshabilitarProducto(producto);
    }

    public List<Producto> listar() {
        return facade().listarProductos();
    }

    public List<Producto> buscar(String texto, boolean soloActivos, int max) {
        return facade().buscarProductos(texto, soloActivos, max);
    }

    public List<Categoria> categorias() {
        return facade().listarCategorias();
    }

    public List<Proveedor> proveedores() {
        return facade().listarProveedores();
    }

    public Categoria categoria(Integer id) {
        return facade().obtenerCategoria(id);
    }

    public Proveedor proveedor(Integer id) {
        return facade().obtenerProveedor(id);
    }

    public BigDecimal precioSugerido(Producto producto, BigDecimal costoNuevo) {
        return facade().calcularPrecioSugerido(producto, costoNuevo);
    }
}
