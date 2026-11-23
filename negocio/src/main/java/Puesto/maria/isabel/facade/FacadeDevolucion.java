package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateDevolucion;
import puesto.maria.entity.DetalleVenta;
import puesto.maria.entity.Devolucion;
import puesto.maria.entity.Venta;
import puesto.maria.persistence.dao.LineaDevolucion;

import java.util.List;
import java.util.Map;

public class FacadeDevolucion {

    private final DelegateDevolucion delegateDevolucion = new DelegateDevolucion();

    public Venta buscarVenta(Integer folio) {
        return delegateDevolucion.buscarVenta(folio);
    }

    public List<DetalleVenta> detallesDeVenta(Integer folio) {
        return delegateDevolucion.detallesDeVenta(folio);
    }

    public Map<Integer, Long> devueltasPorDetalle(Integer folio) {
        return delegateDevolucion.devueltasPorDetalle(folio);
    }

    public Devolucion registrarDevolucion(Integer folio, String motivo, Integer idUsuario,
                                          List<LineaDevolucion> lineas) {
        return delegateDevolucion.registrarDevolucion(folio, motivo, idUsuario, lineas);
    }
}