package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Resultado del reporte de ventas por periodo (PBI-REP-US2). Solo cuenta ventas completadas. */
public class ReporteVentas implements Serializable {

    /** Ventas de un día. */
    public static class Dia implements Serializable {
        private final LocalDate fecha;
        private final String etiqueta;
        private final int ventas;
        private final BigDecimal total;

        public Dia(LocalDate fecha, String etiqueta, int ventas, BigDecimal total) {
            this.fecha = fecha;
            this.etiqueta = etiqueta;
            this.ventas = ventas;
            this.total = total;
        }

        public LocalDate getFecha() { return fecha; }
        public String getEtiqueta() { return etiqueta; }
        public int getVentas() { return ventas; }
        public BigDecimal getTotal() { return total; }
    }

    /** Lo cobrado con un método de pago (el efectivo ya descuenta el cambio entregado). */
    public static class Metodo implements Serializable {
        private final String metodo;
        private final BigDecimal monto;
        private final BigDecimal porcentaje;

        public Metodo(String metodo, BigDecimal monto, BigDecimal porcentaje) {
            this.metodo = metodo;
            this.monto = monto;
            this.porcentaje = porcentaje;
        }

        public String getMetodo() { return metodo; }
        public BigDecimal getMonto() { return monto; }
        public BigDecimal getPorcentaje() { return porcentaje; }
    }

    /** Ventas de una categoría. */
    public static class Categoria implements Serializable {
        private final String categoria;
        private final long piezas;
        private final BigDecimal total;

        public Categoria(String categoria, long piezas, BigDecimal total) {
            this.categoria = categoria;
            this.piezas = piezas;
            this.total = total;
        }

        public String getCategoria() { return categoria; }
        public long getPiezas() { return piezas; }
        public BigDecimal getTotal() { return total; }
    }

    /** Ventas de un producto. */
    public static class Producto implements Serializable {
        private final String codigo;
        private final String nombre;
        private final String categoria;
        private final long piezas;
        private final BigDecimal total;

        public Producto(String codigo, String nombre, String categoria, long piezas, BigDecimal total) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.piezas = piezas;
            this.total = total;
        }

        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public String getCategoria() { return categoria; }
        public long getPiezas() { return piezas; }
        public BigDecimal getTotal() { return total; }
    }

    /** Un producto dentro de un ticket. */
    public static class LineaTicket implements Serializable {
        private final String codigo;
        private final String nombre;
        private final int cantidad;
        private final BigDecimal precio;
        private final BigDecimal subtotal;

        public LineaTicket(String codigo, String nombre, int cantidad, BigDecimal precio, BigDecimal subtotal) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
            this.subtotal = subtotal;
        }

        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public int getCantidad() { return cantidad; }
        public BigDecimal getPrecio() { return precio; }
        public BigDecimal getSubtotal() { return subtotal; }
    }

    /** Lo pagado con un método en un ticket. */
    public static class PagoTicket implements Serializable {
        private final String metodo;
        private final BigDecimal monto;

        public PagoTicket(String metodo, BigDecimal monto) {
            this.metodo = metodo;
            this.monto = monto;
        }

        public String getMetodo() { return metodo; }
        public BigDecimal getMonto() { return monto; }
        /** Nombre legible: "EFECTIVO" se muestra como "Efectivo". */
        public String getEtiqueta() {
            if (metodo == null || metodo.isEmpty()) {
                return "—";
            }
            return metodo.substring(0, 1).toUpperCase() + metodo.substring(1).toLowerCase();
        }
    }

    /** Una venta (ticket) con sus productos, para mostrarla como contenedor. */
    public static class Ticket implements Serializable {
        private final int numero;
        private final String fecha;
        private final List<PagoTicket> pagos;
        private final long piezas;
        private final BigDecimal total;
        private final List<LineaTicket> lineas;

        public Ticket(int numero, String fecha, List<PagoTicket> pagos, long piezas, BigDecimal total,
                      List<LineaTicket> lineas) {
            this.numero = numero;
            this.fecha = fecha;
            this.pagos = pagos;
            this.piezas = piezas;
            this.total = total;
            this.lineas = lineas;
        }

        public int getNumero() { return numero; }
        /** Folio con el mismo formato que la pantalla de ventas y la bitácora: #000003. */
        public String getFolio() { return String.format("#%06d", numero); }
        /** Fecha y hora local, dd/MM/yyyy HH:mm. */
        public String getFecha() { return fecha; }
        /** Cuánto se pagó con cada método (varios si el pago fue mixto). */
        public List<PagoTicket> getPagos() { return pagos; }
        /** True si se pagó con más de un método. */
        public boolean isMixto() { return pagos.size() > 1; }
        /** Pagos en una sola línea para el CSV: "Efectivo 50.00 | Transferencia 50.00". */
        public String getResumenPagos() {
            if (pagos.isEmpty()) {
                return "—";
            }
            StringBuilder sb = new StringBuilder();
            for (PagoTicket pg : pagos) {
                if (sb.length() > 0) {
                    sb.append(" | ");
                }
                sb.append(pg.getMetodo()).append(' ').append(pg.getMonto().toPlainString());
            }
            return sb.toString();
        }
        public long getPiezas() { return piezas; }
        public BigDecimal getTotal() { return total; }
        public List<LineaTicket> getLineas() { return lineas; }
    }

    private final PeriodoReporte periodo;
    private final int numeroVentas;
    private final long piezas;
    private final BigDecimal totalVendido;
    private final BigDecimal ticketPromedio;
    private final List<Dia> dias;
    private final List<Metodo> metodos;
    private final List<Categoria> categorias;
    private final List<Producto> productos;
    private final int ventasNoCompletadas;
    private final BigDecimal montoNoCompletadas;
    private List<Ticket> tickets = new ArrayList<>();

    public ReporteVentas(PeriodoReporte periodo, int numeroVentas, long piezas, BigDecimal totalVendido,
                         BigDecimal ticketPromedio, List<Dia> dias, List<Metodo> metodos,
                         List<Categoria> categorias, List<Producto> productos,
                         int ventasNoCompletadas, BigDecimal montoNoCompletadas) {
        this.periodo = periodo;
        this.numeroVentas = numeroVentas;
        this.piezas = piezas;
        this.totalVendido = totalVendido;
        this.ticketPromedio = ticketPromedio;
        this.dias = dias;
        this.metodos = metodos;
        this.categorias = categorias;
        this.productos = productos;
        this.ventasNoCompletadas = ventasNoCompletadas;
        this.montoNoCompletadas = montoNoCompletadas;
    }

    public PeriodoReporte getPeriodo() { return periodo; }
    public int getNumeroVentas() { return numeroVentas; }
    public long getPiezas() { return piezas; }
    public BigDecimal getTotalVendido() { return totalVendido; }
    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public List<Dia> getDias() { return dias; }
    public List<Metodo> getMetodos() { return metodos; }
    public List<Categoria> getCategorias() { return categorias; }
    /** Productos de más a menos vendidos (por piezas). */
    public List<Producto> getProductos() { return productos; }
    /** Ventas del periodo que no están completadas (anuladas): no suman a lo vendido. */
    public int getVentasNoCompletadas() { return ventasNoCompletadas; }
    public BigDecimal getMontoNoCompletadas() { return montoNoCompletadas; }

    /** Ventas completadas del periodo, una por ticket, de la más reciente a la más antigua. */
    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    /** True si el periodo no tuvo ventas completadas. */
    public boolean isSinVentas() { return numeroVentas == 0; }
}
