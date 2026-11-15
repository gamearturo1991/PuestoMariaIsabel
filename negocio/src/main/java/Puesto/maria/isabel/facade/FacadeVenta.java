package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateVenta;
import puesto.maria.entity.DetalleVenta;

import java.util.List;

public class FacadeVenta {

    private final DelegateVenta delegate = new DelegateVenta();

    public void procesarVenta(List<DetalleVenta> detalles) {
        delegate.procesarVenta(detalles);
    }
}