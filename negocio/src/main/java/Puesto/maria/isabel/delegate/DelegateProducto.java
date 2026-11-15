package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Categoria;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Proveedor;
import puesto.maria.persistence.dao.ProductoDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reglas de negocio de productos (PBI-PRO).
 * Los errores de validación se lanzan como IllegalArgumentException con un mensaje listo para mostrar.
 */
public class DelegateProducto {

    private ProductoDAO dao() {
        return ServiceLocator.getInstanceProductoDAO();
    }

    /** US1: registrar un producto nuevo (queda activo). */
    public void saveProducto(Producto producto) {
        normalizar(producto);
        validar(producto);
        producto.setActivo(true);
        dao().save(producto);
    }

    /** US4: modificar un producto existente. */
    public void updateProducto(Producto producto) {
        normalizar(producto);
        validar(producto);
        dao().update(producto);
    }

    /** US4: baja lógica. El producto deja de aparecer en búsquedas, pero conserva su historial. */
    public void deshabilitarProducto(Producto producto) {
        producto.setActivo(false);
        dao().update(producto);
    }

    /** Todos los productos (pantalla de administración de productos). */
    /** Existencia de cada producto (id -> piezas); los productos sin lotes no aparecen y valen 0. */
    public Map<Integer, Long> existencias() {
        return dao().existenciasPorProducto();
    }

    public List<Producto> listarProductos() {
        return dao().listarTodos();
    }

    /**
     * Búsqueda por código exacto o nombre (sin distinguir mayúsculas ni acentos).
     * El código exacto va primero; después los que contienen el texto en nombre o código.
     * @param soloActivos true en ventas y productos; false en entradas (permite reactivar).
     */
    public List<Producto> buscarProductos(String texto, boolean soloActivos, int max) {
        List<Producto> resultado = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return resultado;
        }
        String t = normalizar(texto);
        List<Producto> coincidencias = new ArrayList<>();
        for (Producto p : dao().listarTodos()) {
            if (soloActivos && !Boolean.TRUE.equals(p.getActivo())) {
                continue;
            }
            String codigo = normalizar(p.getCodigoCorto());
            if (codigo.equals(t)) {
                resultado.add(p);
            } else if (codigo.contains(t) || normalizar(p.getNombre()).contains(t)) {
                coincidencias.add(p);
            }
        }
        resultado.addAll(coincidencias);
        return resultado.size() > max ? new ArrayList<>(resultado.subList(0, max)) : resultado;
    }

    /** Productos (activos y deshabilitados) que surte un proveedor, ordenados por nombre. */
    public List<Producto> productosDeProveedor(Integer idProveedor) {
        List<Producto> lista = new ArrayList<>();
        if (idProveedor == null) {
            return lista;
        }
        for (Producto p : dao().listarTodos()) {
            if (p.getIdProveedor() != null && idProveedor.equals(p.getIdProveedor().getId())) {
                lista.add(p);
            }
        }
        return lista;
    }

    public Producto obtenerProducto(Integer id) {
        return id == null ? null : dao().find(id).orElse(null);
    }

    public List<Categoria> listarCategorias() {
        return ServiceLocator.getInstanceCategoriaDAO().listarOrdenadas();
    }

    public List<Proveedor> listarProveedores() {
        return ServiceLocator.getInstanceProveedorDAO().listarActivos();
    }

    public Categoria obtenerCategoria(Integer id) {
        return id == null ? null : ServiceLocator.getInstanceCategoriaDAO().find(id).orElse(null);
    }

    public Proveedor obtenerProveedor(Integer id) {
        return id == null ? null : ServiceLocator.getInstanceProveedorDAO().find(id).orElse(null);
    }

    /**
     * Precio sugerido al subir/bajar el costo: mantiene la proporción actual.
     * precio sugerido = costo nuevo x (precio actual / costo actual).
     */
    public BigDecimal calcularPrecioSugerido(Producto producto, BigDecimal costoNuevo) {
        if (producto == null || costoNuevo == null || producto.getCosto() == null
                || producto.getPrecioVenta() == null || producto.getCosto().signum() <= 0) {
            return producto == null ? null : producto.getPrecioVenta();
        }
        return costoNuevo
                .multiply(producto.getPrecioVenta())
                .divide(producto.getCosto(), 2, RoundingMode.HALF_UP);
    }

    /* ======================== helpers internos ======================== */

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim();
    }

    private void normalizar(Producto p) {
        if (p.getNombre() != null) {
            p.setNombre(p.getNombre().trim());
        }
        if (p.getCodigoCorto() != null) {
            p.setCodigoCorto(p.getCodigoCorto().trim().toLowerCase());
        }
    }

    private void validar(Producto p) {
        if (p.getCodigoCorto() == null || p.getCodigoCorto().isEmpty()) {
            throw new IllegalArgumentException("El código corto es obligatorio.");
        }
        if (p.getCodigoCorto().length() > 50) {
            throw new IllegalArgumentException("El código corto no puede pasar de 50 caracteres.");
        }
        if (p.getCodigoCorto().matches(".*\\s.*")) {
            throw new IllegalArgumentException("El código corto no admite espacios.");
        }
        if (dao().existeCodigo(p.getCodigoCorto(), p.getId())) {
            throw new IllegalArgumentException("Ya existe un producto con el código \"" + p.getCodigoCorto() + "\".");
        }
        if (p.getNombre() == null || p.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (p.getNombre().length() > 255) {
            throw new IllegalArgumentException("El nombre no puede pasar de 255 caracteres.");
        }
        if (p.getIdCategoria() == null) {
            throw new IllegalArgumentException("Selecciona una categoría.");
        }
        if (p.getIdProveedor() == null) {
            throw new IllegalArgumentException("Selecciona un proveedor.");
        }
        if (p.getCosto() == null || p.getCosto().signum() <= 0) {
            throw new IllegalArgumentException("El costo debe ser mayor a 0.");
        }
        if (p.getPrecioVenta() == null || p.getPrecioVenta().signum() <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0.");
        }
        if (p.getStockMinimo() == null || p.getStockMinimo() < 0) {
            throw new IllegalArgumentException("El stock mínimo debe ser 0 o mayor.");
        }
        if (p.getStockMaximo() == null || p.getStockMaximo() < 2) {
            throw new IllegalArgumentException("El stock máximo debe ser mayor a 1.");
        }
        if (p.getStockMaximo() < p.getStockMinimo()) {
            throw new IllegalArgumentException("El stock máximo no puede ser menor que el mínimo.");
        }
    }
}
