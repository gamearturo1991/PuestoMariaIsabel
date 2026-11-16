package helper;

import Puesto.maria.isabel.facade.FacadeProducto;
import Puesto.maria.isabel.facade.FacadeVenta;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VentaHelper implements Serializable {

    private FacadeVenta facadeVenta() { return ServiceFacadeLocator.getInstanceFacadeVenta(); }
    private FacadeProducto facadeProducto() { return ServiceFacadeLocator.getInstanceFacadeProducto(); }

    public void registrarVenta(Venta venta, List<DetalleVenta> detalles, List<PagoVenta> pagos) {
        facadeVenta().registrarVenta(venta, detalles, pagos);
    }

    public List<Producto> buscarProductos(String texto, boolean soloActivos, int max) {
        return facadeProducto().buscarProductos(texto, soloActivos, max);
    }

    public List<Cliente> listarClientes() {
        return facadeVenta().listarClientes();
    }

    public Integer obtenerSiguienteFolio() {
        return facadeVenta().obtenerSiguienteFolio();
    }

    public Map<Integer, Long> existencias() {
        return facadeProducto().existencias();
    }
}