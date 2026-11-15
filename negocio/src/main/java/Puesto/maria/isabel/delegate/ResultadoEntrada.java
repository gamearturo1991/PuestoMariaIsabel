package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Producto;

import java.io.Serializable;

/** Resultado de registrar una entrada de mercancía. */
public class ResultadoEntrada implements Serializable {

    private final Producto producto;
    private final int existenciaTotal;
    private final boolean reactivado;

    public ResultadoEntrada(Producto producto, int existenciaTotal, boolean reactivado) {
        this.producto = producto;
        this.existenciaTotal = existenciaTotal;
        this.reactivado = reactivado;
    }

    public Producto getProducto() {
        return producto;
    }

    /** Existencia del producto después de la entrada. */
    public int getExistenciaTotal() {
        return existenciaTotal;
    }

    /** True si el producto estaba deshabilitado y la entrada lo reactivó. */
    public boolean isReactivado() {
        return reactivado;
    }

    /** True si la existencia pasó del stock máximo. Solo es un aviso: la entrada ya quedó registrada. */
    public boolean isSuperaMaximo() {
        return producto.getStockMaximo() != null && existenciaTotal > producto.getStockMaximo();
    }
}
