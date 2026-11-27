package helper;

import Puesto.maria.isabel.delegate.PeriodoReporte;
import Puesto.maria.isabel.delegate.ReporteGanancia;
import Puesto.maria.isabel.delegate.ReporteInventario;
import Puesto.maria.isabel.delegate.ReporteVentas;
import Puesto.maria.isabel.facade.FacadeReportes;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import Puesto.maria.isabel.facade.FacadeCorteCaja;
import puesto.maria.entity.CorteCaja;

import Puesto.maria.isabel.delegate.PeriodoCortes;
import Puesto.maria.isabel.delegate.ResultadoArqueo;
import Puesto.maria.isabel.delegate.ResumenCortes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Puente entre la pantalla de reportes y la fachada. */
public class ReporteHelper implements Serializable {

    private FacadeReportes fachada() {
        return ServiceFacadeLocator.getInstanceFacadeReportes();
    }

    private FacadeCorteCaja fachadaCorte() { return ServiceFacadeLocator.getInstanceFacadeCorteCaja(); }

    public PeriodoReporte periodo(PeriodoReporte.Tipo tipo, LocalDate fecha) {
        return fachada().periodo(tipo, fecha);
    }

    public PeriodoReporte mes(int anio, int mes) {
        return fachada().mes(anio, mes);
    }

    public ReporteVentas ventas(PeriodoReporte periodo) {
        return fachada().ventas(periodo);
    }

    public ReporteGanancia ganancia(PeriodoReporte periodo) {
        return fachada().ganancia(periodo);
    }

    public ReporteInventario inventario(PeriodoReporte periodo, Integer idCategoria, Integer idProveedor) {
        return fachada().inventario(periodo, idCategoria, idProveedor);
    }

    public CorteCaja previsualizarCorte(LocalDate fecha) {
        return fachadaCorte().previsualizarCorte(fecha);
    }

    public BigDecimal fondoProvisional(LocalDate fecha) {
        return fachadaCorte().fondoProvisional(fecha);
    }

    public ResultadoArqueo arqueo(LocalDate fecha, BigDecimal contado) {
        return fachadaCorte().arqueo(fecha, contado);
    }

    public ResultadoArqueo registrarCorte(LocalDate fecha, Integer idUsuario, BigDecimal contado) {
        return fachadaCorte().registrarCorte(fecha, idUsuario, contado);
    }

    public CorteCaja obtenerCorte(LocalDate fecha) {
        return fachadaCorte().obtenerCorte(fecha);
    }

    public ResumenCortes resumenCortes(PeriodoCortes tipo, LocalDate referencia) {
        return fachadaCorte().resumen(tipo, referencia);
    }
}
