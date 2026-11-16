package ui;

import helper.VentaHelper;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named("ventaUI")
@ViewScoped
public class VentaBeanUI implements Serializable {

    private final VentaHelper helper = new VentaHelper();

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
    private boolean generarPdf;

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
    }

    public void calcularPagos() {
        BigDecimal ef = efectivo != null ? efectivo : BigDecimal.ZERO;
        BigDecimal tr = transferencia != null ? transferencia : BigDecimal.ZERO;
        BigDecimal ta = tarjeta != null ? tarjeta : BigDecimal.ZERO;

        comisionTarjeta = ta.multiply(new BigDecimal("0.045")).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalIngresado = ef.add(tr).add(ta);
        cambio = totalIngresado.subtract(total).subtract(comisionTarjeta);

        if (cambio.compareTo(BigDecimal.ZERO) < 0) cambio = BigDecimal.ZERO;
    }

    public boolean isPagoCompleto() {
        BigDecimal ef = efectivo != null ? efectivo : BigDecimal.ZERO;
        BigDecimal tr = transferencia != null ? transferencia : BigDecimal.ZERO;
        BigDecimal ta = tarjeta != null ? tarjeta : BigDecimal.ZERO;
        return ef.add(tr).add(ta).compareTo(total) >= 0;
    }

    public void confirmarVenta() {
        if (!isPagoCompleto()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Pago incompleto", "El monto ingresado no cubre el total.");
            return;
        }

        try {
            Venta venta = new Venta();
            venta.setTotal(total);
            venta.setIdCliente(clienteSeleccionado);

            Usuario u = new Usuario();
            u.setId(1);
            venta.setIdUsuario(u);

            List<PagoVenta> pagos = new ArrayList<>();
            if (efectivo != null && efectivo.compareTo(BigDecimal.ZERO) > 0) pagos.add(crearPago(venta, "EFECTIVO", efectivo, BigDecimal.ZERO));
            if (transferencia != null && transferencia.compareTo(BigDecimal.ZERO) > 0) pagos.add(crearPago(venta, "TRANSFERENCIA", transferencia, BigDecimal.ZERO));
            if (tarjeta != null && tarjeta.compareTo(BigDecimal.ZERO) > 0) pagos.add(crearPago(venta, "TARJETA", tarjeta, comisionTarjeta));

            helper.registrarVenta(venta, listaDetalles, pagos);

            mensaje(FacesMessage.SEVERITY_INFO, "Venta exitosa", "La venta " + folioActual + " se registró correctamente.");
            init();
            PrimeFaces.current().executeScript("PF('dlgCobro').hide()");

        } catch (Exception e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al registrar", e.getMessage());
        }
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
        comisionTarjeta = BigDecimal.ZERO; cambio = BigDecimal.ZERO; generarPdf = false;
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
    public boolean isGenerarPdf() { return generarPdf; }
    public void setGenerarPdf(boolean generarPdf) { this.generarPdf = generarPdf; }
}
