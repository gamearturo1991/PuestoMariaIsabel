package ui;

import helper.VentaHelper;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.DetalleVenta;
import puesto.maria.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Named("ventaUI")
@ViewScoped
public class VentaBeanUI implements Serializable {

    private final VentaHelper helper = new VentaHelper();

    private List<DetalleVenta> listaDetalles;
    private BigDecimal total;
    private int totalArticulos;
    private Producto productoSeleccionado;
    private BigDecimal efectivo;
    private BigDecimal transferencia;
    private BigDecimal tarjeta;
    private BigDecimal comisionTarjeta;
    private BigDecimal cambio;
    private boolean generarPdf;
    @PostConstruct
    public void init() {
        listaDetalles = new ArrayList<>();
        total = BigDecimal.ZERO;
        totalArticulos = 0;
        limpiarPagos();
    }

    public List<Producto> completarProducto(String query) {
        return helper.buscarProductos(query, true, 10);
    }
    public void onProductoSeleccionado() {
        if (productoSeleccionado != null) {
            agregarProductoALista(productoSeleccionado);
            productoSeleccionado = null; // Limpiar la caja de búsqueda
        }
    }
    public void agregarProductoALista(Producto p) {
        if (p == null) return;

        for (DetalleVenta d : listaDetalles) {
            if (d.getIdProducto().getId().equals(p.getId())) {
                d.setCantidad(d.getCantidad() + 1);
                recalcularDetalle(d);
                recalcularTotales();
                return;
            }
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

        if (cambio.compareTo(BigDecimal.ZERO) < 0) {
            cambio = BigDecimal.ZERO;
        }
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

            helper.procesarVenta(listaDetalles);

            mensaje(FacesMessage.SEVERITY_INFO, "Venta exitosa", "Stock descontado correctamente.");
            init();
            PrimeFaces.current().executeScript("PF('dlgCobro').hide()");

        } catch (Exception e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al procesar la venta", e.getMessage());
        }
    }

    public void cancelarVenta() {
        init();
    }

    private void limpiarPagos() {
        efectivo = BigDecimal.ZERO;
        transferencia = BigDecimal.ZERO;
        tarjeta = BigDecimal.ZERO;
        comisionTarjeta = BigDecimal.ZERO;
        cambio = BigDecimal.ZERO;
        generarPdf = false;
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    public List<DetalleVenta> getListaDetalles() { return listaDetalles; }
    public BigDecimal getTotal() { return total; }
    public int getTotalArticulos() { return totalArticulos; }
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
