package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Producto;
import puesto.maria.persistence.dao.ProductoDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Consulta de existencias en tiempo real (PBI-PRO-US5) y conteo de alertas (base de US2). */
public class DelegateInventario {

    private ProductoDAO dao() {
        return ServiceLocator.getInstanceProductoDAO();
    }

    /**
     * Existencia y estado de los productos activos. La existencia se calcula en cada consulta con una suma de
     * los lotes en la base de datos, por lo que siempre está al día.
     */
    public List<ExistenciaProducto> consultarExistencias() {
        Map<Integer, Long> existencias = dao().existenciasPorProducto();
        List<ExistenciaProducto> lista = new ArrayList<>();
        for (Producto p : dao().listarTodos()) {
            if (!Boolean.TRUE.equals(p.getActivo())) {
                continue;
            }
            Long n = existencias.get(p.getId());
            lista.add(new ExistenciaProducto(p, n == null ? 0 : n));
        }
        return lista;
    }

    /** Productos activos agotados o con stock bajo; primero los agotados y luego los de menor existencia. */
    public List<ExistenciaProducto> listarAlertas() {
        List<ExistenciaProducto> alertas = new ArrayList<>();
        for (ExistenciaProducto e : consultarExistencias()) {
            if (e.isAlerta()) {
                alertas.add(e);
            }
        }
        alertas.sort(java.util.Comparator
                .comparingLong(ExistenciaProducto::getExistencia)
                .thenComparing(e -> e.getProducto().getNombre(), String.CASE_INSENSITIVE_ORDER));
        return alertas;
    }

    /** Cantidad de productos activos agotados o con stock bajo. */
    public int contarAlertas() {
        return listarAlertas().size();
    }
}
