package ui;

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

    /** Cantidad de alertas de inventario. TODO: conectar con Inventario y alertas. */
    private int alertas = 0;

    @PostConstruct
    public void init() {
        items = new ArrayList<>();
        // Cuando una pantalla exista, cambia su "disponible" a true y pon su url.
        items.add(new ItemMenu("Ventas", "/index.xhtml", true));
        items.add(new ItemMenu("Productos", "/productos.xhtml", false));
        items.add(new ItemMenu("Inventario y alertas", "/inventario.xhtml", false));
        items.add(new ItemMenu("Entradas de mercancía", "/entradas.xhtml", false));
        items.add(new ItemMenu("Mermas y devoluciones", "/mermas.xhtml", false));
        items.add(new ItemMenu("Clientes y facturación", "/clientes.xhtml", false));
        items.add(new ItemMenu("Proveedores", "/proveedores.xhtml", false));
        items.add(new ItemMenu("Reportes y corte", "/reportes.xhtml", false));
        items.add(new ItemMenu("Bitácora", "/bitacora.xhtml", false));
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

    public int getAlertas() {
        return alertas;
    }

    public void setAlertas(int alertas) {
        this.alertas = alertas;
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