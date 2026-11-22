package ui;

import Puesto.maria.isabel.delegate.AccionBitacora;
import helper.BitacoraHelper;
import helper.DevolucionHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.DetalleVenta;
import puesto.maria.entity.Devolucion;
import puesto.maria.entity.Usuario;
import puesto.maria.entity.Venta;
import puesto.maria.persistence.dao.LineaDevolucion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("devolucionUI")
@SessionScoped
public class DevolucionBeanUI implements Serializable {

    /** Un renglón de la venta con las piezas que se van a devolver. */
    public static class Linea implements Serializable {
        private final DetalleVenta detalle;
        private Integer cantidad = 0;
        /** True: la pieza regresa al inventario. False: se registra como merma. */
        private boolean revendible = true;

        public Linea(DetalleVenta detalle) {
            this.detalle = detalle;
        }

        public Integer getIdDetalle() { return detalle.getId(); }
        public String getNombre() { return detalle.getIdProducto().getNombre(); }
        public String getCodigo() { return detalle.getIdProducto().getCodigoCorto(); }
        public int getVendidas() { return detalle.getCantidad(); }
        public BigDecimal getPrecio() { return detalle.getPrecioUnitario(); }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public boolean isRevendible() { return revendible; }
        public void setRevendible(boolean revendible) { this.revendible = revendible; }

        /** Dinero a reembolsar por las piezas capturadas. */
        public BigDecimal getImporte() {
            return cantidad == null || cantidad < 1 ? BigDecimal.ZERO
                    : detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));
        }
    }

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DevolucionHelper helper = new DevolucionHelper();
    private final BitacoraHelper bitacoraHelper = new BitacoraHelper();

    @Inject
    private LoginBeanUI loginUI;

    private Integer folio;
    private Venta venta;      // null mientras no se haya encontrado una venta
    private List<Linea> lineas = new ArrayList<>();
    /** TOTAL o PARCIAL */
    private String tipo;
    private String motivo;

    /** Busca la venta por folio; si no se puede devolver, avisa y no sigue. */
    public void continuar() {
        tipo = null;
        motivo = null;
        try {
            venta = helper.buscarVenta(folio);
            lineas = new ArrayList<>();
            for (DetalleVenta d : helper.detallesDeVenta(folio)) {
                lineas.add(new Linea(d));
            }
        } catch (IllegalArgumentException ex) {
            venta = null;
            lineas = new ArrayList<>();
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo abrir la venta", ex.getMessage());
        } catch (RuntimeException ex) {
            venta = null;
            lineas = new ArrayList<>();
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al buscar la venta", ex.getMessage());
        }
    }

    /** Al elegir total se llenan todas las piezas; al elegir parcial se empieza en cero. */
    public void cambiarTipo() {
        for (Linea l : lineas) {
            l.setCantidad("TOTAL".equals(tipo) ? l.getVendidas() : 0);
        }
    }

    /** Valida lo capturado y abre la ventana de confirmación. */
    public void pedirConfirmacion() {
        if (venta == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta la venta", "Busca primero la venta por su folio.");
            return;
        }
        if (tipo == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta elegir", "Elige si se devuelve toda la venta o solo algunos productos.");
            return;
        }
        if (getPiezasADevolver() < 1) {
            mensaje(FacesMessage.SEVERITY_WARN, "Faltan piezas", "Indica cuántas piezas se devuelven.");
            return;
        }
        for (Linea l : lineas) {
            if (l.getCantidad() != null && l.getCantidad() > l.getVendidas()) {
                mensaje(FacesMessage.SEVERITY_WARN, "Cantidad mayor a la vendida",
                        "De " + l.getNombre() + " solo se vendieron " + l.getVendidas() + " pieza(s).");
                return;
            }
        }
        if (motivo == null || motivo.isBlank()) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta el motivo", "Escribe el motivo de la devolución.");
            return;
        }
        PrimeFaces.current().executeScript("PF('dlgConfirmarDevolucion').show()");
    }

    /** Se confirmó: guarda la devolución, regresa al inventario lo revendible y manda a merma lo demás. */
    public void registrar() {
        try {
            if (venta == null) {
                throw new IllegalArgumentException("Busca primero la venta que se va a devolver.");
            }
            List<LineaDevolucion> enviadas = new ArrayList<>();
            for (Linea l : lineas) {
                if (l.getCantidad() != null && l.getCantidad() > 0) {
                    enviadas.add(new LineaDevolucion(l.getIdDetalle(), l.getCantidad(), l.isRevendible()));
                }
            }
            // Se calculan antes de limpiar, porque limpiar() borra lo capturado.
            int regresadas = getPiezasRevendibles();
            int mermadas = getPiezasNoRevendibles();
            Integer idVenta = venta.getId();
            Integer idUsuario = loginUI.getUsuario().getId();

            Devolucion d = helper.registrar(idVenta, motivo, idUsuario, enviadas);

            PrimeFaces.current().executeScript("PF('dlgConfirmarDevolucion').hide()");
            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.DEVOLUCION, "devolucion",
                    d.getId(), null,
                    BitacoraHelper.texto(BitacoraHelper.campos("venta", idVenta,
                            "monto", d.getMontoReembolsado(),
                            "piezas al inventario", regresadas, "piezas a merma", mermadas)),
                    motivo);

            String detalle = "Venta #" + idVenta + ": reembolsar "
                    + String.format("$%,.2f", d.getMontoReembolsado()) + ". "
                    + regresadas + " pieza(s) regresaron al inventario"
                    + (mermadas > 0 ? " y " + mermadas + " se registraron como merma." : ".");
            mensaje(FacesMessage.SEVERITY_INFO, "Devolución registrada", detalle);
            limpiar();
        } catch (IllegalArgumentException ex) {
            PrimeFaces.current().executeScript("PF('dlgConfirmarDevolucion').hide()");
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo registrar la devolución", ex.getMessage());
        } catch (RuntimeException ex) {
            PrimeFaces.current().executeScript("PF('dlgConfirmarDevolucion').hide()");
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al registrar la devolución", ex.getMessage());
        }
    }

    public void limpiar() {
        folio = null;
        venta = null;
        lineas = new ArrayList<>();
        tipo = null;
        motivo = null;
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    /** True cuando ya se encontró una venta válida. */
    public boolean isFolioListo() {
        return venta != null;
    }

    public String getFechaVenta() {
        return venta == null || venta.getFechaHora() == null ? ""
                : venta.getFechaHora().atZone(ZoneId.systemDefault()).format(FORMATO_FECHA);
    }

    public String getUsuarioVenta() {
        if (venta == null) {
            return "";
        }
        Usuario u = venta.getIdUsuario();
        return u.getNombreCompleto() != null && !u.getNombreCompleto().isBlank()
                ? u.getNombreCompleto() : u.getNombreUsuario();
    }

    public BigDecimal getTotalVenta() {
        return venta == null ? BigDecimal.ZERO : venta.getTotal();
    }

    /** Piezas capturadas para devolver, en todos los renglones. */
    public int getPiezasADevolver() {
        int piezas = 0;
        for (Linea l : lineas) {
            if (l.getCantidad() != null && l.getCantidad() > 0) {
                piezas += l.getCantidad();
            }
        }
        return piezas;
    }

    /** Piezas capturadas que regresan al inventario. */
    public int getPiezasRevendibles() {
        int piezas = 0;
        for (Linea l : lineas) {
            if (l.isRevendible() && l.getCantidad() != null && l.getCantidad() > 0) {
                piezas += l.getCantidad();
            }
        }
        return piezas;
    }

    /** Piezas capturadas que se registran como merma. */
    public int getPiezasNoRevendibles() {
        return getPiezasADevolver() - getPiezasRevendibles();
    }

    /** Dinero total a reembolsar con lo capturado. */
    public BigDecimal getMontoReembolso() {
        BigDecimal monto = BigDecimal.ZERO;
        for (Linea l : lineas) {
            monto = monto.add(l.getImporte());
        }
        return monto;
    }

    public String getMensajeConfirmacion() {
        return venta == null ? "" : "¿Registrar la devolución de " + getPiezasADevolver()
                + " pieza(s) de la venta #" + venta.getId() + "?";
    }

    public Integer getFolio() { return folio; }
    public void setFolio(Integer folio) { this.folio = folio; }

    public List<Linea> getLineas() { return lineas; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}