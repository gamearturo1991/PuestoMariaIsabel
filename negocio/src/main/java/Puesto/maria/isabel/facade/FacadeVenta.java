package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateVenta;
import puesto.maria.entity.*;

import java.util.List;

public class FacadeVenta {
    private final DelegateVenta delegate = new DelegateVenta();

    public Integer obtenerSiguienteFolio() { return delegate.obtenerSiguienteFolio(); }
    public List<Cliente> listarClientes() { return delegate.listarClientes(); }

    public void registrarVenta(Venta venta, List<DetalleVenta> detalles, List<PagoVenta> pagos) {
        delegate.registrarVenta(venta, detalles, pagos);
    }
}