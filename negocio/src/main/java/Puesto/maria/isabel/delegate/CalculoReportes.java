package Puesto.maria.isabel.delegate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Cálculos de los reportes (PBI-REP). No toca la base de datos: recibe las filas ya consultadas y arma el
 * resultado, para que las reglas de negocio se puedan probar solas.
 */
public final class CalculoReportes {

    private static final Locale ES_MX = Locale.forLanguageTag("es-MX");
    private static final BigDecimal CIEN = new BigDecimal("100");

    private CalculoReportes() {
    }

    /** Una venta completada: fecha y total. */
    public record FilaVenta(Instant fecha, BigDecimal total) {
    }

    /** Un producto de una venta: ticket, fecha, total del ticket y la línea. */
    public record FilaLinea(Integer idVenta, Instant fecha, BigDecimal total, String codigo, String producto,
                            int cantidad, BigDecimal precio, BigDecimal subtotal) {
    }

    /** Lo pagado en una venta con un método de pago. */
    public record FilaMetodo(Integer idVenta, String metodo, BigDecimal monto) {
    }

    /** Lo cobrado con un método de pago y su comisión. */
    public record FilaPago(String metodo, BigDecimal monto, BigDecimal comision) {
    }

    /** Lo vendido de un producto: piezas e ingreso (suma de subtotales). */
    public record FilaVendido(Integer idProducto, String codigo, String producto, Integer idCategoria,
                              String categoria, long piezas, BigDecimal ingreso) {
    }

    /** Costo de adquisición de las piezas vendidas de un producto. */
    public record FilaCosto(Integer idProducto, BigDecimal costo) {
    }

    /** Datos de un producto para el informe de inventario. */
    public record DatosProducto(Integer id, String codigo, String nombre, Integer idCategoria, String categoria,
                                Integer idProveedor, String proveedor, boolean activo,
                                Integer stockMinimo, Integer stockMaximo) {
    }

    /** Movimientos acumulados por producto hasta un instante (recibidas, vendidas, mermadas). */
    public record Acumulado(Map<Integer, Long> recibidas, Map<Integer, Long> vendidas, Map<Integer, Long> mermadas) {
    }

    /* ======================== ventas por periodo ======================== */

    public static ReporteVentas ventas(PeriodoReporte periodo, ZoneId zona, List<FilaVenta> ventas,
                                       List<FilaPago> pagos, List<FilaVendido> vendidos,
                                       int noCompletadas, BigDecimal montoNoCompletadas) {
        BigDecimal total = BigDecimal.ZERO;
        Map<LocalDate, int[]> cuentaDia = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> totalDia = new LinkedHashMap<>();
        for (LocalDate d : periodo.getDias()) {
            cuentaDia.put(d, new int[1]);
            totalDia.put(d, BigDecimal.ZERO);
        }
        for (FilaVenta v : ventas) {
            BigDecimal t = dinero(v.total());
            total = total.add(t);
            LocalDate dia = v.fecha().atZone(zona).toLocalDate();
            if (cuentaDia.containsKey(dia)) {
                cuentaDia.get(dia)[0]++;
                totalDia.merge(dia, t, BigDecimal::add);
            }
        }
        List<ReporteVentas.Dia> dias = new ArrayList<>();
        for (LocalDate d : periodo.getDias()) {
            String etiqueta = capitalizar(d.getDayOfWeek().getDisplayName(TextStyle.SHORT, ES_MX).replace(".", ""))
                    + " " + String.format("%02d/%02d", d.getDayOfMonth(), d.getMonthValue());
            dias.add(new ReporteVentas.Dia(d, etiqueta, cuentaDia.get(d)[0], totalDia.get(d)));
        }

        BigDecimal totalCobrado = BigDecimal.ZERO;
        Map<String, BigDecimal> porMetodo = new LinkedHashMap<>();
        for (FilaPago p : pagos) {
            BigDecimal m = dinero(p.monto());
            totalCobrado = totalCobrado.add(m);
            porMetodo.merge(capitalizar(p.metodo() == null ? "Sin método" : p.metodo().toLowerCase(ES_MX)), m,
                    BigDecimal::add);
        }
        List<ReporteVentas.Metodo> metodos = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : porMetodo.entrySet()) {
            BigDecimal pct = totalCobrado.signum() == 0 ? BigDecimal.ZERO
                    : e.getValue().multiply(CIEN).divide(totalCobrado, 1, RoundingMode.HALF_UP);
            metodos.add(new ReporteVentas.Metodo(e.getKey(), e.getValue(), pct));
        }
        metodos.sort(Comparator.comparing(ReporteVentas.Metodo::getMonto).reversed());

        long piezas = 0;
        Map<String, long[]> piezasCat = new LinkedHashMap<>();
        Map<String, BigDecimal> totalCat = new LinkedHashMap<>();
        List<ReporteVentas.Producto> productos = new ArrayList<>();
        for (FilaVendido f : vendidos) {
            piezas += f.piezas();
            String cat = nombreCategoria(f.categoria());
            piezasCat.computeIfAbsent(cat, k -> new long[1])[0] += f.piezas();
            totalCat.merge(cat, dinero(f.ingreso()), BigDecimal::add);
            productos.add(new ReporteVentas.Producto(f.codigo(), f.producto(), cat, f.piezas(), dinero(f.ingreso())));
        }
        List<ReporteVentas.Categoria> categorias = new ArrayList<>();
        for (Map.Entry<String, long[]> e : piezasCat.entrySet()) {
            categorias.add(new ReporteVentas.Categoria(e.getKey(), e.getValue()[0], totalCat.get(e.getKey())));
        }
        categorias.sort(Comparator.comparing(ReporteVentas.Categoria::getTotal).reversed());
        productos.sort(Comparator.comparingLong(ReporteVentas.Producto::getPiezas).reversed()
                .thenComparing(Comparator.comparing(ReporteVentas.Producto::getTotal).reversed())
                .thenComparing(ReporteVentas.Producto::getNombre, String.CASE_INSENSITIVE_ORDER));

        int n = ventas.size();
        BigDecimal ticket = n == 0 ? BigDecimal.ZERO : total.divide(new BigDecimal(n), 2, RoundingMode.HALF_UP);
        return new ReporteVentas(periodo, n, piezas, total, ticket, dias, metodos, categorias, productos,
                noCompletadas, dinero(montoNoCompletadas));
    }

    /* ======================== ingresos y ganancia neta ======================== */

    public static ReporteGanancia ganancia(PeriodoReporte periodo, List<FilaVenta> ventas,
                                           List<FilaVendido> vendidos, List<FilaCosto> costos,
                                           List<FilaPago> pagos, BigDecimal mermasNetas,
                                           int noCompletadas, BigDecimal montoNoCompletadas) {
        BigDecimal ingresos = BigDecimal.ZERO;
        for (FilaVenta v : ventas) {
            ingresos = ingresos.add(dinero(v.total()));
        }
        Map<Integer, BigDecimal> costoPorProducto = new LinkedHashMap<>();
        BigDecimal costoTotal = BigDecimal.ZERO;
        for (FilaCosto c : costos) {
            BigDecimal valor = dinero(c.costo());
            costoPorProducto.merge(c.idProducto(), valor, BigDecimal::add);
            costoTotal = costoTotal.add(valor);
        }
        BigDecimal comisiones = BigDecimal.ZERO;
        for (FilaPago p : pagos) {
            comisiones = comisiones.add(dinero(p.comision()));
        }
        BigDecimal mermas = dinero(mermasNetas);

        List<ReporteGanancia.Linea> productos = new ArrayList<>();
        Map<String, long[]> piezasCat = new LinkedHashMap<>();
        Map<String, BigDecimal> ingresoCat = new LinkedHashMap<>();
        Map<String, BigDecimal> costoCat = new LinkedHashMap<>();
        for (FilaVendido f : vendidos) {
            BigDecimal ingreso = dinero(f.ingreso());
            BigDecimal costo = costoPorProducto.getOrDefault(f.idProducto(), BigDecimal.ZERO);
            productos.add(new ReporteGanancia.Linea(f.producto(), f.piezas(), ingreso, costo));
            String cat = nombreCategoria(f.categoria());
            piezasCat.computeIfAbsent(cat, k -> new long[1])[0] += f.piezas();
            ingresoCat.merge(cat, ingreso, BigDecimal::add);
            costoCat.merge(cat, costo, BigDecimal::add);
        }
        List<ReporteGanancia.Linea> categorias = new ArrayList<>();
        for (Map.Entry<String, long[]> e : piezasCat.entrySet()) {
            categorias.add(new ReporteGanancia.Linea(e.getKey(), e.getValue()[0], ingresoCat.get(e.getKey()),
                    costoCat.get(e.getKey())));
        }
        categorias.sort(Comparator.comparing(ReporteGanancia.Linea::getGanancia).reversed());
        productos.sort(Comparator.comparing(ReporteGanancia.Linea::getGanancia).reversed()
                .thenComparing(ReporteGanancia.Linea::getNombre, String.CASE_INSENSITIVE_ORDER));

        BigDecimal bruta = ingresos.subtract(costoTotal);
        BigDecimal neta = bruta.subtract(mermas).subtract(comisiones);
        BigDecimal margen = ingresos.signum() == 0 ? null
                : neta.multiply(CIEN).divide(ingresos, 2, RoundingMode.HALF_UP);

        List<ReporteGanancia.Concepto> resumen = new ArrayList<>();
        resumen.add(new ReporteGanancia.Concepto("Ingresos (ventas completadas)", ingresos));
        resumen.add(new ReporteGanancia.Concepto("Costo de lo vendido", costoTotal));
        resumen.add(new ReporteGanancia.Concepto("Ganancia bruta", bruta));
        resumen.add(new ReporteGanancia.Concepto("Mermas (neto de anulaciones)", mermas));
        resumen.add(new ReporteGanancia.Concepto("Comisiones de tarjeta", comisiones));
        resumen.add(new ReporteGanancia.Concepto("Ganancia neta", neta));

        boolean sinMovimientos = ventas.isEmpty() && mermas.signum() == 0 && comisiones.signum() == 0;
        return new ReporteGanancia(periodo, ingresos, costoTotal, mermas, comisiones, margen, resumen,
                categorias, productos, noCompletadas, dinero(montoNoCompletadas), sinMovimientos);
    }

    /* ======================== estado y rotación del inventario ======================== */

    /**
     * @param alInicio  movimientos acumulados hasta las 00:00 del primer día del mes
     * @param alFinal   movimientos acumulados hasta el final del mes (excluido)
     * @param actual    existencia actual por producto
     */
    public static ReporteInventario inventario(PeriodoReporte periodo, List<DatosProducto> productos,
                                               Acumulado alInicio, Acumulado alFinal, Map<Integer, Long> actual,
                                               Integer idCategoria, Integer idProveedor) {
        List<ReporteInventario.Fila> conVentas = new ArrayList<>();
        List<ReporteInventario.Fila> sinMovimiento = new ArrayList<>();
        for (DatosProducto p : productos) {
            if (idCategoria != null && !idCategoria.equals(p.idCategoria())) {
                continue;
            }
            if (idProveedor != null && !idProveedor.equals(p.idProveedor())) {
                continue;
            }
            long rec0 = valor(alInicio.recibidas(), p.id());
            long ven0 = valor(alInicio.vendidas(), p.id());
            long mer0 = valor(alInicio.mermadas(), p.id());
            long rec1 = valor(alFinal.recibidas(), p.id());
            long ven1 = valor(alFinal.vendidas(), p.id());
            long mer1 = valor(alFinal.mermadas(), p.id());

            long inicial = rec0 - ven0 - mer0;
            long entradas = rec1 - rec0;
            long vendidas = ven1 - ven0;
            long mermas = mer1 - mer0;
            long finalMes = rec1 - ven1 - mer1;
            boolean hubo = entradas != 0 || vendidas != 0 || mermas != 0;
            if (!p.activo() && !hubo) {
                continue;
            }
            long existenciaActual = valor(actual, p.id());
            EstadoStock estado = EstadoStock.de(existenciaActual, p.stockMinimo(), p.stockMaximo());
            BigDecimal rotacion = null;
            if (vendidas > 0) {
                BigDecimal promedio = new BigDecimal(inicial + finalMes).divide(new BigDecimal(2), 4, RoundingMode.HALF_UP);
                if (promedio.signum() > 0) {
                    rotacion = new BigDecimal(vendidas).divide(promedio, 2, RoundingMode.HALF_UP);
                }
            }
            ReporteInventario.Fila fila = new ReporteInventario.Fila(p.id(), p.codigo(), p.nombre(),
                    nombreCategoria(p.categoria()), p.proveedor() == null ? "—" : p.proveedor(), p.activo(),
                    inicial, entradas, vendidas, mermas, finalMes, existenciaActual, estado, rotacion);
            if (vendidas > 0) {
                conVentas.add(fila);
            } else {
                sinMovimiento.add(fila);
            }
        }
        // Mayor rotación primero; los que no se pueden calcular ("—") al final de los vendidos.
        conVentas.sort(Comparator
                .comparing(ReporteInventario.Fila::getRotacion, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Comparator.comparingLong(ReporteInventario.Fila::getVendidas).reversed())
                .thenComparing(ReporteInventario.Fila::getNombre, String.CASE_INSENSITIVE_ORDER));
        // Sin movimiento: primero los que hay que reabastecer (agotados o stock bajo).
        sinMovimiento.sort(Comparator
                .comparing((ReporteInventario.Fila f) -> !f.isAlerta())
                .thenComparing(ReporteInventario.Fila::getNombre, String.CASE_INSENSITIVE_ORDER));
        return new ReporteInventario(periodo, conVentas, sinMovimiento);
    }

    /* ======================== utilidades ======================== */

    private static long valor(Map<Integer, Long> mapa, Integer id) {
        Long v = mapa == null ? null : mapa.get(id);
        return v == null ? 0 : v;
    }

    /**
     * Agrupa las líneas vendidas por ticket (número de venta). Los tickets salen del más reciente al más
     * antiguo; las líneas conservan el orden recibido.
     */
    public static List<ReporteVentas.Ticket> tickets(ZoneId zona, List<FilaLinea> lineas, List<FilaMetodo> metodos) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Map<Integer, List<ReporteVentas.PagoTicket>> pagosPorVenta = new LinkedHashMap<>();
        for (FilaMetodo m : metodos) {
            pagosPorVenta.computeIfAbsent(m.idVenta(), k -> new ArrayList<>())
                    .add(new ReporteVentas.PagoTicket(m.metodo(), dinero(m.monto())));
        }
        Map<Integer, List<FilaLinea>> porVenta = new LinkedHashMap<>();
        for (FilaLinea l : lineas) {
            porVenta.computeIfAbsent(l.idVenta(), k -> new ArrayList<>()).add(l);
        }
        List<ReporteVentas.Ticket> tickets = new ArrayList<>();
        for (Map.Entry<Integer, List<FilaLinea>> e : porVenta.entrySet()) {
            FilaLinea primera = e.getValue().get(0);
            List<ReporteVentas.LineaTicket> detalle = new ArrayList<>();
            long piezas = 0;
            for (FilaLinea l : e.getValue()) {
                piezas += l.cantidad();
                detalle.add(new ReporteVentas.LineaTicket(l.codigo(), l.producto(), l.cantidad(),
                        dinero(l.precio()), dinero(l.subtotal())));
            }
            List<ReporteVentas.PagoTicket> pagos = pagosPorVenta.getOrDefault(e.getKey(), new ArrayList<>());
            tickets.add(new ReporteVentas.Ticket(e.getKey(), formato.format(primera.fecha().atZone(zona)), pagos,
                    piezas, dinero(primera.total()), detalle));
        }
        tickets.sort(Comparator.comparingInt(ReporteVentas.Ticket::getNumero).reversed());
        return tickets;
    }

    private static BigDecimal dinero(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP);
    }

    private static String nombreCategoria(String categoria) {
        return categoria == null || categoria.isBlank() ? "Sin categoría" : categoria;
    }

    private static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
