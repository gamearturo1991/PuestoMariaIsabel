package puesto.maria.persistence.dao;

import java.io.Serializable;
import java.math.BigDecimal;

/** Una línea de una entrada de mercancía: producto, piezas recibidas, costo de compra y precio de venta. */
public class LineaEntrada implements Serializable {

    private final Integer idProducto;
    private final Integer cantidad;
    private final BigDecimal costo;
    private final BigDecimal precioVenta;

    public LineaEntrada(Integer idProducto, Integer cantidad, BigDecimal costo, BigDecimal precioVenta) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costo = costo;
        this.precioVenta = precioVenta;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }
}
