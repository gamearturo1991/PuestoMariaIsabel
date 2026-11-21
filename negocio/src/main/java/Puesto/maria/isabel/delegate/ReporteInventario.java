package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/** Informe mensual de estado y rotación del inventario (PBI-REP-US4). */
public class ReporteInventario implements Serializable {

    /** Movimiento del mes de un producto. */
    public static class Fila implements Serializable {
        private final Integer idProducto;
        private final String codigo;
        private final String nombre;
        private final String categoria;
        private final String proveedor;
        private final boolean activo;
        private final long inicial;
        private final long entradas;
        private final long vendidas;
        private final long mermas;
        private final long finalMes;
        private final long actual;
        private final EstadoStock estado;
        private final BigDecimal rotacion;

        public Fila(Integer idProducto, String codigo, String nombre, String categoria, String proveedor,
                    boolean activo, long inicial, long entradas, long vendidas, long mermas, long finalMes,
                    long actual, EstadoStock estado, BigDecimal rotacion) {
            this.idProducto = idProducto;
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.proveedor = proveedor;
            this.activo = activo;
            this.inicial = inicial;
            this.entradas = entradas;
            this.vendidas = vendidas;
            this.mermas = mermas;
            this.finalMes = finalMes;
            this.actual = actual;
            this.estado = estado;
            this.rotacion = rotacion;
        }

        public Integer getIdProducto() { return idProducto; }
        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public String getCategoria() { return categoria; }
        public String getProveedor() { return proveedor; }
        public boolean isActivo() { return activo; }
        public long getInicial() { return inicial; }
        public long getEntradas() { return entradas; }
        public long getVendidas() { return vendidas; }
        /** Piezas mermadas en el mes, netas de anulaciones de merma. */
        public long getMermas() { return mermas; }
        public long getFinalMes() { return finalMes; }
        public long getActual() { return actual; }
        public EstadoStock getEstado() { return estado; }
        /** Piezas vendidas ÷ existencia promedio, o null cuando no se puede calcular. */
        public BigDecimal getRotacion() { return rotacion; }

        /** True si el producto está agotado o con stock bajo (conviene reabastecerlo). */
        public boolean isAlerta() {
            return estado == EstadoStock.AGOTADO || estado == EstadoStock.BAJO;
        }

        /** Rotación lista para mostrar: el número, "—" si no se puede calcular o "Sin movimiento". */
        public String getRotacionTexto() {
            if (vendidas <= 0) {
                return "Sin movimiento";
            }
            return rotacion == null ? "—" : rotacion.toPlainString();
        }
    }

    private final PeriodoReporte periodo;
    private final List<Fila> conVentas;
    private final List<Fila> sinMovimiento;

    public ReporteInventario(PeriodoReporte periodo, List<Fila> conVentas, List<Fila> sinMovimiento) {
        this.periodo = periodo;
        this.conVentas = conVentas;
        this.sinMovimiento = sinMovimiento;
    }

    public PeriodoReporte getPeriodo() { return periodo; }
    /** Productos con ventas en el mes, de mayor a menor rotación. */
    public List<Fila> getConVentas() { return conVentas; }
    /** Productos sin ventas en el mes (con o sin existencia). */
    public List<Fila> getSinMovimiento() { return sinMovimiento; }
    public boolean isVacio() { return conVentas.isEmpty() && sinMovimiento.isEmpty(); }
}
