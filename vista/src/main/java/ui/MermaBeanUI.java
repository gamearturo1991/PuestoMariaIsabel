package ui;

import Puesto.maria.isabel.delegate.RegistroMerma;
import Puesto.maria.isabel.delegate.ResultadoMerma;
import Puesto.maria.isabel.delegate.ResumenMotivo;
import Puesto.maria.isabel.delegate.AccionBitacora;
import helper.BitacoraHelper;
import helper.MermaHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de mermas (PBI-MER): registrar una merma con su motivo, consultar el historial por motivo y periodo
 * y anular (rescatar piezas) una merma hecha por error. Antes de mermar y antes de anular se pide confirmación.
 */
@Named("mermaUI")
@SessionScoped
public class MermaBeanUI implements Serializable {

    private static final int MAX_SUGERENCIAS = 20;

    private final BitacoraHelper bitacoraHelper = new BitacoraHelper();

    @Inject
    private LoginBeanUI loginUI;

    private final MermaHelper helper = new MermaHelper();

    private List<String> motivos = new ArrayList<>();

    /** Modo de la pantalla: REGISTRAR o HISTORIAL. */
    private String modo = "REGISTRAR";

    /* ---- registrar ---- */
    private Producto productoSeleccionado;
    private Integer cantidad;
    private String motivo;
    private String observaciones;
    private int existenciaActual;
    private BigDecimal perdidaEstimada = BigDecimal.ZERO;

    /* ---- historial ---- */
    private String filtroMotivo;
    private LocalDate desde;
    private LocalDate hasta;
    private List<RegistroMerma> registros = new ArrayList<>();
    private List<ResumenMotivo> totales = new ArrayList<>();
    private long piezasTotal;
    private BigDecimal perdidaTotal = BigDecimal.ZERO;

    /* ---- anulación ---- */
    private RegistroMerma registroAnulacion;
    private Integer cantidadAnular;
    private String descripcionAnulacion;

    @PostConstruct
    public void init() {
        motivos = helper.motivos();
        hasta = LocalDate.now();
        desde = hasta.withDayOfMonth(1);
    }

    /* ======================== registrar ======================== */

    /** Sugerencias de la barra de búsqueda: código exacto primero. */
    public List<Producto> completarProducto(String texto) {
        return helper.buscarProductos(texto, MAX_SUGERENCIAS);
    }

    /** Al elegir un producto se carga su existencia actual. */
    public void onProductoSeleccionado() {
        cantidad = null;
        perdidaEstimada = BigDecimal.ZERO;
        existenciaActual = productoSeleccionado == null ? 0 : helper.existencia(productoSeleccionado.getId());
    }

    /** Al cambiar la cantidad se recalcula la pérdida estimada (piezas por su costo). */
    public void onCantidadCambiada() {
        perdidaEstimada = productoSeleccionado == null ? BigDecimal.ZERO
                : helper.estimarPerdida(productoSeleccionado.getId(), cantidad);
    }

    /** Valida lo capturado y abre la ventana de confirmación; todavía no se descuenta nada. */
    public void pedirConfirmacion() {
        if (productoSeleccionado == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta el producto", "Selecciona el producto que se va a mermar.");
            return;
        }
        existenciaActual = helper.existencia(productoSeleccionado.getId());
        if (cantidad == null || cantidad < 1) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta la cantidad", "Indica cuántas piezas se van a mermar.");
            return;
        }
        if (cantidad > existenciaActual) {
            mensaje(FacesMessage.SEVERITY_WARN, "Cantidad mayor a la existencia",
                    "Solo hay " + existenciaActual + " pieza(s) de " + productoSeleccionado.getNombre() + ".");
            return;
        }
        if (motivo == null || motivo.isBlank()) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta el motivo", "Selecciona el motivo de la merma.");
            return;
        }
        perdidaEstimada = helper.estimarPerdida(productoSeleccionado.getId(), cantidad);
        PrimeFaces.current().executeScript("PF('dlgConfirmarMerma').show()");
    }

    /** Se confirmó: descuenta las piezas y registra la merma. */
    public void registrar() {
        try {
            ResultadoMerma r = helper.registrar(productoSeleccionado == null ? null : productoSeleccionado.getId(),
                    cantidad, motivo, observaciones, idUsuario());
            PrimeFaces.current().executeScript("PF('dlgConfirmarMerma').hide()");
            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.MERMA, "merma", null, null,
                    BitacoraHelper.texto(BitacoraHelper.campos("producto", r.getProducto().getNombre(),
                            "piezas", r.getPiezas(), "perdida", r.getPerdida(),
                            "existencia", r.getExistenciaRestante())), motivo);
            mensaje(FacesMessage.SEVERITY_INFO, "Merma registrada",
                    r.getPiezas() + " pieza(s) de " + r.getProducto().getNombre() + ". Pérdida: "
                            + dinero(r.getPerdida()) + ". Existencia actual: " + r.getExistenciaRestante() + " pzas.");
            if (r.isAlerta()) {
                mensaje(FacesMessage.SEVERITY_WARN, r.getEstado().getEtiqueta(),
                        r.getProducto().getNombre() + " quedó con " + r.getExistenciaRestante()
                                + " pza(s). Conviene reabastecerlo.");
            }
            limpiar();
        } catch (IllegalArgumentException ex) {
            PrimeFaces.current().executeScript("PF('dlgConfirmarMerma').hide()");
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo registrar la merma", ex.getMessage());
        } catch (RuntimeException ex) {
            PrimeFaces.current().executeScript("PF('dlgConfirmarMerma').hide()");
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al registrar la merma", ex.getMessage());
        }
    }

    /** Deja el formulario vacío para una nueva merma. */
    public void limpiar() {
        productoSeleccionado = null;
        cantidad = null;
        motivo = null;
        observaciones = null;
        existenciaActual = 0;
        perdidaEstimada = BigDecimal.ZERO;
    }

    /** Texto de la ventana de confirmación antes de mermar. */
    public String getMensajeConfirmacion() {
        if (productoSeleccionado == null || cantidad == null) {
            return "";
        }
        return "¿Mermar " + cantidad + " pieza(s) de " + productoSeleccionado.getNombre()
                + " por el motivo «" + motivo + "»?";
    }

    /* ======================== historial ======================== */

    /** Cambia entre "Registrar merma" y "Historial de mermas". */
    public void cambiarModo() {
        if ("HISTORIAL".equals(modo)) {
            consultar();
        }
    }

    public boolean isModoHistorial() {
        return "HISTORIAL".equals(modo);
    }

    /** Consulta las mermas del motivo y periodo elegidos, con sus totales. */
    public void consultar() {
        try {
            registros = helper.consultar(filtroMotivo, desde, hasta);
            totales = helper.totalesPorMotivo(registros);
            piezasTotal = 0;
            perdidaTotal = BigDecimal.ZERO;
            for (ResumenMotivo t : totales) {
                piezasTotal += t.getPiezas();
                perdidaTotal = perdidaTotal.add(t.getPerdida());
            }
        } catch (IllegalArgumentException ex) {
            registros = new ArrayList<>();
            totales = new ArrayList<>();
            piezasTotal = 0;
            perdidaTotal = BigDecimal.ZERO;
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo consultar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al consultar las mermas", ex.getMessage());
        }
    }

    /** Muestra el periodo del mes actual con todos los motivos. */
    public void limpiarFiltros() {
        filtroMotivo = null;
        hasta = LocalDate.now();
        desde = hasta.withDayOfMonth(1);
        consultar();
    }

    /* ======================== anulación ======================== */

    /** Prepara la ventana donde se capturan las piezas a rescatar y la descripción del error. */
    public void abrirAnulacion(RegistroMerma registro) {
        registroAnulacion = registro;
        cantidadAnular = null;
        descripcionAnulacion = null;
    }

    /** Valida lo capturado y abre la ventana de confirmación; todavía no se devuelve nada. */
    public void continuarAnulacion() {
        if (registroAnulacion == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta la merma", "Selecciona la merma que quieres anular.");
            return;
        }
        if (cantidadAnular == null || cantidadAnular < 1) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta la cantidad", "Indica cuántas piezas se van a rescatar.");
            return;
        }
        if (cantidadAnular > registroAnulacion.getPendientes()) {
            mensaje(FacesMessage.SEVERITY_WARN, "Cantidad mayor a la mermada",
                    "Solo quedan " + registroAnulacion.getPendientes() + " pieza(s) por rescatar.");
            return;
        }
        if (descripcionAnulacion == null || descripcionAnulacion.isBlank()) {
            mensaje(FacesMessage.SEVERITY_WARN, "Falta la descripción",
                    "Describe cuál fue el error o por qué se mermó por accidente.");
            return;
        }
        PrimeFaces.current().executeScript("PF('dlgAnular').hide();PF('dlgConfirmarAnular').show()");
    }

    /** Se confirmó: las piezas regresan al inventario y la anulación queda en el historial. */
    public void anular() {
        try {
            String producto = registroAnulacion == null ? "" : registroAnulacion.getProducto().getNombre();
            Integer idMerma = registroAnulacion == null || registroAnulacion.getIds().isEmpty()
                    ? null : registroAnulacion.getIds().get(0);
            helper.anular(registroAnulacion, cantidadAnular, descripcionAnulacion, idUsuario());
            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.ANULAR_MERMA, "merma", idMerma, null,
                    BitacoraHelper.texto(BitacoraHelper.campos("producto", producto,
                            "piezas rescatadas", cantidadAnular)), descripcionAnulacion);
            PrimeFaces.current().executeScript("PF('dlgConfirmarAnular').hide()");
            mensaje(FacesMessage.SEVERITY_INFO, "Merma anulada",
                    cantidadAnular + " pieza(s) de " + producto + " regresaron al inventario.");
            registroAnulacion = null;
            cantidadAnular = null;
            descripcionAnulacion = null;
            consultar();
        } catch (IllegalArgumentException ex) {
            PrimeFaces.current().executeScript("PF('dlgConfirmarAnular').hide()");
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo anular la merma", ex.getMessage());
        } catch (RuntimeException ex) {
            PrimeFaces.current().executeScript("PF('dlgConfirmarAnular').hide()");
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al anular la merma", ex.getMessage());
        }
    }

    /** Texto de la ventana de confirmación antes de anular. */
    public String getMensajeConfirmacionAnulacion() {
        if (registroAnulacion == null || cantidadAnular == null) {
            return "";
        }
        return "¿Rescatar " + cantidadAnular + " pieza(s) de " + registroAnulacion.getProducto().getNombre()
                + " mermadas por «" + registroAnulacion.getMotivo() + "»?";
    }

    /* ======================== utilidades ======================== */

    private Integer idUsuario() {
        return loginUI == null || loginUI.getUsuario() == null ? null : loginUI.getUsuario().getId();
    }

    private static String dinero(BigDecimal valor) {
        return String.format("$%,.2f", valor);
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    /* ======================== getters y setters ======================== */

    public List<String> getMotivos() {
        return motivos;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getExistenciaActual() {
        return existenciaActual;
    }

    public BigDecimal getPerdidaEstimada() {
        return perdidaEstimada;
    }

    public String getFiltroMotivo() {
        return filtroMotivo;
    }

    public void setFiltroMotivo(String filtroMotivo) {
        this.filtroMotivo = filtroMotivo;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }

    public List<RegistroMerma> getRegistros() {
        return registros;
    }

    public List<ResumenMotivo> getTotales() {
        return totales;
    }

    public long getPiezasTotal() {
        return piezasTotal;
    }

    public BigDecimal getPerdidaTotal() {
        return perdidaTotal;
    }

    public RegistroMerma getRegistroAnulacion() {
        return registroAnulacion;
    }

    public Integer getCantidadAnular() {
        return cantidadAnular;
    }

    public void setCantidadAnular(Integer cantidadAnular) {
        this.cantidadAnular = cantidadAnular;
    }

    public String getDescripcionAnulacion() {
        return descripcionAnulacion;
    }

    public void setDescripcionAnulacion(String descripcionAnulacion) {
        this.descripcionAnulacion = descripcionAnulacion;
    }
}
