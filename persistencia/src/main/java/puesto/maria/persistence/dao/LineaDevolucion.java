package puesto.maria.persistence.dao;

import java.io.Serializable;

/** Una línea de una devolución: renglón de la venta, piezas que regresan y si se pueden volver a vender. */
public class LineaDevolucion implements Serializable {

    private final Integer idDetalle;
    private final int cantidad;
    private final boolean revendible;

    public LineaDevolucion(Integer idDetalle, int cantidad, boolean revendible) {
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.revendible = revendible;
    }

    public Integer getIdDetalle() { return idDetalle; }
    public int getCantidad() { return cantidad; }
    public boolean isRevendible() { return revendible; }
}