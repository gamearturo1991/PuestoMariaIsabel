package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateReportes;
import Puesto.maria.isabel.delegate.PeriodoReporte;
import Puesto.maria.isabel.delegate.ReporteGanancia;
import Puesto.maria.isabel.delegate.ReporteInventario;
import Puesto.maria.isabel.delegate.ReporteVentas;

import java.time.LocalDate;

public class FacadeReportes {

    private final DelegateReportes delegateReportes = new DelegateReportes();

    public PeriodoReporte periodo(PeriodoReporte.Tipo tipo, LocalDate fecha) {
        return delegateReportes.periodo(tipo, fecha);
    }

    public PeriodoReporte mes(int anio, int mes) {
        return delegateReportes.mes(anio, mes);
    }

    public ReporteVentas ventas(PeriodoReporte periodo) {
        return delegateReportes.ventas(periodo);
    }

    public ReporteGanancia ganancia(PeriodoReporte periodo) {
        return delegateReportes.ganancia(periodo);
    }

    public ReporteInventario inventario(PeriodoReporte periodo, Integer idCategoria, Integer idProveedor) {
        return delegateReportes.inventario(periodo, idCategoria, idProveedor);
    }
}
