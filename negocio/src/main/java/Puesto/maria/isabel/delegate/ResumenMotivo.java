package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.math.BigDecimal;

/** Total de piezas y dinero perdido por un motivo de merma en el periodo consultado. */
public class ResumenMotivo implements Serializable {

    private final String motivo;
    private long piezas;
    private BigDecimal perdida = BigDecimal.ZERO;

    public ResumenMotivo(String motivo) {
        this.motivo = motivo;
    }

    void sumar(long piezasNetas, BigDecimal perdidaNeta) {
        this.piezas += piezasNetas;
        this.perdida = this.perdida.add(perdidaNeta);
    }

    public String getMotivo() {
        return motivo;
    }

    /** Piezas netas: mermadas menos rescatadas. */
    public long getPiezas() {
        return piezas;
    }

    /** Pérdida neta: piezas netas a su costo. */
    public BigDecimal getPerdida() {
        return perdida;
    }
}
