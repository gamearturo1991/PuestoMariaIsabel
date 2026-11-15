package helper;

import Puesto.maria.isabel.facade.FacadeProducto;
import Puesto.maria.isabel.facade.FacadeVenta;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.DetalleVenta;
import puesto.maria.entity.Producto;

import java.io.Serializable;
import java.util.List;

public class VentaHelper implements Serializable {

    private FacadeVenta facadeVenta() {
        return ServiceFacadeLocator.getInstanceFacadeVenta();
    }

    private FacadeProducto facadeProducto() {
        return ServiceFacadeLocator.getInstanceFacadeProducto();
    }

    public void procesarVenta(List<DetalleVenta> detalles) {
        facadeVenta().procesarVenta(detalles);
    }

    public List<Producto> buscarProductos(String texto, boolean soloActivos, int max) {
        return facadeProducto().buscarProductos(texto, soloActivos, max);
    }
}
