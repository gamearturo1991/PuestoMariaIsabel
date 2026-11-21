package puesto.maria.persistence.dao;

import jakarta.persistence.EntityManager;
import puesto.maria.entity.Producto;
import puesto.maria.entity.Venta;
import puesto.maria.persistence.persistence.AbstractDAO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consultas de los reportes (PBI-REP). Solo lee. Usa su propio EntityManager, así que no se mezcla con las
 * transacciones de ventas, inventario o mermas. Las filas se devuelven como arreglos de columnas; la lógica de
 * los reportes está en el negocio.
 */
public class ReporteDAO extends AbstractDAO<Venta> {

    /** Estado con el que se guardan las ventas que sí cuentan. */
    public static final String COMPLETADA = "COMPLETADA";

    private final EntityManager entityManager;

    public ReporteDAO(EntityManager em) {
        super(Venta.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /** Ventas completadas del periodo [desde, hasta). Columnas: fechaHora, total. */
    public List<Object[]> ventasCompletadas(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT v.fechaHora, v.total FROM Venta v "
                                + "WHERE v.estado = :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta "
                                + "ORDER BY v.fechaHora", Object[].class)
                .setParameter("estado", COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /** Ventas del periodo que no están completadas. Columnas: cantidad, suma de totales (puede ser null). */
    public Object[] ventasNoCompletadas(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT COUNT(v), SUM(v.total) FROM Venta v "
                                + "WHERE v.estado <> :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta",
                        Object[].class)
                .setParameter("estado", COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult());
    }

    /** Pagos de ventas completadas por método. Columnas: metodo, suma de montos, suma de comisiones. */
    public List<Object[]> pagos(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT p.metodo, SUM(p.monto), SUM(p.comision) FROM PagoVenta p JOIN p.idVenta v "
                                + "WHERE v.estado = :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta "
                                + "GROUP BY p.metodo", Object[].class)
                .setParameter("estado", COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /**
     * Productos de cada venta completada. Columnas: número de venta, fecha, total de la venta, código, producto,
     * cantidad, precio unitario, subtotal.
     */
    public List<Object[]> lineasDeVentas(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT v.id, v.fechaHora, v.total, p.codigoCorto, p.nombre, d.cantidad, d.precioUnitario, "
                                + "d.subtotal FROM DetalleVenta d JOIN d.idVenta v JOIN d.idProducto p "
                                + "WHERE v.estado = :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta "
                                + "ORDER BY v.id DESC, d.id", Object[].class)
                .setParameter("estado", COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /** Lo pagado en cada venta completada por método. Columnas: número de venta, método, monto. */
    public List<Object[]> metodosPorVenta(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT v.id, p.metodo, SUM(p.monto) FROM PagoVenta p JOIN p.idVenta v "
                                + "WHERE v.estado = :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta "
                                + "GROUP BY v.id, p.metodo ORDER BY v.id, p.metodo",
                        Object[].class)
                .setParameter("estado", COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /**
     * Lo vendido por producto en ventas completadas. Columnas: id, código, nombre, id de categoría,
     * categoría (los dos últimos pueden ser null), piezas, suma de subtotales.
     */
    public List<Object[]> vendidosPorProducto(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT p.id, p.codigoCorto, p.nombre, c.id, c.nombre, SUM(d.cantidad), SUM(d.subtotal) "
                                + "FROM DetalleVenta d JOIN d.idVenta v JOIN d.idProducto p "
                                + "LEFT JOIN p.idCategoria c "
                                + "WHERE v.estado = :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta "
                                + "GROUP BY p.id, p.codigoCorto, p.nombre, c.id, c.nombre", Object[].class)
                .setParameter("estado", COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /**
     * Costo de adquisición de las piezas vendidas por producto: lo que salió de cada entrada por el costo con
     * que esa entrada llegó. Columnas: id de producto, costo.
     */
    public List<Object[]> costoVendidoPorProducto(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT p.id, SUM(dvl.cantidad * l.costoUnitario) "
                                + "FROM DetalleVentaLote dvl JOIN dvl.idDetalle d JOIN d.idVenta v "
                                + "JOIN dvl.idLote l JOIN l.idProducto p "
                                + "WHERE v.estado = :estado AND v.fechaHora >= :desde AND v.fechaHora < :hasta "
                                + "GROUP BY p.id", Object[].class)
                .setParameter("estado", COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /** Pérdida por mermas del periodo, neta de anulaciones (que van con piezas negativas). Puede ser null. */
    public BigDecimal mermasNetas(Instant desde, Instant hasta) {
        return execute(em -> em.createQuery(
                        "SELECT SUM(m.cantidad * m.costoUnitario) FROM Merma m "
                                + "WHERE m.fechaHora >= :desde AND m.fechaHora < :hasta", BigDecimal.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult());
    }

    /** Todos los productos con su categoría y proveedor, recién leídos de la base. */
    public List<Producto> productos() {
        return execute(em -> {
            em.clear();
            return em.createQuery(
                            "SELECT p FROM Producto p LEFT JOIN FETCH p.idCategoria LEFT JOIN FETCH p.idProveedor "
                                    + "ORDER BY p.nombre", Producto.class)
                    .getResultList();
        });
    }

    /** Piezas recibidas por producto en entradas anteriores al instante dado. */
    public Map<Integer, Long> recibidasAntesDe(Instant corte) {
        return mapa("SELECT p.id, SUM(l.cantidadRecibida) FROM Lote l JOIN l.idEntrada e JOIN l.idProducto p "
                + "WHERE e.fechaHora < :corte GROUP BY p.id", corte, false);
    }

    /** Piezas vendidas por producto (ventas completadas) antes del instante dado. */
    public Map<Integer, Long> vendidasAntesDe(Instant corte) {
        return mapa("SELECT p.id, SUM(d.cantidad) FROM DetalleVenta d JOIN d.idVenta v JOIN d.idProducto p "
                + "WHERE v.estado = :estado AND v.fechaHora < :corte GROUP BY p.id", corte, true);
    }

    /** Piezas mermadas por producto, netas de anulaciones, antes del instante dado. */
    public Map<Integer, Long> mermadasAntesDe(Instant corte) {
        return mapa("SELECT p.id, SUM(m.cantidad) FROM Merma m JOIN m.idLote l JOIN l.idProducto p "
                + "WHERE m.fechaHora < :corte GROUP BY p.id", corte, false);
    }

    /** Existencia actual por producto. */
    public Map<Integer, Long> existenciaActual() {
        return execute(em -> {
            Map<Integer, Long> mapa = new HashMap<>();
            for (Object[] f : em.createQuery(
                    "SELECT l.idProducto.id, SUM(l.cantidadDisponible) FROM Lote l GROUP BY l.idProducto.id",
                    Object[].class).getResultList()) {
                mapa.put((Integer) f[0], ((Number) f[1]).longValue());
            }
            return mapa;
        });
    }

    private Map<Integer, Long> mapa(String jpql, Instant corte, boolean conEstado) {
        return execute(em -> {
            var consulta = em.createQuery(jpql, Object[].class).setParameter("corte", corte);
            if (conEstado) {
                consulta.setParameter("estado", COMPLETADA);
            }
            Map<Integer, Long> mapa = new HashMap<>();
            for (Object[] f : consulta.getResultList()) {
                mapa.put((Integer) f[0], f[1] == null ? 0L : ((Number) f[1]).longValue());
            }
            return mapa;
        });
    }
}
