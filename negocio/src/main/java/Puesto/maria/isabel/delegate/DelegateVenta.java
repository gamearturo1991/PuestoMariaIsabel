package Puesto.maria.isabel.delegate;

import puesto.maria.entity.*;
import puesto.maria.persistence.dao.VentaDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class DelegateVenta {

    private VentaDAO dao() {
        return ServiceLocator.getInstanceVentaDAO();
    }

    public Integer obtenerSiguienteFolio() {
        return dao().obtenerSiguienteFolio();
    }

    public List<Cliente> listarClientes() {
        return ServiceLocator.getInstanceClienteDAO().listarActivos();
    }

    public void registrarVenta(Venta venta, List<DetalleVenta> detalles, List<PagoVenta> pagos) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto.");
        }

        BigDecimal totalPagado = pagos.stream()
                .map(PagoVenta::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPagado.compareTo(venta.getTotal()) < 0) {
            throw new IllegalArgumentException("El monto pagado es menor al total de la venta.");
        }

        venta.setFechaHora(Instant.now());
        venta.setEstado("COMPLETADA");

        dao().registrarVentaCompleta(venta, detalles, pagos);
    }
}