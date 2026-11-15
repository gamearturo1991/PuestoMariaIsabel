package Puesto.maria.isabel.delegate;

import puesto.maria.entity.DetalleVenta;
import puesto.maria.persistence.dao.LoteDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.util.List;

public class DelegateVenta {

    private LoteDAO loteDAO() {
        return ServiceLocator.getInstanceLoteDAO();
    }

    // Procesa la venta (por ahora solo descuenta el stock)
    public void procesarVenta(List<DetalleVenta> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto.");
        }

        for (DetalleVenta d : detalles) {
            loteDAO().descontarStock(d.getIdProducto().getId(), d.getCantidad());
        }
    }
}