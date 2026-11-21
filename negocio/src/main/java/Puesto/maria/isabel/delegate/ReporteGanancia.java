package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/** Resultado del reporte de ingresos y ganancia neta (PBI-REP-US3). */
public class ReporteGanancia implements Serializable {

    /** Ingreso, costo y ganancia bruta de una categoría o de un producto. */
    public static class Linea implements Serializable {
        private final String nombre;
        private final long piezas;
        private final BigDecimal ingreso;
        private final BigDecimal costo;
        private final BigDecimal ganancia;

        public Linea(String nombre, long piezas, BigDecimal ingreso, BigDecimal costo) {
            this.nombre = nombre;
            this.piezas = piezas;
            this.ingreso = ingreso;
            this.costo = costo;
            this.ganancia = ingreso.subtract(costo);
        }

        public String getNombre() { return nombre; }
        public long getPiezas() { return piezas; }
        public BigDecimal getIngreso() { return ingreso; }
        public BigDecimal getCosto() { return costo; }
        /** Ganancia bruta de la línea: ingreso − costo. */
        public BigDecimal getGanancia() { return ganancia; }
    }

    /** Renglón del resumen de conciliación (concepto y valor). */
    public static class Concepto implements Serializable {
        private final String concepto;
        private final BigDecimal valor;

        public Concepto(String concepto, BigDecimal valor) {
            this.concepto = concepto;
            this.valor = valor;
        }

        public String getConcepto() { return concepto; }
        public BigDecimal getValor() { return valor; }
    }

    private final PeriodoReporte periodo;
    private final BigDecimal ingresos;
    private final BigDecimal costoVendido;
    private final BigDecimal gananciaBruta;
    private final BigDecimal mermas;
    private final BigDecimal comisiones;
    private final BigDecimal gananciaNeta;
    private final BigDecimal margen;
    private final List<Concepto> resumen;
    private final List<Linea> categorias;
    private final List<Linea> productos;
    private final int ventasNoCompletadas;
    private final BigDecimal montoNoCompletadas;
    private final boolean sinMovimientos;

    public ReporteGanancia(PeriodoReporte periodo, BigDecimal ingresos, BigDecimal costoVendido,
                           BigDecimal mermas, BigDecimal comisiones, BigDecimal margen,
                           List<Concepto> resumen, List<Linea> categorias, List<Linea> productos,
                           int ventasNoCompletadas, BigDecimal montoNoCompletadas, boolean sinMovimientos) {
        this.periodo = periodo;
        this.ingresos = ingresos;
        this.costoVendido = costoVendido;
        this.gananciaBruta = ingresos.subtract(costoVendido);
        this.mermas = mermas;
        this.comisiones = comisiones;
        this.gananciaNeta = gananciaBruta.subtract(mermas).subtract(comisiones);
        this.margen = margen;
        this.resumen = resumen;
        this.categorias = categorias;
        this.productos = productos;
        this.ventasNoCompletadas = ventasNoCompletadas;
        this.montoNoCompletadas = montoNoCompletadas;
        this.sinMovimientos = sinMovimientos;
    }

    public PeriodoReporte getPeriodo() { return periodo; }
    public BigDecimal getIngresos() { return ingresos; }
    /** Costo de adquisición de las piezas vendidas (las más antiguas salen primero y conservan su costo). */
    public BigDecimal getCostoVendido() { return costoVendido; }
    public BigDecimal getGananciaBruta() { return gananciaBruta; }
    /** Pérdida por mermas, neta de anulaciones de merma. */
    public BigDecimal getMermas() { return mermas; }
    public BigDecimal getComisiones() { return comisiones; }
    public BigDecimal getGananciaNeta() { return gananciaNeta; }
    /** Ganancia neta ÷ ingresos en porcentaje, o null si no hay ingresos. */
    public BigDecimal getMargen() { return margen; }
    public List<Concepto> getResumen() { return resumen; }
    public List<Linea> getCategorias() { return categorias; }
    public List<Linea> getProductos() { return productos; }
    public int getVentasNoCompletadas() { return ventasNoCompletadas; }
    public BigDecimal getMontoNoCompletadas() { return montoNoCompletadas; }
    /** True si en el periodo no hubo ventas, mermas ni comisiones. */
    public boolean isSinMovimientos() { return sinMovimientos; }
}
