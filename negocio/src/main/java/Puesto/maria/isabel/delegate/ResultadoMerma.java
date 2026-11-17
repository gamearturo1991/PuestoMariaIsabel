package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Resultado de registrar una merma. */
public class ResultadoMerma implements Serializable {

    private final Producto producto;
    private final int piezas;
    private final BigDecimal perdida;
    private final int existenciaRestante;

    public ResultadoMerma(Producto producto, int piezas, BigDecimal perdida, int existenciaRestante) {
        this.producto = producto;
        this.piezas = piezas;
        this.perdida = perdida;
        this.existenciaRestante = existenciaRestante;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getPiezas() {
        return piezas;
    }

    /** Dinero perdido: piezas por el costo con el que entró cada una. */
    public BigDecimal getPerdida() {
        return perdida;
    }

    /** Existencia del producto después de la merma. */
    public int getExistenciaRestante() {
        return existenciaRestante;
    }

    /** Estado de stock del producto después de la merma (agotado, stock bajo...). */
    public EstadoStock getEstado() {
        return EstadoStock.de(existenciaRestante, producto.getStockMinimo(), producto.getStockMaximo());
    }

    /** True si la merma dejó al producto agotado o con stock bajo. */
    public boolean isAlerta() {
        return getEstado() == EstadoStock.AGOTADO || getEstado() == EstadoStock.BAJO;
    }
}
