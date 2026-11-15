package ui;

import Puesto.maria.isabel.delegate.EstadoStock;
import Puesto.maria.isabel.delegate.ExistenciaProducto;
import helper.InventarioHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla "Inventario y alertas": existencias en tiempo real (PBI-PRO-US5).
 * Muestra la existencia de cada producto activo con su estado (agotado, stock bajo, sobre el máximo o normal).
 */
@Named("inventarioUI")
@SessionScoped
public class InventarioBeanUI implements Serializable {

    private final InventarioHelper helper = new InventarioHelper();

    /** Todas las existencias tal como están en la base de datos en el momento de la última consulta. */
    private List<ExistenciaProducto> todas = new ArrayList<>();
    private List<ExistenciaProducto> filas = new ArrayList<>();

    private String textoBusqueda;
    /** Estado elegido en el filtro; vacío = todos. */
    private String estadoFiltro = "";

    private int agotados;
    private int bajos;
    private int sobreMaximo;
    private int normales;

    @PostConstruct
    public void init() {
        actualizar();
    }

    /** Vuelve a consultar las existencias a la base de datos y reaplica los filtros. */
    public void actualizar() {
        todas = helper.consultarExistencias();
        agotados = 0;
        bajos = 0;
        sobreMaximo = 0;
        normales = 0;
        for (ExistenciaProducto e : todas) {
            switch (e.getEstado()) {
                case AGOTADO -> agotados++;
                case BAJO -> bajos++;
                case SOBRE_MAXIMO -> sobreMaximo++;
                default -> normales++;
            }
        }
        filtrar();
    }

    /** Aplica el texto de búsqueda (código o nombre) y el estado elegido sobre la última consulta. */
    public void filtrar() {
        String t = normalizar(textoBusqueda);
        List<ExistenciaProducto> resultado = new ArrayList<>();
        for (ExistenciaProducto e : todas) {
            if (!estadoFiltro.isEmpty() && !e.getEstado().name().equals(estadoFiltro)) {
                continue;
            }
            if (t.isEmpty() || normalizar(e.getProducto().getNombre()).contains(t)
                    || normalizar(e.getProducto().getCodigoCorto()).contains(t)) {
                resultado.add(e);
            }
        }
        filas = resultado;
    }

    /** Pulsar una tarjeta de resumen filtra por ese estado; pulsarla de nuevo quita el filtro. */
    public void filtrarPorEstado(String estado) {
        estadoFiltro = estadoFiltro.equals(estado) ? "" : estado;
        filtrar();
    }

    public void limpiar() {
        textoBusqueda = null;
        estadoFiltro = "";
        filtrar();
    }

    public EstadoStock[] getEstados() {
        return EstadoStock.values();
    }

    /** Clase CSS de la etiqueta de estado. */
    public String claseEstado(EstadoStock estado) {
        return switch (estado) {
            case AGOTADO -> "estado-agotado";
            case BAJO -> "estado-bajo";
            case SOBRE_MAXIMO -> "estado-sobre";
            default -> "estado-normal";
        };
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "").trim();
    }

    /* ======================== getters y setters ======================== */

    public List<ExistenciaProducto> getFilas() {
        return filas;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }

    public String getEstadoFiltro() {
        return estadoFiltro;
    }

    public void setEstadoFiltro(String estadoFiltro) {
        this.estadoFiltro = estadoFiltro == null ? "" : estadoFiltro;
    }

    public int getAgotados() {
        return agotados;
    }

    public int getBajos() {
        return bajos;
    }

    public int getSobreMaximo() {
        return sobreMaximo;
    }

    public int getNormales() {
        return normales;
    }

    public int getTotal() {
        return todas.size();
    }
}
