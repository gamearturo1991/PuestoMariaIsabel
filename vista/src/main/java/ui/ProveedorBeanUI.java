package ui;

import Puesto.maria.isabel.delegate.AccionBitacora;
import helper.BitacoraHelper;
import helper.ProveedorHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.Proveedor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pantalla de proveedores: registrar, modificar, activar/desactivar y eliminar. */
@Named("proveedorUI")
@SessionScoped
public class ProveedorBeanUI implements Serializable {

    private final ProveedorHelper helper = new ProveedorHelper();
    private final BitacoraHelper bitacoraHelper = new BitacoraHelper();

    @Inject
    private LoginBeanUI loginUI;

    /** Valores del proveedor al abrir la edición. */
    private Map<String, Object> antes;

    private List<Proveedor> proveedores = new ArrayList<>();
    private Map<Integer, Long> productos = new HashMap<>();

    /** Proveedor que se está registrando o editando en el formulario. */
    private Proveedor proveedor;

    @PostConstruct
    public void init() {
        nuevo();
        cargar();
    }

    public void cargar() {
        proveedores = helper.listar();
        productos = helper.productosPorProveedor();
    }

    public void nuevo() {
        proveedor = new Proveedor();
        proveedor.setActivo(true);
    }

    public void editar(Proveedor seleccionado) {
        Proveedor copia = new Proveedor();
        copia.setId(seleccionado.getId());
        copia.setNombre(seleccionado.getNombre());
        copia.setEmpresa(seleccionado.getEmpresa());
        copia.setCorreo(seleccionado.getCorreo());
        copia.setTelefono(seleccionado.getTelefono());
        copia.setActivo(seleccionado.getActivo());
        proveedor = copia;
        antes = camposProveedor(copia);
    }

    public void guardar() {
        try {
            boolean esNuevo = proveedor.getId() == null;
            if (esNuevo) {
                helper.guardar(proveedor);
            } else {
                helper.modificar(proveedor);
            }
            if (esNuevo) {
                bitacoraHelper.registrarAlta(loginUI.getUsuario(), "proveedor", proveedor.getId(),
                        camposProveedor(proveedor));
            } else {
                bitacoraHelper.registrarCambio(loginUI.getUsuario(), "proveedor", proveedor.getId(), antes,
                        camposProveedor(proveedor));
            }
            mensaje(FacesMessage.SEVERITY_INFO, esNuevo ? "Proveedor registrado" : "Proveedor modificado",
                    proveedor.getNombre());
            PrimeFaces.current().ajax().addCallbackParam("guardado", true);
            nuevo();
            cargar();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo guardar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al guardar el proveedor", ex.getMessage());
        }
    }

    public void cambiarEstado(Proveedor seleccionado) {
        try {
            helper.cambiarEstado(seleccionado);
            boolean activo = Boolean.TRUE.equals(seleccionado.getActivo());
            bitacoraHelper.registrar(loginUI.getUsuario(),
                    activo ? AccionBitacora.ACTIVAR : AccionBitacora.DESACTIVAR, "proveedor", seleccionado.getId(),
                    "activo=" + (activo ? "No" : "Sí"), "activo=" + (activo ? "Sí" : "No"), null);
            mensaje(FacesMessage.SEVERITY_INFO,
                    Boolean.TRUE.equals(seleccionado.getActivo()) ? "Proveedor activado" : "Proveedor desactivado",
                    seleccionado.getNombre());
            cargar();
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cambiar el estado", ex.getMessage());
        }
    }

    public void eliminar(Proveedor seleccionado) {
        try {
            helper.eliminar(seleccionado);
            bitacoraHelper.registrarBaja(loginUI.getUsuario(), "proveedor", seleccionado.getId(),
                    camposProveedor(seleccionado));
            mensaje(FacesMessage.SEVERITY_INFO, "Proveedor eliminado", seleccionado.getNombre());
            cargar();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo eliminar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al eliminar el proveedor", ex.getMessage());
        }
    }

    /** Número de productos de un proveedor. */
    public long productosDe(Proveedor p) {
        Long n = p == null ? null : productos.get(p.getId());
        return n == null ? 0 : n;
    }

    private static Map<String, Object> camposProveedor(Proveedor p) {
        return BitacoraHelper.campos(
                "nombre", p.getNombre(),
                "empresa", p.getEmpresa(),
                "correo", p.getCorreo(),
                "telefono", p.getTelefono(),
                "activo", p.getActivo());
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}