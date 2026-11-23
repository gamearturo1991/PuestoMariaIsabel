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
                filasDevolucion(desde, hasta), filasPago(desde, hasta), vendidosNetos(desde, hasta),
                cantidad(noCompletadas), monto(noCompletadas));
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
        Object[] noCompletadas = dao().ventasNoCompletadas(desde, hasta);
        return CalculoReportes.ganancia(periodo, filasVenta(desde, hasta), filasDevolucion(desde, hasta),
                vendidosNetos(desde, hasta), costosNetos(desde, hasta), filasPago(desde, hasta),
                dao().mermasNetas(desde, hasta), cantidad(noCompletadas), monto(noCompletadas));
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
        Acumulado alInicio = new Acumulado(dao().recibidasAntesDe(desde), vendidasNetasAntesDe(desde),
                dao().mermadasAntesDe(desde));
        Acumulado alFinal = new Acumulado(dao().recibidasAntesDe(hasta), vendidasNetasAntesDe(hasta),
                dao().mermadasAntesDe(hasta));
        Map<Integer, Long> actual = dao().existenciaActual();
        return CalculoReportes.inventario(periodo, productos, alInicio, alFinal, actual, idCategoria, idProveedor);
    }

    /* ======================== devoluciones ======================== */

    /** Piezas vendidas hasta el instante, sin las que se devolvieron (revendibles o mermadas). */
    private Map<Integer, Long> vendidasNetasAntesDe(Instant corte) {
        Map<Integer, Long> netas = new java.util.HashMap<>(dao().vendidasAntesDe(corte));
        for (Map.Entry<Integer, Long> d : dao().devueltasAntesDe(corte).entrySet()) {
            netas.merge(d.getKey(), -d.getValue(), Long::sum);
        }
        return netas;
    }

    /** Lo vendido por producto menos lo devuelto en el mismo periodo (piezas y dinero). */
    private List<FilaVendido> vendidosNetos(Instant desde, Instant hasta) {
        Map<Integer, FilaVendido> netos = new java.util.LinkedHashMap<>();
        for (FilaVendido f : filasVendido(desde, hasta)) {
            netos.put(f.idProducto(), f);
        }
        for (Object[] r : dao().devueltosPorProducto(desde, hasta)) {
            Integer id = (Integer) r[0];
            long piezas = ((Number) r[5]).longValue();
            BigDecimal dinero = r[6] == null ? BigDecimal.ZERO : (BigDecimal) r[6];
            FilaVendido v = netos.get(id);
            if (v == null) {
                v = new FilaVendido(id, (String) r[1], (String) r[2], (Integer) r[3], (String) r[4],
                        0, BigDecimal.ZERO);
            }
            BigDecimal ingreso = (v.ingreso() == null ? BigDecimal.ZERO : v.ingreso()).subtract(dinero);
            netos.put(id, new FilaVendido(id, v.codigo(), v.producto(), v.idCategoria(), v.categoria(),
                    v.piezas() - piezas, ingreso));
        }
        List<FilaVendido> filas = new ArrayList<>();
        for (FilaVendido f : netos.values()) {
            if (f.piezas() != 0 || (f.ingreso() != null && f.ingreso().signum() != 0)) {
                filas.add(f);
            }
        }
        return filas;
    }

    /** Costo de lo vendido por producto menos el costo de las piezas devueltas. */
    private List<FilaCosto> costosNetos(Instant desde, Instant hasta) {
        Map<Integer, BigDecimal> costos = new java.util.LinkedHashMap<>();
        for (Object[] f : dao().costoVendidoPorProducto(desde, hasta)) {
            costos.put((Integer) f[0], f[1] == null ? BigDecimal.ZERO : (BigDecimal) f[1]);
        }
        for (Object[] f : dao().costoDevueltoPorProducto(desde, hasta)) {
            BigDecimal devuelto = f[1] == null ? BigDecimal.ZERO : (BigDecimal) f[1];
            costos.merge((Integer) f[0], devuelto.negate(), BigDecimal::add);
        }
        List<FilaCosto> filas = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> e : costos.entrySet()) {
            filas.add(new FilaCosto(e.getKey(), e.getValue()));
        }
        return filas;
    }

    private List<FilaVenta> filasDevolucion(Instant desde, Instant hasta) {
        List<FilaVenta> filas = new ArrayList<>();
        for (Object[] f : dao().devoluciones(desde, hasta)) {
            filas.add(new FilaVenta((Instant) f[0], (BigDecimal) f[1]));
        }
        return filas;
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
