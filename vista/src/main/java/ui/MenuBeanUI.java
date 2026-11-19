package ui;

import Puesto.maria.isabel.delegate.ExistenciaProducto;
import Puesto.maria.isabel.delegate.AccionBitacora;
import helper.BitacoraHelper;
import helper.InventarioHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Bean de la plantilla base (sidebar + cabecera).
 * Lo usa /WEB-INF/templates/layout.xhtml; cada pantalla tiene su propio bean.
 */
@Named("menuUI")
@SessionScoped
public class MenuBeanUI implements Serializable {

    @Inject
    private LoginBeanUI loginUI;

    private List<ItemMenu> items;

    private final InventarioHelper inventarioHelper = new InventarioHelper();
    private final BitacoraHelper bitacoraHelper = new BitacoraHelper();

    /** Alertas de stock (agotados o con stock bajo), consultadas a lo más cada 10 segundos para no cargar la base. */
    private static final long VIGENCIA_ALERTAS_MS = 10_000;
    private int alertas = 0;
    private long alertasConsultadasEn = 0;

    /** Aviso de stock bajo que se muestra una sola vez después de iniciar sesión (PBI-PRO-US2). */
    private boolean avisoMostrado = false;
    private boolean mostrarAviso = false;
    private List<ExistenciaProducto> listaAviso = new ArrayList<>();

    @PostConstruct
    public void init() {
        items = new ArrayList<>();
        // Cuando una pantalla exista, cambia su "disponible" a true y pon su url.
        items.add(new ItemMenu("Ventas", "/venta.xhtml", true));
        items.add(new ItemMenu("Productos", "/productos.xhtml", true));
        items.add(new ItemMenu("Categorías", "/categorias.xhtml", true));
        items.add(new ItemMenu("Inventario y alertas", "/inventario.xhtml", true));
        items.add(new ItemMenu("Entradas de mercancía", "/entradas.xhtml", true));
        items.add(new ItemMenu("Mermas y devoluciones", "/mermas.xhtml", true));
        items.add(new ItemMenu("Clientes y facturación", "/clientes.xhtml", false));
        items.add(new ItemMenu("Proveedores", "/proveedores.xhtml", true));
        items.add(new ItemMenu("Reportes y corte", "/reportes.xhtml", false));
        items.add(new ItemMenu("Bitácora", "/bitacora.xhtml", true));
    }

    /** Si no hay sesión iniciada, manda al login (se llama en preRenderView). */
    public void verificarSesion() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null || fc.getResponseComplete()) {
            return;
        }
        if (loginUI == null || loginUI.getUsuario() == null || loginUI.getUsuario().getId() == null) {
            fc.getApplication().getNavigationHandler()
                    .handleNavigation(fc, null, "/login.xhtml?faces-redirect=true");
        }
    }

    public String cerrarSesion() {
        if (loginUI != null && loginUI.getUsuario() != null && loginUI.getUsuario().getId() != null) {
            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.CIERRE_SESION, "usuario",
                    loginUI.getUsuario().getId(), null, null, null);
        }
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean esActivo(ItemMenu item) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null || fc.getViewRoot() == null) {
            return false;
        }
        return item.getUrl().equals(fc.getViewRoot().getViewId());
    }

    public String getNombreUsuario() {
        if (loginUI == null || loginUI.getUsuario() == null) {
            return "";
        }
        String completo = loginUI.getUsuario().getNombreCompleto();
        if (completo != null && !completo.isBlank()) {
            return completo;
        }
        String nombre = loginUI.getUsuario().getNombreUsuario();
        return nombre == null ? "" : nombre;
    }

    /** Formato del mockup: "Mié 04/11/2026". */
    public String getFechaHoy() {
        String texto = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", Locale.forLanguageTag("es-MX")));
        texto = texto.replace(".", "");
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    /** Número de productos agotados o con stock bajo, para el contador de la cabecera. */
    public int getAlertas() {
        long ahora = System.currentTimeMillis();
        if (ahora - alertasConsultadasEn > VIGENCIA_ALERTAS_MS) {
            try {
                alertas = inventarioHelper.contarAlertas();
            } catch (RuntimeException ex) {
                alertas = 0;
            }
            alertasConsultadasEn = ahora;
        }
        return alertas;
    }

    /** Fuerza que el contador se vuelva a calcular en la próxima lectura. */
    public void refrescarAlertas() {
        alertasConsultadasEn = 0;
    }

    /**
     * Se ejecuta al abrir cada página (no en las peticiones ajax). La primera vez después de iniciar sesión
     * prepara el aviso de stock bajo; en las siguientes páginas ya no se muestra.
     */
    public void prepararAviso() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null || fc.isPostback()) {
            return;
        }
        if (avisoMostrado || loginUI == null || loginUI.getUsuario() == null
                || loginUI.getUsuario().getId() == null) {
            mostrarAviso = false;
            return;
        }
        avisoMostrado = true;
        try {
            listaAviso = inventarioHelper.listarAlertas();
        } catch (RuntimeException ex) {
            listaAviso = new ArrayList<>();
        }
        mostrarAviso = !listaAviso.isEmpty();
    }

    public boolean isMostrarAviso() {
        return mostrarAviso;
    }

    public List<ExistenciaProducto> getListaAviso() {
        return listaAviso;
    }

    public int getAgotadosAviso() {
        int n = 0;
        for (ExistenciaProducto e : listaAviso) {
            if (e.getExistencia() <= 0) {
                n++;
            }
        }
        return n;
    }

    public List<ItemMenu> getItems() {
        return items;
    }

    /** Elemento del menú lateral. */
    public static class ItemMenu implements Serializable {
        private final String etiqueta;
        private final String url;
        private final boolean disponible;

        public ItemMenu(String etiqueta, String url, boolean disponible) {
            this.etiqueta = etiqueta;
            this.url = url;
            this.disponible = disponible;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public String getUrl() {
            return url;
        }

        public boolean isDisponible() {
            return disponible;
        }
    }
}