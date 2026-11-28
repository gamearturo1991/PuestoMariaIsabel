package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Resultado de contar el efectivo de la caja contra lo que el sistema dice que debería haber.
 *
 * <p>Diferencia = esperado - contado. Un número positivo es un FALTANTE (falta dinero en la caja) y uno negativo
 * es un SOBRANTE (hay dinero de más).
 */
public class ResultadoArqueo implements Serializable {

    private final LocalDate fecha;
    private final BigDecimal efectivoEsperado;
    private final BigDecimal efectivoContado;
    private final BigDecimal diferencia;
    private Integer idCorte;

    public ResultadoArqueo(LocalDate fecha, BigDecimal efectivoEsperado, BigDecimal efectivoContado) {
        this.fecha = fecha;
        this.efectivoEsperado = efectivoEsperado.setScale(2, RoundingMode.HALF_UP);
        this.efectivoContado = efectivoContado.setScale(2, RoundingMode.HALF_UP);
        this.diferencia = this.efectivoEsperado.subtract(this.efectivoContado);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getEfectivoEsperado() {
        return efectivoEsperado;
    }

    public BigDecimal getEfectivoContado() {
        return efectivoContado;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public Integer getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(Integer idCorte) {
        this.idCorte = idCorte;
    }

    public boolean isFaltante() {
        return diferencia.signum() > 0;
    }

    public boolean isSobrante() {
        return diferencia.signum() < 0;
    }

    public String getEstado() {
        if (isFaltante()) {
            return "Faltante";
        }
        return isSobrante() ? "Sobrante" : "Caja cuadrada";
    }

    /** Texto para la bitácora (se vuelve a leer con {@link DelegateCorteCaja}). */
    public String aTextoBitacora() {
        return "esperado=" + efectivoEsperado.toPlainString() + ", contado=" + efectivoContado.toPlainString()
                + ", diferencia=" + diferencia.toPlainString();
    }
}
