package helper;

import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.DetalleVenta;
import puesto.maria.entity.Devolucion;
import puesto.maria.entity.Venta;
import puesto.maria.persistence.dao.LineaDevolucion;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DevolucionHelper implements Serializable {

    public Venta buscarVenta(Integer folio) {
        return ServiceFacadeLocator.getInstanceFacadeDevolucion().buscarVenta(folio);
    }

    public List<DetalleVenta> detallesDeVenta(Integer folio) {
        return ServiceFacadeLocator.getInstanceFacadeDevolucion().detallesDeVenta(folio);
    }

    public Map<Integer, Long> devueltasPorDetalle(Integer folio) {
        return ServiceFacadeLocator.getInstanceFacadeDevolucion().devueltasPorDetalle(folio);
    }

    public Devolucion registrar(Integer folio, String motivo, Integer idUsuario,
                                List<LineaDevolucion> lineas) {
        return ServiceFacadeLocator.getInstanceFacadeDevolucion()
                .registrarDevolucion(folio, motivo, idUsuario, lineas);
    }
}