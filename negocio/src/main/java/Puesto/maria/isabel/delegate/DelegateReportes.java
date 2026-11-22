package Puesto.maria.isabel.delegate;

import Puesto.maria.isabel.delegate.CalculoReportes.Acumulado;
import Puesto.maria.isabel.delegate.CalculoReportes.DatosProducto;
import Puesto.maria.isabel.delegate.CalculoReportes.FilaCosto;
import Puesto.maria.isabel.delegate.CalculoReportes.FilaPago;
import Puesto.maria.isabel.delegate.CalculoReportes.FilaVendido;
import Puesto.maria.isabel.delegate.CalculoReportes.FilaVenta;
import puesto.maria.entity.Producto;
import puesto.maria.persistence.dao.ReporteDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reportes (PBI-REP-US2, US3 y US4): ventas por periodo, ingresos y ganancia neta, y estado y rotación del
 * inventario. Todos son de solo lectura. Los errores de validación se lanzan como IllegalArgumentException.
 */
public class DelegateReportes {

    private ReporteDAO dao() {
        return ServiceLocator.getInstanceReporteDAO();
    }

    private static ZoneId zona() {
        return ZoneId.systemDefault();
    }

    /** Periodo semanal o mensual que contiene la fecha; sin fecha se usa hoy. No admite periodos futuros. */
    public PeriodoReporte periodo(PeriodoReporte.Tipo tipo, LocalDate fecha) {
        LocalDate hoy = LocalDate.now(zona());
        PeriodoReporte periodo = PeriodoReporte.de(tipo, fecha == null ? hoy : fecha);
        if (periodo.esFuturo(hoy)) {
            throw new IllegalArgumentException("No se pueden consultar periodos futuros.");
        }
        return periodo;
    }

    /** Mes calendario indicado; no admite meses futuros. */
    public PeriodoReporte mes(int anio, int mes) {
        PeriodoReporte periodo = PeriodoReporte.mes(anio, mes);
        if (periodo.esFuturo(LocalDate.now(zona()))) {
            throw new IllegalArgumentException("No se pueden consultar meses futuros.");
        }
        return periodo;
    }

    public ReporteVentas ventas(PeriodoReporte periodo) {
        Instant desde = periodo.desde(zona());
        Instant hasta = periodo.hasta(zona());
        Object[] noCompletadas = dao().ventasNoCompletadas(desde, hasta);
        ReporteVentas reporte = CalculoReportes.ventas(periodo, zona(), filasVenta(desde, hasta),
                filasPago(desde, hasta), filasVendido(desde, hasta), cantidad(noCompletadas),
                monto(noCompletadas));
        List<CalculoReportes.FilaLinea> lineas = filasLinea(desde, hasta);
        List<ReporteVentas.Ticket> tickets = CalculoReportes.tickets(zona(), lineas, filasMetodo(desde, hasta));
        asignarClientes(tickets, desde, hasta);
        reporte.setTickets(tickets);
        return reporte;
    }

    /** Pone en cada ticket su cliente con el nombre y régimen que tenía el día de la venta. */
    private void asignarClientes(List<ReporteVentas.Ticket> tickets, Instant desde, Instant hasta) {
        Map<Integer, Object[]> porVenta = new java.util.HashMap<>();
        for (Object[] f : dao().clientesDeVentas(desde, hasta)) {
            porVenta.put((Integer) f[0], f);
        }
        if (porVenta.isEmpty()) {
            return;
        }
        DelegateCliente delegateCliente = new DelegateCliente();
        Map<Integer, puesto.maria.entity.Cliente> clientes = new java.util.HashMap<>();
        Map<Integer, HistorialCliente> historiales = new java.util.HashMap<>();
        for (ReporteVentas.Ticket t : tickets) {
            Object[] f = porVenta.get(t.getNumero());
            if (f == null) {
                continue;
            }
            Integer idCliente = (Integer) f[1];
            puesto.maria.entity.Cliente c = clientes.computeIfAbsent(idCliente,
                    id -> ServiceLocator.getInstanceClienteDAO().find(id).orElse(null));
            if (c == null) {
                continue;
            }
            HistorialCliente h = historiales.computeIfAbsent(idCliente, id -> delegateCliente.historial(c));
            HistorialCliente.Datos d = h.en((Instant) f[2]);
            t.setCliente(d.nombre(), c.getRfc(), d.regimen());
        }
    }

    public ReporteGanancia ganancia(PeriodoReporte periodo) {
        Instant desde = periodo.desde(zona());
        Instant hasta = periodo.hasta(zona());
        List<FilaCosto> costos = new ArrayList<>();
        for (Object[] f : dao().costoVendidoPorProducto(desde, hasta)) {
            costos.add(new FilaCosto((Integer) f[0], (BigDecimal) f[1]));
        }
        Object[] noCompletadas = dao().ventasNoCompletadas(desde, hasta);
        return CalculoReportes.ganancia(periodo, filasVenta(desde, hasta), filasVendido(desde, hasta), costos,
                filasPago(desde, hasta), dao().mermasNetas(desde, hasta), cantidad(noCompletadas),
                monto(noCompletadas));
    }

    /** Informe del mes; la categoría y el proveedor son filtros opcionales (null = todos). */
    public ReporteInventario inventario(PeriodoReporte periodo, Integer idCategoria, Integer idProveedor) {
        Instant desde = periodo.desde(zona());
        Instant hasta = periodo.hasta(zona());
        List<DatosProducto> productos = new ArrayList<>();
        for (Producto p : dao().productos()) {
            productos.add(new DatosProducto(p.getId(), p.getCodigoCorto(), p.getNombre(),
                    p.getIdCategoria() == null ? null : p.getIdCategoria().getId(),
                    p.getIdCategoria() == null ? null : p.getIdCategoria().getNombre(),
                    p.getIdProveedor() == null ? null : p.getIdProveedor().getId(),
                    p.getIdProveedor() == null ? null : p.getIdProveedor().getNombre(),
                    Boolean.TRUE.equals(p.getActivo()), p.getStockMinimo(), p.getStockMaximo()));
        }
        Acumulado alInicio = new Acumulado(dao().recibidasAntesDe(desde), dao().vendidasAntesDe(desde),
                dao().mermadasAntesDe(desde));
        Acumulado alFinal = new Acumulado(dao().recibidasAntesDe(hasta), dao().vendidasAntesDe(hasta),
                dao().mermadasAntesDe(hasta));
        Map<Integer, Long> actual = dao().existenciaActual();
        return CalculoReportes.inventario(periodo, productos, alInicio, alFinal, actual, idCategoria, idProveedor);
    }

    /* ======================== conversión de filas ======================== */

    private List<FilaVenta> filasVenta(Instant desde, Instant hasta) {
        List<FilaVenta> filas = new ArrayList<>();
        for (Object[] f : dao().ventasCompletadas(desde, hasta)) {
            filas.add(new FilaVenta((Instant) f[0], (BigDecimal) f[1]));
        }
        return filas;
    }

    private List<FilaPago> filasPago(Instant desde, Instant hasta) {
        List<FilaPago> filas = new ArrayList<>();
        for (Object[] f : dao().pagos(desde, hasta)) {
            filas.add(new FilaPago((String) f[0], (BigDecimal) f[1], (BigDecimal) f[2]));
        }
        return filas;
    }

    private List<CalculoReportes.FilaLinea> filasLinea(Instant desde, Instant hasta) {
        List<CalculoReportes.FilaLinea> filas = new ArrayList<>();
        for (Object[] f : dao().lineasDeVentas(desde, hasta)) {
            filas.add(new CalculoReportes.FilaLinea((Integer) f[0], (Instant) f[1], (BigDecimal) f[2],
                    (String) f[3], (String) f[4], ((Number) f[5]).intValue(), (BigDecimal) f[6],
                    (BigDecimal) f[7]));
        }
        return filas;
    }

    private List<CalculoReportes.FilaMetodo> filasMetodo(Instant desde, Instant hasta) {
        List<CalculoReportes.FilaMetodo> filas = new ArrayList<>();
        for (Object[] f : dao().metodosPorVenta(desde, hasta)) {
            filas.add(new CalculoReportes.FilaMetodo((Integer) f[0], (String) f[1], (BigDecimal) f[2]));
        }
        return filas;
    }

    private List<FilaVendido> filasVendido(Instant desde, Instant hasta) {
        List<FilaVendido> filas = new ArrayList<>();
        for (Object[] f : dao().vendidosPorProducto(desde, hasta)) {
            filas.add(new FilaVendido((Integer) f[0], (String) f[1], (String) f[2], (Integer) f[3], (String) f[4],
                    ((Number) f[5]).longValue(), (BigDecimal) f[6]));
        }
        return filas;
    }

    private static int cantidad(Object[] fila) {
        return fila == null || fila[0] == null ? 0 : ((Number) fila[0]).intValue();
    }

    private static BigDecimal monto(Object[] fila) {
        return fila == null || fila[1] == null ? BigDecimal.ZERO : (BigDecimal) fila[1];
    }
}
