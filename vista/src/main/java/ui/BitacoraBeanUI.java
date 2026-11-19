package ui;

import Puesto.maria.isabel.delegate.DelegateBitacora;
import Puesto.maria.isabel.delegate.RegistroBitacora;
import helper.BitacoraHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import puesto.maria.entity.Usuario;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla Bitácora (PBI-REP-US5): consulta de los movimientos del sistema, lo más reciente primero.
 * Es de solo lectura: no hay botones para editar ni borrar movimientos.
 */
@Named("bitacoraUI")
@SessionScoped
public class BitacoraBeanUI implements Serializable {

    private final BitacoraHelper helper = new BitacoraHelper();

    private LocalDate desde;
    private LocalDate hasta;
    private Integer usuarioId;
    private String accion;
    private String tabla;

    private List<RegistroBitacora> registros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<String> acciones = new ArrayList<>();
    private List<String> tablas = new ArrayList<>();

    @PostConstruct
    public void init() {
        restablecerFiltros();
    }

    /** Se ejecuta cada vez que se abre la pantalla (no en las peticiones ajax). */
    public void alAbrir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null || fc.isPostback() || fc.getResponseComplete()) {
            return;
        }
        restablecerFiltros();
        consultar();
    }

    /** Vuelve a los últimos 7 días y quita los filtros. */
    public void limpiar() {
        restablecerFiltros();
        consultar();
    }

    private void restablecerFiltros() {
        hasta = LocalDate.now();
        desde = hasta.minusDays(6);
        usuarioId = null;
        accion = null;
        tabla = null;
    }

    public void consultar() {
        try {
            cargarFiltros();
            registros = helper.consultar(desde, hasta, usuarioId, accion, tabla);
            if (registros.size() >= DelegateBitacora.MAX_RESULTADOS) {
                mensaje(FacesMessage.SEVERITY_INFO, "Resultados limitados",
                        "Se muestran los " + DelegateBitacora.MAX_RESULTADOS
                                + " movimientos más recientes. Acota las fechas o usa los filtros.");
            }
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo consultar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al consultar la bitácora", ex.getMessage());
        }
    }

    private void cargarFiltros() {
        usuarios = helper.usuarios();
        acciones = helper.acciones();
        tablas = helper.tablas();
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    /* ======================== getters y setters ======================== */

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

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public List<RegistroBitacora> getRegistros() {
        return registros;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<String> getAcciones() {
        return acciones;
    }

    public List<String> getTablas() {
        return tablas;
    }
}
