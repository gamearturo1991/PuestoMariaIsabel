package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Una merma tal como la ve el administrador: producto, motivo, piezas y pérdida. Aunque se haya descontado
 * de varias entradas (varias filas de la tabla merma), aquí aparece como un solo registro.
 */
public class RegistroMerma implements Serializable {

    private final String fecha;
    private final Producto producto;
    private final String motivo;
    private final String observaciones;
    private final String usuario;
    private final List<Integer> ids;
    private final int piezas;
    private final long rescatadas;
    private final BigDecimal perdidaBruta;
    private final BigDecimal perdidaNeta;
    private final List<AnulacionRegistro> anulaciones;

    public RegistroMerma(String fecha, Producto producto, String motivo, String observaciones, String usuario,
                         List<Integer> ids, int piezas, long rescatadas, BigDecimal perdidaBruta,
                         BigDecimal perdidaNeta, List<AnulacionRegistro> anulaciones) {
        this.fecha = fecha;
        this.producto = producto;
        this.motivo = motivo;
        this.observaciones = observaciones;
        this.usuario = usuario;
        this.ids = new ArrayList<>(ids);
        this.piezas = piezas;
        this.rescatadas = rescatadas;
        this.perdidaBruta = perdidaBruta;
        this.perdidaNeta = perdidaNeta;
        this.anulaciones = new ArrayList<>(anulaciones);
    }

    public String getFecha() {
        return fecha;
    }

    public Producto getProducto() {
        return producto;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getUsuario() {
        return usuario;
    }

    /** Filas de la tabla merma que forman este registro. */
    public List<Integer> getIds() {
        return ids;
    }

    /** Piezas que se mermaron originalmente. */
    public int getPiezas() {
        return piezas;
    }

    /** Piezas que ya se rescataron con anulaciones. */
    public long getRescatadas() {
        return rescatadas;
    }

    /** Piezas que siguen mermadas (mermadas menos rescatadas). */
    public long getPendientes() {
        return piezas - rescatadas;
    }

    /** Pérdida de lo que se mermó originalmente. */
    public BigDecimal getPerdidaBruta() {
        return perdidaBruta;
    }

    /** Pérdida real: lo mermado menos lo rescatado, a su costo. */
    public BigDecimal getPerdidaNeta() {
        return perdidaNeta;
    }

    public List<AnulacionRegistro> getAnulaciones() {
        return anulaciones;
    }

    public boolean isConAnulaciones() {
        return !anulaciones.isEmpty();
    }

    /** True si todas las piezas ya se rescataron: no hay nada más que anular. */
    public boolean isTotalmenteAnulada() {
        return getPendientes() <= 0;
    }
}
