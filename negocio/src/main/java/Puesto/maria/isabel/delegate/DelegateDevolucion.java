package Puesto.maria.isabel.delegate;

import puesto.maria.entity.DetalleVenta;
import puesto.maria.entity.Devolucion;
import puesto.maria.entity.Venta;
import puesto.maria.persistence.dao.LineaDevolucion;
import puesto.maria.persistence.dao.ReporteDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.util.List;

public class DelegateDevolucion {

    /** Busca la venta por folio. Solo las ventas completadas admiten devolución. */
    public Venta buscarVenta(Integer folio) {
        if (folio == null || folio < 1) {
            throw new IllegalArgumentException("Indica el folio de la venta.");
        }
        Venta venta = ServiceLocator.getInstanceVentaDAO().findFresh(folio).orElse(null);
        if (venta == null) {
            throw new IllegalArgumentException("No existe una venta con el folio " + folio + ".");
        }
        if (!ReporteDAO.COMPLETADA.equals(venta.getEstado())) {
            throw new IllegalArgumentException("La venta " + folio + " está en estado "
                    + venta.getEstado() + " y no admite devoluciones.");
        }
        return venta;
    }

    public List<DetalleVenta> detallesDeVenta(Integer folio) {
        return ServiceLocator.getInstanceDevolucionDAO().detallesDeVenta(folio);
    }

    public Devolucion registrarDevolucion(Integer folio, String motivo, Integer idUsuario,
                                          List<LineaDevolucion> lineas) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("No hay un usuario con sesión iniciada.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Escribe el motivo de la devolución.");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException("Indica cuántas piezas se devuelven.");
        }
        return ServiceLocator.getInstanceDevolucionDAO()
                .registrarDevolucion(folio, idUsuario, motivo.trim(), lineas);
    }
}