package helper;

import Puesto.maria.isabel.delegate.ResultadoEntrada;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.Producto;
import puesto.maria.persistence.dao.LineaEntrada;
import puesto.maria.entity.Proveedor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Puente entre EntradaInventarioBeanUI y las fachadas de entradas y productos.
 */
public class EntradaInventarioHelper implements Serializable {

    public List<Proveedor> proveedores() {
        return ServiceFacadeLocator.getInstanceFacadeProducto().listarProveedores();
    }

    /** Busca productos activos y deshabilitados: una entrada puede reactivar un producto. */
    public List<Producto> buscarProductos(String texto, int max) {
        return ServiceFacadeLocator.getInstanceFacadeProducto().buscarProductos(texto, false, max);
    }

    /** Productos (activos y deshabilitados) que surte un proveedor. */
    public List<Producto> productosDeProveedor(Integer idProveedor) {
        return ServiceFacadeLocator.getInstanceFacadeProducto().productosDeProveedor(idProveedor);
    }

    /** Existencia de todos los productos en una sola consulta (id -> piezas). */
    public Map<Integer, Long> existencias() {
        return ServiceFacadeLocator.getInstanceFacadeProducto().existencias();
    }

    public List<ResultadoEntrada> registrarRecepcion(Integer idProveedor, List<LineaEntrada> lineas,
                                                     Integer idUsuario, String observaciones) {
        return ServiceFacadeLocator.getInstanceFacadeEntradaInventario().registrarRecepcion(
                idProveedor, lineas, idUsuario, observaciones);
    }

    public Producto obtenerProducto(Integer id) {
        return ServiceFacadeLocator.getInstanceFacadeProducto().obtenerProducto(id);
    }

    public BigDecimal precioSugerido(Producto producto, BigDecimal costoNuevo) {
        return ServiceFacadeLocator.getInstanceFacadeProducto().calcularPrecioSugerido(producto, costoNuevo);
    }

    public int existencia(Integer idProducto) {
        return ServiceFacadeLocator.getInstanceFacadeEntradaInventario().consultarExistencia(idProducto);
    }

    public ResultadoEntrada registrar(Integer idProveedor, Integer idProducto, Integer cantidad,
                                      BigDecimal costo, BigDecimal precioVenta,
                                      Integer idUsuario, String observaciones) {
        return ServiceFacadeLocator.getInstanceFacadeEntradaInventario().registrarEntrada(
                idProveedor, idProducto, cantidad, costo, precioVenta, idUsuario, observaciones);
    }
}
