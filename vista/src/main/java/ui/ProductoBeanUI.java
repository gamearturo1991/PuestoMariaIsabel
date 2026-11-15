package ui;

import helper.ProductoHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.Categoria;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Proveedor;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de productos (PBI-PRO): registrar (US1), modificar y deshabilitar (US4),
 * y buscar por código o nombre. El listado incluye los productos deshabilitados (en gris).
 */
@Named("productoUI")
@SessionScoped
public class ProductoBeanUI implements Serializable {

    private final ProductoHelper helper = new ProductoHelper();

    private List<Producto> productos = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();
    private List<Proveedor> proveedores = new ArrayList<>();

    /** Producto que se está registrando o editando en el formulario. */
    private Producto producto;
    private Integer categoriaId;
    private Integer proveedorId;

    /** Existencia por id de producto, calculada con los lotes cada vez que se recarga el listado. */
    private Map<Integer, Long> existencias = new HashMap<>();

    private String textoBusqueda;
    private boolean verDeshabilitados = true;

    @PostConstruct
    public void init() {
        categorias = helper.categorias();
        proveedores = helper.proveedores();
        nuevo();
        cargar();
    }

    /**
     * Recarga el listado. Muestra los productos (los deshabilitados solo si la casilla está marcada) y, si hay texto
     * en la barra de búsqueda, filtra por código o nombre sin distinguir mayúsculas ni acentos.
     */
    public void cargar() {
        List<Producto> todos = helper.listar();
        existencias = helper.existencias();
        String t = textoBusqueda == null ? "" : normalizar(textoBusqueda);
        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : todos) {
            if (!verDeshabilitados && !Boolean.TRUE.equals(p.getActivo())) {
                continue;
            }
            if (t.isEmpty() || normalizar(p.getNombre()).contains(t) || normalizar(p.getCodigoCorto()).contains(t)) {
                filtrados.add(p);
            }
        }
        productos = filtrados;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim();
    }

    public void buscar() {
        cargar();
    }

    public void limpiarBusqueda() {
        textoBusqueda = null;
        cargar();
    }

    /** Prepara el formulario para registrar un producto nuevo. */
    public void nuevo() {
        categorias = helper.categorias();
        producto = new Producto();
        producto.setActivo(true);
        categoriaId = null;
        proveedorId = null;
    }

    /** Carga un producto existente en el formulario para modificarlo. */
    public void editar(Producto seleccionado) {
        categorias = helper.categorias();
        producto = seleccionado;
        categoriaId = seleccionado.getIdCategoria() == null ? null : seleccionado.getIdCategoria().getId();
        proveedorId = seleccionado.getIdProveedor() == null ? null : seleccionado.getIdProveedor().getId();
    }

    /** Guarda: registra si el producto es nuevo, modifica si ya existe. */
    public void guardar() {
        try {
            producto.setIdCategoria(helper.categoria(categoriaId));
            producto.setIdProveedor(helper.proveedor(proveedorId));
            boolean esNuevo = producto.getId() == null;
            if (esNuevo) {
                helper.guardar(producto);
            } else {
                helper.modificar(producto);
            }
            mensaje(FacesMessage.SEVERITY_INFO, esNuevo ? "Producto registrado" : "Producto actualizado",
                    producto.getNombre());
            nuevo();
            cargar();
            PrimeFaces.current().ajax().addCallbackParam("guardado", true);
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo guardar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al guardar el producto", ex.getMessage());
        }
    }

    /** Baja lógica: el producto ya no aparece en búsquedas. Se reactiva desde Entradas de mercancía. */
    public void deshabilitar(Producto seleccionado) {
        try {
            helper.deshabilitar(seleccionado);
            mensaje(FacesMessage.SEVERITY_INFO, "Producto deshabilitado", seleccionado.getNombre());
            cargar();
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al deshabilitar el producto", ex.getMessage());
        }
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    /* ======================== getters y setters ======================== */

    public List<Producto> getProductos() {
        return productos;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }

    public boolean isVerDeshabilitados() {
        return verDeshabilitados;
    }

    public void setVerDeshabilitados(boolean verDeshabilitados) {
        this.verDeshabilitados = verDeshabilitados;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }

    /** Piezas que hay de un producto (suma de sus lotes); 0 si no tiene. */
    public long existenciaDe(Producto p) {
        Long n = p == null ? null : existencias.get(p.getId());
        return n == null ? 0 : n;
    }

    /** True si la existencia es igual o menor al stock mínimo (para resaltarla en la tabla). */
    public boolean existenciaBaja(Producto p) {
        return p != null && p.getStockMinimo() != null && existenciaDe(p) <= p.getStockMinimo();
    }
}
