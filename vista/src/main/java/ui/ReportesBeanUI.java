package ui;

import Puesto.maria.isabel.delegate.ExportadorCsv;
import Puesto.maria.isabel.delegate.PeriodoReporte;
import Puesto.maria.isabel.delegate.ReporteGanancia;
import Puesto.maria.isabel.delegate.ReporteInventario;
import Puesto.maria.isabel.delegate.ReporteVentas;
import helper.CategoriaHelper;
import helper.ProveedorHelper;
import helper.ReporteHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import puesto.maria.entity.Categoria;
import puesto.maria.entity.Proveedor;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.CorteCaja;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla de reportes (PBI-REP-US2, US3 y US4): ventas por periodo, ingresos y ganancia neta, y estado y
 * rotación del inventario. Es de solo lectura; cada reporte se puede imprimir (o guardar como PDF); los tickets también se exportan a CSV.
 */
@Named("reportesUI")
@SessionScoped
public class ReportesBeanUI implements Serializable {

    public static final String VENTAS = "VENTAS";
    public static final String GANANCIA = "GANANCIA";
    public static final String INVENTARIO = "INVENTARIO";
    public static final String TICKETS = "TICKETS";
    public static final String CORTE = "CORTE";

    private static final Locale ES_MX = Locale.forLanguageTag("es-MX");

    private final ReporteHelper helper = new ReporteHelper();
    private final CategoriaHelper categoriaHelper = new CategoriaHelper();
    private final ProveedorHelper proveedorHelper = new ProveedorHelper();

    private String vista = VENTAS;

    /** Periodo de los reportes de ventas y de ganancia. */
    private PeriodoReporte.Tipo tipo = PeriodoReporte.Tipo.SEMANAL;
    private LocalDate fecha = LocalDate.now();

    /** Mes del informe de inventario y sus filtros. */
    private Integer anio = LocalDate.now().getYear();
    private Integer mes = LocalDate.now().getMonthValue();
    private Integer categoriaId;
    private Integer proveedorId;

    /** Texto que se escribe en el buscador de tickets (folio o número de venta). */
    private String busquedaTicket = "";

    private ReporteVentas ventas;
    private ReporteGanancia ganancia;
    private ReporteInventario inventario;
    private CorteCaja cortePrevisualizacion;
    private LocalDate fechaCorte;
    private CorteCaja corteConsultado;

    private List<Categoria> categorias = new ArrayList<>();
    private List<Proveedor> proveedores = new ArrayList<>();

    /** Se ejecuta cada vez que se abre la pantalla (no en las peticiones ajax). */
    public void alAbrir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null || fc.isPostback() || fc.getResponseComplete()) {
            return;
        }
        vista = VENTAS;
        tipo = PeriodoReporte.Tipo.SEMANAL;
        fecha = LocalDate.now();
        anio = fecha.getYear();
        mes = fecha.getMonthValue();
        categoriaId = null;
        proveedorId = null;
        busquedaTicket = "";
        ventas = null;
        ganancia = null;
        inventario = null;
        try {
            categorias = categoriaHelper.listar();
            proveedores = proveedorHelper.listar();
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los filtros", ex.getMessage());
        }
        consultar();
    }

    /** Genera el reporte de la pestaña activa con los parámetros elegidos. */
    public void consultar() {
        try {
            if (CORTE.equals(vista)) {
                corteConsultado = helper.obtenerCorte(fecha);
            }
            else if (GANANCIA.equals(vista)) {
                ganancia = null;
                ganancia = helper.ganancia(helper.periodo(tipo, fecha));
            } else if (INVENTARIO.equals(vista)) {
                inventario = null;
                inventario = helper.inventario(helper.mes(anio, mes), categoriaId, proveedorId);
            } else {
                ventas = null;
                ventas = helper.ventas(helper.periodo(tipo, fecha));
            }
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo generar el reporte", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al generar el reporte", ex.getMessage());
        }
    }

    public void cambiarVista() {
        consultar();
    }

    /** Vuelve al periodo en curso. */
    public void hoy() {
        fecha = LocalDate.now();
        anio = fecha.getYear();
        mes = fecha.getMonthValue();
        consultar();
    }

    /** Mueve el periodo hacia atrás (-1) o hacia adelante (1). */
    public void desplazar(int sentido) {
        try {
            if (INVENTARIO.equals(vista)) {
                YearMonth ym = YearMonth.of(anio, mes).plusMonths(sentido);
                anio = ym.getYear();
                mes = ym.getMonthValue();
            } else if (CORTE.equals(vista)) {
                fecha = fecha.plusDays(sentido);
            } else {
                PeriodoReporte actual = PeriodoReporte.de(tipo, fecha == null ? LocalDate.now() : fecha);
                fecha = sentido < 0 ? actual.getInicio().minusDays(1) : actual.getFin().plusDays(1);
            }
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "Periodo no válido", "Elige el periodo de nuevo.");
            return;
        }
        consultar();
    }

    public void abrirCorteCaja() {
        fechaCorte = LocalDate.now();
        try {
            cortePrevisualizacion = helper.previsualizarCorte(fechaCorte);
            PrimeFaces.current().ajax().addCallbackParam("mostrar", true);
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "Corte de caja no disponible", ex.getMessage());
            cortePrevisualizacion = null;
            PrimeFaces.current().ajax().addCallbackParam("mostrar", false);

            vista = CORTE;
            fecha = fechaCorte;
            consultar();
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al calcular corte", ex.getMessage());
            cortePrevisualizacion = null;
            PrimeFaces.current().ajax().addCallbackParam("mostrar", false);
        }
    }

    public void confirmarCorte() {
        try {
            Integer idUsuario = 1;
            helper.registrarCorte(fechaCorte, idUsuario);
            mensaje(FacesMessage.SEVERITY_INFO, "Corte exitoso", "El corte del día " + fechaCorte + " se ha guardado.");
            cortePrevisualizacion = null;

            vista = CORTE;
            fecha = fechaCorte;
            consultar();

            PrimeFaces.current().executeScript("PF('dlgCorteCaja').hide()");
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al guardar", ex.getMessage());
        }
    }
    /**
     * Tickets que se muestran: todos los del periodo o, si se escribió un número en el buscador, el que tenga ese
     * folio ("3", "03" o "#000003" encuentran el mismo ticket).
     */
    public List<ReporteVentas.Ticket> getTicketsFiltrados() {
        if (ventas == null) {
            return new ArrayList<>();
        }
        String digitos = busquedaTicket == null ? "" : busquedaTicket.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            return ventas.getTickets();
        }
        String buscado = digitos.replaceFirst("^0+(?=.)", "");
        List<ReporteVentas.Ticket> encontrados = new ArrayList<>();
        for (ReporteVentas.Ticket t : ventas.getTickets()) {
            if (String.valueOf(t.getNumero()).equals(buscado)) {
                encontrados.add(t);
            }
        }
        return encontrados;
    }

    /** True si hay algo escrito en el buscador de tickets. */
    public boolean isBuscandoTicket() {
        return busquedaTicket != null && !busquedaTicket.replaceAll("\\D", "").isEmpty();
    }

    public void limpiarBusqueda() {
        busquedaTicket = "";
    }

    public String getBusquedaTicket() {
        return busquedaTicket;
    }

    public void setBusquedaTicket(String busquedaTicket) {
        this.busquedaTicket = busquedaTicket == null ? "" : busquedaTicket.trim();
    }

    /** Última fecha que se puede elegir en el calendario: hoy. */
    public LocalDate getFechaMaxima() {
        return LocalDate.now();
    }

    /** True si el periodo del reporte mostrado incluye el día de hoy. */
    public boolean isEnCurso() {
        PeriodoReporte periodo = null;
        if ((VENTAS.equals(vista) || TICKETS.equals(vista)) && ventas != null) {
            periodo = ventas.getPeriodo();
        } else if (GANANCIA.equals(vista) && ganancia != null) {
            periodo = ganancia.getPeriodo();
        } else if (INVENTARIO.equals(vista) && inventario != null) {
            periodo = inventario.getPeriodo();
        }
        return periodo != null && periodo.enCurso(LocalDate.now());
    }

    /* ======================== exportar tickets a CSV ======================== */

    /** CSV de los tickets mostrados (si hay una búsqueda, solo los encontrados). Excel lo abre bien por llevar BOM. */
    public StreamedContent getCsv() {
        String texto = "Sin datos\n";
        String nombre = "tickets.csv";
        if (ventas != null) {
            texto = ExportadorCsv.tickets(ventas.getPeriodo().getTitulo(), getTicketsFiltrados());
            nombre = "tickets_" + ventas.getPeriodo().getTipo().name().toLowerCase(ES_MX) + "_"
                    + ventas.getPeriodo().getInicio() + ".csv";
        }
        byte[] bytes = ("\uFEFF" + texto).getBytes(StandardCharsets.UTF_8);
        return DefaultStreamedContent.builder()
                .name(nombre)
                .contentType("text/csv; charset=UTF-8")
                .stream(() -> new ByteArrayInputStream(bytes))
                .build();
    }

    /* ======================== opciones de los filtros ======================== */

    public PeriodoReporte.Tipo[] getTipos() {
        return PeriodoReporte.Tipo.values();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> items = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String nombre = Month.of(i).getDisplayName(TextStyle.FULL, ES_MX);
            items.add(new SelectItem(i, Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1)));
        }
        return items;
    }

    public List<Integer> getAnios() {
        int actual = LocalDate.now().getYear();
        List<Integer> anios = new ArrayList<>();
        for (int a = actual; a >= actual - 5; a--) {
            anios.add(a);
        }
        return anios;
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    /* ======================== getters y setters ======================== */

    public String getVista() {
        return vista;
    }

    public void setVista(String vista) {
        this.vista = vista;
    }

    public PeriodoReporte.Tipo getTipo() {
        return tipo;
    }

    public void setTipo(PeriodoReporte.Tipo tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
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

    public ReporteVentas getVentas() {
        return ventas;
    }

    public ReporteGanancia getGanancia() {
        return ganancia;
    }

    public ReporteInventario getInventario() {
        return inventario;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public CorteCaja getCortePrevisualizacion() { return cortePrevisualizacion; }

    public LocalDate getFechaCorte() { return fechaCorte; }

    public CorteCaja getCorteConsultado() { return corteConsultado; }
}
