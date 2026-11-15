package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Producto;
import puesto.maria.persistence.dao.EntradaInventarioDAO;
import puesto.maria.persistence.dao.LineaEntrada;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reglas de negocio de las entradas de mercancía (PBI-PRO-US3): entrada de un solo producto
 * o recepción de un proveedor con varios productos. Los errores de validación se lanzan como
 * IllegalArgumentException con un mensaje listo para mostrar.
 */
public class DelegateEntradaInventario {

    private EntradaInventarioDAO dao() {
        return ServiceLocator.getInstanceEntradaInventarioDAO();
    }

    /** Entrada de un solo producto. */
    public ResultadoEntrada registrarEntrada(Integer idProveedor, Integer idProducto, Integer cantidad,
                                             BigDecimal costo, BigDecimal precioVenta,
                                             Integer idUsuario, String observaciones) {
        if (idProducto == null) {
            throw new IllegalArgumentException("Selecciona un producto.");
        }
        List<LineaEntrada> lineas = new ArrayList<>();
        lineas.add(new LineaEntrada(idProducto, cantidad, costo, precioVenta));
        return registrarRecepcion(idProveedor, lineas, idUsuario, observaciones).get(0);
    }

    /**
     * Recepción de un proveedor: una sola entrada con un lote por producto, todo en una transacción.
     * Los productos deshabilitados se reactivan. Pasar del stock máximo no bloquea: solo se informa.
     */
    public List<ResultadoEntrada> registrarRecepcion(Integer idProveedor, List<LineaEntrada> lineas,
                                                     Integer idUsuario, String observaciones) {
        if (idProveedor == null) {
            throw new IllegalArgumentException("Selecciona un proveedor.");
        }
        if (idUsuario == null) {
            throw new IllegalArgumentException("No hay un usuario con sesión iniciada.");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException("Indica la cantidad de al menos un producto.");
        }
        Set<Integer> vistos = new HashSet<>();
        List<Boolean> estabaDeshabilitado = new ArrayList<>();
        for (LineaEntrada l : lineas) {
            Producto actual = l.getIdProducto() == null ? null
                    : ServiceLocator.getInstanceProductoDAO().find(l.getIdProducto()).orElse(null);
            if (actual == null) {
                throw new IllegalArgumentException("Un producto de la entrada ya no existe.");
            }
            String nombre = actual.getNombre();
            if (!vistos.add(l.getIdProducto())) {
                throw new IllegalArgumentException("El producto " + nombre + " está repetido.");
            }
            if (l.getCantidad() == null || l.getCantidad() < 1) {
                throw new IllegalArgumentException(nombre + ": la cantidad debe ser mayor a 0.");
            }
            if (l.getCosto() == null || l.getCosto().signum() <= 0) {
                throw new IllegalArgumentException(nombre + ": el costo debe ser mayor a 0.");
            }
            if (l.getPrecioVenta() == null || l.getPrecioVenta().signum() <= 0) {
                throw new IllegalArgumentException(nombre + ": el precio de venta debe ser mayor a 0.");
            }
            estabaDeshabilitado.add(!Boolean.TRUE.equals(actual.getActivo()));
        }

        String notas = (observaciones == null || observaciones.isBlank()) ? null : observaciones.trim();
        List<Producto> actualizados = dao().registrarEntrada(idProveedor, idUsuario, lineas, notas);

        List<ResultadoEntrada> resultados = new ArrayList<>();
        for (int i = 0; i < actualizados.size(); i++) {
            Producto p = actualizados.get(i);
            resultados.add(new ResultadoEntrada(p, dao().existenciaTotal(p.getId()), estabaDeshabilitado.get(i)));
        }
        return resultados;
    }

    /** Existencia actual de un producto (suma de sus lotes). */
    public int consultarExistencia(Integer idProducto) {
        return dao().existenciaTotal(idProducto);
    }
}
