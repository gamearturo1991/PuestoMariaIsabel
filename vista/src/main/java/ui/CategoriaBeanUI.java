package ui;

import helper.CategoriaHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.Categoria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pantalla de categorías de productos (PBI-PRO-US6): registrar, modificar y eliminar. */
@Named("categoriaUI")
@SessionScoped
public class CategoriaBeanUI implements Serializable {

    private final CategoriaHelper helper = new CategoriaHelper();

    private List<Categoria> categorias = new ArrayList<>();
    private Map<Integer, Long> productos = new HashMap<>();

    /** Categoría que se está registrando o editando en el formulario. */
    private Categoria categoria;

    @PostConstruct
    public void init() {
        nueva();
        cargar();
    }

    public void cargar() {
        categorias = helper.listar();
        productos = helper.productosPorCategoria();
    }

    public void nueva() {
        categoria = new Categoria();
    }

    public void editar(Categoria seleccionada) {
        Categoria copia = new Categoria();
        copia.setId(seleccionada.getId());
        copia.setNombre(seleccionada.getNombre());
        categoria = copia;
    }

    public void guardar() {
        try {
            boolean esNueva = categoria.getId() == null;
            if (esNueva) {
                helper.guardar(categoria);
            } else {
                helper.modificar(categoria);
            }
            mensaje(FacesMessage.SEVERITY_INFO, esNueva ? "Categoría registrada" : "Categoría modificada",
                    categoria.getNombre());
            PrimeFaces.current().ajax().addCallbackParam("guardado", true);
            nueva();
            cargar();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo guardar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al guardar la categoría", ex.getMessage());
        }
    }

    public void eliminar(Categoria seleccionada) {
        try {
            helper.eliminar(seleccionada);
            mensaje(FacesMessage.SEVERITY_INFO, "Categoría eliminada", seleccionada.getNombre());
            cargar();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo eliminar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al eliminar la categoría", ex.getMessage());
        }
    }

    /** Número de productos (activos y deshabilitados) de una categoría. */
    public long productosDe(Categoria c) {
        Long n = c == null ? null : productos.get(c.getId());
        return n == null ? 0 : n;
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
