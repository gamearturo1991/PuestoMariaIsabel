package ui;

import puesto.maria.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Una fila de la tabla "Recibir un proveedor": un producto del proveedor con lo que se va a recibir. */
public class LineaRecepcion implements Serializable {

    private final Producto producto;
    private final long existencia;
    private Integer cantidad;
    private BigDecimal costo;
    private BigDecimal precioVenta;
    private BigDecimal precioSugerido;

    public LineaRecepcion(Producto producto, long existencia) {
        this.producto = producto;
        this.existencia = existencia;
        this.costo = producto.getCosto();
        this.precioVenta = producto.getPrecioVenta();
    }

    /** True si el usuario capturó una cantidad para recibir. */
    public boolean isSeleccionada() {
        return cantidad != null && cantidad > 0;
    }

    public boolean isBajoMinimo() {
        return producto.getStockMinimo() != null && existencia <= producto.getStockMinimo();
    }

    public Producto getProducto() {
        return producto;
    }

    public long getExistencia() {
        return existencia;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPrecioSugerido() {
        return precioSugerido;
    }

    public void setPrecioSugerido(BigDecimal precioSugerido) {
        this.precioSugerido = precioSugerido;
    }
}
