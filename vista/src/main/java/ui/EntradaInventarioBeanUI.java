package ui;

import Puesto.maria.isabel.delegate.ResultadoEntrada;
import Puesto.maria.isabel.delegate.AccionBitacora;
import helper.BitacoraHelper;
import helper.EntradaInventarioHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Proveedor;
import puesto.maria.persistence.dao.LineaEntrada;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de entradas de mercancía (PBI-PRO-US3).
 * Cada entrada guarda proveedor, producto, cantidad y costo de compra, y propone un precio de venta.
 */
@Named("entradaUI")
@SessionScoped
public class EntradaInventarioBeanUI implements Serializable {

    private static final int MAX_SUGERENCIAS = 20;

    private final BitacoraHelper bitacoraHelper = new BitacoraHelper();

    @Inject
    private LoginBeanUI loginUI;

    private final EntradaInventarioHelper helper = new EntradaInventarioHelper();

    private List<Proveedor> proveedores = new ArrayList<>();

    /** Modo de la pantalla: PRODUCTO (un solo producto) o PROVEEDOR (recibir un proveedor). */
    private String modo = "PRODUCTO";

    private Integer proveedorId;
    private List<LineaRecepcion> lineas = new ArrayList<>();
    private String filtroLineas;
    private Producto productoSeleccionado;
    private Integer cantidad;
    private BigDecimal costo;
    private BigDecimal precioVenta;
    private String observaciones;

    /** Existencia del producto seleccionado, antes de registrar la entrada. */
    private int existenciaActual;
    private BigDecimal precioSugerido;

    @PostConstruct
    public void init() {
        proveedores = helper.proveedores();
    }

    /** Sugerencias de la barra de búsqueda: código exacto primero; incluye productos deshabilitados. */
    public List<Producto> completarProducto(String texto) {
        return helper.buscarProductos(texto, MAX_SUGERENCIAS);
    }

    /** Al elegir un producto se cargan su costo y precio actuales y su existencia. */
    public void onProductoSeleccionado() {
        if (productoSeleccionado == null) {
            limpiarDatosProducto();
            return;
        }
        costo = productoSeleccionado.getCosto();
        precioVenta = productoSeleccionado.getPrecioVenta();
        precioSugerido = null;
        existenciaActual = helper.existencia(productoSeleccionado.getId());
        // Se propone el proveedor habitual del producto, pero se puede cambiar.
        if (proveedorId == null && productoSeleccionado.getIdProveedor() != null) {
            proveedorId = productoSeleccionado.getIdProveedor().getId();
        }
    }

    /** Al cambiar el costo se propone un precio que mantiene la proporción actual; se puede editar. */
    public void onCostoCambiado() {
        if (productoSeleccionado == null || costo == null) {
            precioSugerido = null;
            return;
        }
        precioSugerido = helper.precioSugerido(productoSeleccionado, costo);
        if (precioSugerido != null) {
            precioVenta = precioSugerido;
        }
    }

    /** Cambia entre "Un producto" y "Recibir un proveedor" y deja el formulario limpio. */
    public void cambiarModo() {
        limpiar();
    }

    public boolean isModoProveedor() {
        return "PROVEEDOR".equals(modo);
    }

    /** Al elegir un proveedor en el modo "Recibir un proveedor" se lista lo que él surte. */
    public void cargarProductosProveedor() {
        lineas = new ArrayList<>();
        filtroLineas = null;
        if (proveedorId == null) {
            return;
        }
        Map<Integer, Long> existencias = helper.existencias();
        for (Producto p : helper.productosDeProveedor(proveedorId)) {
            Long n = existencias.get(p.getId());
            lineas.add(new LineaRecepcion(p, n == null ? 0 : n));
        }
    }

    /** Filas visibles según el filtro de la tabla (sin distinguir mayúsculas ni acentos). */
    public List<LineaRecepcion> getLineasVisibles() {
        if (filtroLineas == null || filtroLineas.isBlank()) {
            return lineas;
        }
        String f = normalizar(filtroLineas);
        List<LineaRecepcion> visibles = new ArrayList<>();
        for (LineaRecepcion l : lineas) {
            if (normalizar(l.getProducto().getNombre()).contains(f)
                    || normalizar(l.getProducto().getCodigoCorto()).contains(f)) {
                visibles.add(l);
            }
        }
        return visibles;
    }

    public void filtrar() {
        // el filtro se aplica en getLineasVisibles(); el método solo dispara la actualización de la tabla
    }

    /** Al cambiar el costo de una fila se propone su precio; se puede editar. */
    public void onCostoLinea(LineaRecepcion l) {
        if (l.getCosto() == null) {
            l.setPrecioSugerido(null);
            return;
        }
        BigDecimal sugerido = helper.precioSugerido(l.getProducto(), l.getCosto());
        l.setPrecioSugerido(sugerido);
        if (sugerido != null) {
            l.setPrecioVenta(sugerido);
        }
    }

    public int getProductosSeleccionados() {
        int n = 0;
        for (LineaRecepcion l : lineas) {
            if (l.isSeleccionada()) {
                n++;
            }
        }
        return n;
    }

    public int getPiezasSeleccionadas() {
        int n = 0;
        for (LineaRecepcion l : lineas) {
            if (l.isSeleccionada()) {
                n += l.getCantidad();
            }
        }
        return n;
    }

    public void guardar() {
        if (isModoProveedor()) {
            guardarRecepcion();
        } else {
            guardarUno();
        }
    }

    private void guardarRecepcion() {
        try {
            Integer idUsuario = loginUI == null || loginUI.getUsuario() == null ? null : loginUI.getUsuario().getId();
            List<LineaEntrada> aRecibir = new ArrayList<>();
            for (LineaRecepcion l : lineas) {
                if (l.isSeleccionada()) {
                    aRecibir.add(new LineaEntrada(l.getProducto().getId(), l.getCantidad(), l.getCosto(),
                            l.getPrecioVenta()));
                }
            }
            List<ResultadoEntrada> resultados = helper.registrarRecepcion(proveedorId, aRecibir, idUsuario,
                    observaciones);

            int piezas = 0;
            List<String> reactivados = new ArrayList<>();
            List<String> sobreMaximo = new ArrayList<>();
            for (int i = 0; i < resultados.size(); i++) {
                ResultadoEntrada r = resultados.get(i);
                piezas += aRecibir.get(i).getCantidad();
                if (r.isReactivado()) {
                    reactivados.add(r.getProducto().getNombre());
                }
                if (r.isSuperaMaximo()) {
                    sobreMaximo.add(r.getProducto().getNombre());
                }
            }
            for (int i = 0; i < resultados.size(); i++) {
                ResultadoEntrada r = resultados.get(i);
                bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.ENTRADA, "entrada_inventario", null, null,
                        BitacoraHelper.texto(BitacoraHelper.campos("producto", r.getProducto().getNombre(),
                                "piezas", aRecibir.get(i).getCantidad(), "costo", aRecibir.get(i).getCosto(),
                                "existencia", r.getExistenciaTotal())), observaciones);
            }
            mensaje(FacesMessage.SEVERITY_INFO, "Recepción registrada",
                    resultados.size() + " producto(s), " + piezas + " pieza(s) recibidas.");
            if (!reactivados.isEmpty()) {
                mensaje(FacesMessage.SEVERITY_INFO, "Productos reactivados", String.join(", ", reactivados));
            }
            if (!sobreMaximo.isEmpty()) {
                mensaje(FacesMessage.SEVERITY_WARN, "Superan el stock máximo recomendado",
                        String.join(", ", sobreMaximo) + ". La entrada se registró de todos modos.");
            }
            // Se vuelve a listar al proveedor con las existencias y costos ya actualizados.
            observaciones = null;
            cargarProductosProveedor();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo registrar la recepción", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al registrar la recepción", ex.getMessage());
        }
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").trim();
    }

    private void guardarUno() {
        try {
            Integer idUsuario = loginUI == null || loginUI.getUsuario() == null ? null : loginUI.getUsuario().getId();
            Integer idProducto = productoSeleccionado == null ? null : productoSeleccionado.getId();
            ResultadoEntrada r = helper.registrar(proveedorId, idProducto, cantidad, costo, precioVenta,
                    idUsuario, observaciones);

            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.ENTRADA, "entrada_inventario", null, null,
                    BitacoraHelper.texto(BitacoraHelper.campos("producto", r.getProducto().getNombre(),
                            "piezas", cantidad, "costo", costo, "existencia", r.getExistenciaTotal())),
                    observaciones);
            mensaje(FacesMessage.SEVERITY_INFO, "Entrada registrada",
                    r.getProducto().getNombre() + ": existencia " + r.getExistenciaTotal() + " pzas.");
            if (r.isReactivado()) {
                mensaje(FacesMessage.SEVERITY_INFO, "Producto reactivado",
                        r.getProducto().getNombre() + " estaba deshabilitado y ya está activo de nuevo.");
            }
            if (r.isSuperaMaximo()) {
                mensaje(FacesMessage.SEVERITY_WARN, "Supera el stock máximo recomendado",
                        "Existencia " + r.getExistenciaTotal() + " de " + r.getProducto().getStockMaximo()
                                + " como máximo. La entrada se registró de todos modos.");
            }
            limpiar();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo registrar la entrada", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al registrar la entrada", ex.getMessage());
        }
    }

    /** Deja el formulario vacío para una nueva entrada. */
    public void limpiar() {
        proveedorId = null;
        lineas = new ArrayList<>();
        filtroLineas = null;
        productoSeleccionado = null;
        cantidad = null;
        observaciones = null;
        limpiarDatosProducto();
    }

    private void limpiarDatosProducto() {
        costo = null;
        precioVenta = null;
        precioSugerido = null;
        existenciaActual = 0;
    }

    /** True si hay un producto elegido y está deshabilitado: la entrada lo reactivará. */
    public boolean isProductoDeshabilitado() {
        return productoSeleccionado != null && !Boolean.TRUE.equals(productoSeleccionado.getActivo());
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    /* ======================== getters y setters ======================== */

    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public List<LineaRecepcion> getLineas() {
        return lineas;
    }

    public String getFiltroLineas() {
        return filtroLineas;
    }

    public void setFiltroLineas(String filtroLineas) {
        this.filtroLineas = filtroLineas;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
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

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
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

    public BigDecimal getPrecioSugerido() {
        return precioSugerido;
    }
}
