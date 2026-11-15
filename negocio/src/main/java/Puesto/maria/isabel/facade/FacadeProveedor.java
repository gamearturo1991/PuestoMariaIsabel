package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateProveedor;
import puesto.maria.entity.Proveedor;

import java.util.List;
import java.util.Map;

public class FacadeProveedor {

    private final DelegateProveedor delegateProveedor = new DelegateProveedor();

    public List<Proveedor> listarProveedores() {
        return delegateProveedor.listarProveedores();
    }

    public Map<Integer, Long> productosPorProveedor() {
        return delegateProveedor.productosPorProveedor();
    }

    public void saveProveedor(Proveedor proveedor) {
        delegateProveedor.saveProveedor(proveedor);
    }

    public void updateProveedor(Proveedor proveedor) {
        delegateProveedor.updateProveedor(proveedor);
    }

    public void cambiarEstado(Proveedor proveedor) {
        delegateProveedor.cambiarEstado(proveedor);
    }

    public void eliminarProveedor(Proveedor proveedor) {
        delegateProveedor.eliminarProveedor(proveedor);
    }
}