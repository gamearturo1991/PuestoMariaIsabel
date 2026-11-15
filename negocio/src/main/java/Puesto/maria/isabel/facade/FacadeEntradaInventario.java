package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateEntradaInventario;
import Puesto.maria.isabel.delegate.ResultadoEntrada;
import puesto.maria.persistence.dao.LineaEntrada;

import java.math.BigDecimal;
import java.util.List;

public class FacadeEntradaInventario {

    private final DelegateEntradaInventario delegateEntrada;

    public FacadeEntradaInventario() {
        this.delegateEntrada = new DelegateEntradaInventario();
    }

    public ResultadoEntrada registrarEntrada(Integer idProveedor, Integer idProducto, Integer cantidad,
                                             BigDecimal costo, BigDecimal precioVenta,
                                             Integer idUsuario, String observaciones) {
        return delegateEntrada.registrarEntrada(idProveedor, idProducto, cantidad, costo, precioVenta,
                idUsuario, observaciones);
    }

    public List<ResultadoEntrada> registrarRecepcion(Integer idProveedor, List<LineaEntrada> lineas,
                                                     Integer idUsuario, String observaciones) {
        return delegateEntrada.registrarRecepcion(idProveedor, lineas, idUsuario, observaciones);
    }

    public int consultarExistencia(Integer idProducto) {
        return delegateEntrada.consultarExistencia(idProducto);
    }
}
