package ui;

import Puesto.maria.isabel.delegate.AccionBitacora;
import helper.BitacoraHelper;
import helper.VentaHelper;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named("ventaUI")
@ViewScoped
public class VentaBeanUI implements Serializable {

    private static final BigDecimal TASA_COMISION_TARJETA = new BigDecimal("0.045");

    @Inject
    private LoginBeanUI loginUI;

    private final VentaHelper helper = new VentaHelper();
    private final BitacoraHelper bitacoraHelper = new BitacoraHelper();

    private List<DetalleVenta> listaDetalles;
    private BigDecimal total;
    private int totalArticulos;
    private String folioActual;
    private List<Cliente> clientes;
    private Cliente clienteSeleccionado;
    private Producto productoSeleccionado;
    private Map<Integer, Long> existencias;
    private BigDecimal efectivo;
    private BigDecimal transferencia;
    private BigDecimal tarjeta;
    private BigDecimal comisionTarjeta;
    private BigDecimal cambio;
    private boolean mostrarTicket = true;
    private TicketVenta ticket;

    @PostConstruct
    public void init() {
        listaDetalles = new ArrayList<>();
        clientes = helper.listarClientes();
        clienteSeleccionado = null;
        existencias = helper.existencias();
        actualizarFolio();
        limpiarPagos();
        recalcularTotales();
    }

    private void actualizarFolio() {
        Integer nextId = helper.obtenerSiguienteFolio();
        folioActual = String.format("#%06d", nextId);
    }

    public List<Producto> completarProducto(String query) {
        return helper.buscarProductos(query, true, 10);
    }

    public void onProductoSeleccionado() {
        if (productoSeleccionado != null) {
            agregarProductoALista(productoSeleccionado);
            productoSeleccionado = null;
        }
    }

    public void agregarProductoALista(Producto p) {
        if (p == null) return;

        long stockDisponible = existencias.getOrDefault(p.getId(), 0L);

        for (DetalleVenta d : listaDetalles) {
            if (d.getIdProducto().getId().equals(p.getId())) {
                if (d.getCantidad() + 1 > stockDisponible) {
                    mensaje(FacesMessage.SEVERITY_WARN, "Sin stock", "No hay suficientes piezas de " + p.getNombre());
                    return;
                }
                d.setCantidad(d.getCantidad() + 1);
                recalcularDetalle(d);
                recalcularTotales();
                return;
            }
        }

        if (stockDisponible <= 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "Sin stock", "El producto está agotado.");
            return;
        }

        DetalleVenta nuevo = new DetalleVenta();
        nuevo.setIdProducto(p);
        nuevo.setCantidad(1);
        nuevo.setPrecioUnitario(p.getPrecioVenta());
        nuevo.setCostoUnitario(p.getCosto());
        recalcularDetalle(nuevo);
        listaDetalles.add(nuevo);
        recalcularTotales();
    }

    public void actualizarCantidad(DetalleVenta detalle) {
        if (detalle.getCantidad() <= 0) {
            listaDetalles.remove(detalle);
        } else {
            long stockDisponible = existencias.getOrDefault(detalle.getIdProducto().getId(), 0L);
            if (detalle.getCantidad() > stockDisponible) {
                mensaje(FacesMessage.SEVERITY_WARN, "Sin stock", "Solo hay " + stockDisponible + " piezas disponibles.");
                detalle.setCantidad((int) stockDisponible);
            }
            recalcularDetalle(detalle);
        }
        recalcularTotales();
    }

    public void eliminarDetalle(DetalleVenta detalle) {
        listaDetalles.remove(detalle);
        recalcularTotales();
    }

    private void recalcularDetalle(DetalleVenta d) {
        d.setSubtotal(d.getPrecioUnitario().multiply(new BigDecimal(d.getCantidad())));
    }

    private void recalcularTotales() {
        total = BigDecimal.ZERO;
        totalArticulos = 0;
        for (DetalleVenta d : listaDetalles) {
            total = total.add(d.getSubtotal());
            totalArticulos += d.getCantidad();
        }
    }

    public void abrirModalCobro() {
        if (listaDetalles.isEmpty()) {
            mensaje(FacesMessage.SEVERITY_WARN, "Venta vacía", "Agrega productos antes de cobrar.");
            return;
        }
        limpiarPagos();
        efectivo = total;
        calcularPagos();
    }

    private static BigDecimal valor(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    /** Suma de lo que el cliente entrega en los tres metodos de pago. */
    public BigDecimal getPagado() {
        return valor(efectivo).add(valor(transferencia)).add(valor(tarjeta));
    }

    /** Lo que todavia falta por cubrir (nunca negativo). */
    public BigDecimal getFalta() {
        BigDecimal falta = total.subtract(getPagado());
        return falta.signum() < 0 ? BigDecimal.ZERO : falta;
    }

    /**
     * El cambio es solo el excedente sobre el total. La comision de la terminal es un costo del negocio
     * (PBI-CAJ-US2): no se le resta al cliente.
     */
    public void calcularPagos() {
        comisionTarjeta = valor(tarjeta).multiply(TASA_COMISION_TARJETA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal excedente = getPagado().subtract(total);
        cambio = excedente.signum() > 0 ? excedente : BigDecimal.ZERO;
    }

    /** El cambio solo se puede dar de lo cobrado en efectivo. */
    public boolean isExcedeEfectivo() {
        return cambio.compareTo(valor(efectivo)) > 0;
    }

    public boolean isPagoCompleto() {
        return getPagado().compareTo(total) >= 0 && !isExcedeEfectivo();
    }

    public void confirmarVenta() {
        calcularPagos();
        if (!isPagoCompleto()) {
            String detalle = isExcedeEfectivo()
                    ? "El cambio solo se puede dar de lo pagado en efectivo."
                    : "El monto ingresado no cubre el total.";
            mensaje(FacesMessage.SEVERITY_ERROR, "Pago incompleto", detalle);
            return;
        }

        Usuario usuarioSesion = loginUI.getUsuario();
        if (usuarioSesion == null || usuarioSesion.getId() == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Sin sesión", "Inicia sesión para registrar ventas.");
            return;
        }

        try {
            Venta venta = new Venta();
            venta.setTotal(total);
            venta.setIdCliente(clienteSeleccionado);
            venta.setIdUsuario(usuarioSesion);

            // Se registra lo que realmente entra a caja: el cambio sale del efectivo.
            BigDecimal efectivoNeto = valor(efectivo).subtract(cambio);

            List<PagoVenta> pagos = new ArrayList<>();
            if (efectivoNeto.signum() > 0) pagos.add(crearPago(venta, "EFECTIVO", efectivoNeto, BigDecimal.ZERO));
            if (valor(transferencia).signum() > 0) pagos.add(crearPago(venta, "TRANSFERENCIA", transferencia, BigDecimal.ZERO));
            if (valor(tarjeta).signum() > 0) pagos.add(crearPago(venta, "TARJETA", tarjeta, comisionTarjeta));

            // Se arma el ticket antes de limpiar la pantalla.
            TicketVenta nuevoTicket = armarTicket(usuarioSesion, pagos, cambio);

            helper.registrarVenta(venta, new ArrayList<>(listaDetalles), pagos);
            bitacoraHelper.registrar(usuarioSesion, AccionBitacora.VENTA, "venta", venta.getId(), null,
                    BitacoraHelper.texto(BitacoraHelper.campos("folio", folioActual, "total", total,
                            "articulos", totalArticulos, "pagos", pagos.size())), null);

            mensaje(FacesMessage.SEVERITY_INFO, "Venta exitosa", "La venta " + folioActual + " se registró correctamente.");
            boolean abrirTicket = mostrarTicket;
            init();
            ticket = nuevoTicket;
            PrimeFaces.current().executeScript("PF('dlgCobro').hide()");
            if (abrirTicket) {
                PrimeFaces.current().executeScript("PF('dlgTicket').show()");
            }

        } catch (Exception e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al registrar", e.getMessage());
        }
    }

    private TicketVenta armarTicket(Usuario usuarioSesion, List<PagoVenta> pagos, BigDecimal cambioEntregado) {
        TicketVenta t = new TicketVenta();
        t.folio = folioActual;
        t.fecha = ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        t.cajero = usuarioSesion.getNombreUsuario();
        t.cliente = clienteSeleccionado != null ? clienteSeleccionado.getNombre() : "Público en general";
        for (DetalleVenta d : listaDetalles) {
            t.lineas.add(new String[]{
                    String.valueOf(d.getCantidad()),
                    d.getIdProducto().getNombre(),
                    formato(d.getPrecioUnitario()),
                    formato(d.getSubtotal())});
        }
        for (PagoVenta pv : pagos) {
            t.pagos.add(new String[]{nombreMetodo(pv.getMetodo()), formato(pv.getMonto())});
        }
        t.total = formato(total);
        t.cambio = formato(cambioEntregado);
        t.articulos = totalArticulos;
        return t;
    }

    private static String nombreMetodo(String metodo) {
        switch (metodo) {
            case "EFECTIVO": return "Efectivo";
            case "TRANSFERENCIA": return "Transferencia";
            case "TARJETA": return "Tarjeta";
            default: return metodo;
        }
    }

    private static String formato(BigDecimal v) {
        return String.format("$%,.2f", v);
    }

    /** Datos del ultimo ticket para mostrarlo e imprimirlo (o guardarlo como PDF desde la impresion). */
    public static class TicketVenta implements Serializable {
        private String folio;
        private String fecha;
        private String cajero;
        private String cliente;
        private String total;
        private String cambio;
        private int articulos;
        private final List<String[]> lineas = new ArrayList<>();
        private final List<String[]> pagos = new ArrayList<>();

        public String getFolio() { return folio; }
        public String getFecha() { return fecha; }
        public String getCajero() { return cajero; }
        public String getCliente() { return cliente; }
        public String getTotal() { return total; }
        public String getCambio() { return cambio; }
        public int getArticulos() { return articulos; }
        public List<String[]> getLineas() { return lineas; }
        public List<String[]> getPagos() { return pagos; }
    }

    private PagoVenta crearPago(Venta venta, String metodo, BigDecimal monto, BigDecimal comision) {
        PagoVenta p = new PagoVenta();
        p.setIdVenta(venta);
        p.setMetodo(metodo);
        p.setMonto(monto);
        p.setComision(comision);
        return p;
    }

    public void cancelarVenta() { init(); }

    private void limpiarPagos() {
        efectivo = BigDecimal.ZERO; transferencia = BigDecimal.ZERO; tarjeta = BigDecimal.ZERO;
        comisionTarjeta = BigDecimal.ZERO; cambio = BigDecimal.ZERO;
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    public List<DetalleVenta> getListaDetalles() { return listaDetalles; }
    public BigDecimal getTotal() { return total; }
    public int getTotalArticulos() { return totalArticulos; }
    public String getFolioActual() { return folioActual; }
    public List<Cliente> getClientes() { return clientes; }
    public Cliente getClienteSeleccionado() { return clienteSeleccionado; }
    public void setClienteSeleccionado(Cliente clienteSeleccionado) { this.clienteSeleccionado = clienteSeleccionado; }
    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) { this.productoSeleccionado = productoSeleccionado; }
    public BigDecimal getEfectivo() { return efectivo; }
    public void setEfectivo(BigDecimal efectivo) { this.efectivo = efectivo; }
    public BigDecimal getTransferencia() { return transferencia; }
    public void setTransferencia(BigDecimal transferencia) { this.transferencia = transferencia; }
    public BigDecimal getTarjeta() { return tarjeta; }
    public void setTarjeta(BigDecimal tarjeta) { this.tarjeta = tarjeta; }
    public BigDecimal getComisionTarjeta() { return comisionTarjeta; }
    public BigDecimal getCambio() { return cambio; }
    public boolean isMostrarTicket() { return mostrarTicket; }
    public void setMostrarTicket(boolean mostrarTicket) { this.mostrarTicket = mostrarTicket; }
    public TicketVenta getTicket() { return ticket; }
}
