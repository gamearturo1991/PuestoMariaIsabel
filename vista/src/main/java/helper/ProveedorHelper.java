package helper;

import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.Proveedor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProveedorHelper implements Serializable {

    public List<Proveedor> listar() {
        return ServiceFacadeLocator.getInstanceFacadeProveedor().listarProveedores();
    }

    public Map<Integer, Long> productosPorProveedor() {
        return ServiceFacadeLocator.getInstanceFacadeProveedor().productosPorProveedor();
    }

    public void guardar(Proveedor proveedor) {
        ServiceFacadeLocator.getInstanceFacadeProveedor().saveProveedor(proveedor);
    }

    public void modificar(Proveedor proveedor) {
        ServiceFacadeLocator.getInstanceFacadeProveedor().updateProveedor(proveedor);
    }

    public void cambiarEstado(Proveedor proveedor) {
        ServiceFacadeLocator.getInstanceFacadeProveedor().cambiarEstado(proveedor);
    }

    public void eliminar(Proveedor proveedor) {
        ServiceFacadeLocator.getInstanceFacadeProveedor().eliminarProveedor(proveedor);
    }
}
