package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateCorteCaja;
import Puesto.maria.isabel.delegate.PeriodoCortes;
import Puesto.maria.isabel.delegate.ResultadoArqueo;
import Puesto.maria.isabel.delegate.ResumenCortes;
import puesto.maria.entity.CorteCaja;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FacadeCorteCaja {

    private final DelegateCorteCaja delegateCorteCaja = new DelegateCorteCaja();

    public CorteCaja previsualizarCorte(LocalDate fecha) {
        return delegateCorteCaja.previsualizarCorte(fecha);
    }

    public BigDecimal fondoProvisional(LocalDate fecha) {
        return delegateCorteCaja.fondoProvisional(fecha);
    }

    public ResultadoArqueo arqueo(LocalDate fecha, BigDecimal contado) {
        return delegateCorteCaja.arqueo(fecha, contado);
    }

    public ResultadoArqueo registrarCorte(LocalDate fecha, Integer idUsuario, BigDecimal contado) {
        return delegateCorteCaja.registrarCorte(fecha, idUsuario, contado);
    }

    public CorteCaja obtenerCorte(LocalDate fecha) {
        return delegateCorteCaja.obtenerCorte(fecha);
    }

    public ResumenCortes resumen(PeriodoCortes tipo, LocalDate referencia) {
        return delegateCorteCaja.resumen(tipo, referencia);
    }
}
