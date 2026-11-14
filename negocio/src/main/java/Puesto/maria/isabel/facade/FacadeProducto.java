package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateProducto;
import puesto.maria.entity.Categoria;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Proveedor;

import java.math.BigDecimal;
import java.util.List;

public class FacadeProducto {

    private final DelegateProducto delegateProducto;

    public FacadeProducto() {
        this.delegateProducto = new DelegateProducto();
    }

    public void saveProducto(Producto producto) {
        delegateProducto.saveProducto(producto);
    }

    public void updateProducto(Producto producto) {
        delegateProducto.updateProducto(producto);
    }

    public void deshabilitarProducto(Producto producto) {
        delegateProducto.deshabilitarProducto(producto);
    }

    public List<Producto> listarProductos() {
        return delegateProducto.listarProductos();
    }

    public List<Producto> buscarProductos(String texto, boolean soloActivos, int max) {
        return delegateProducto.buscarProductos(texto, soloActivos, max);
    }

    public List<Categoria> listarCategorias() {
        return delegateProducto.listarCategorias();
    }

    public List<Proveedor> listarProveedores() {
        return delegateProducto.listarProveedores();
    }

    public Categoria obtenerCategoria(Integer id) {
        return delegateProducto.obtenerCategoria(id);
    }

    public Proveedor obtenerProveedor(Integer id) {
        return delegateProducto.obtenerProveedor(id);
    }

    public BigDecimal calcularPrecioSugerido(Producto producto, BigDecimal costoNuevo) {
        return delegateProducto.calcularPrecioSugerido(producto, costoNuevo);
    }
}
