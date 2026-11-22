package ui;

import Puesto.maria.isabel.delegate.AccionBitacora;
import Puesto.maria.isabel.delegate.CatalogoRegimenes;
import Puesto.maria.isabel.delegate.FilaFacturacion;
import Puesto.maria.isabel.delegate.HistorialCliente;
import Puesto.maria.isabel.delegate.ValidadorRfc;
import helper.BitacoraHelper;
import helper.ClienteHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import puesto.maria.entity.Cliente;
import puesto.maria.entity.FacturacionCliente;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pantalla de clientes: registrar, modificar nombre y régimen, y activar/desactivar. Nunca se eliminan. */
@Named("clienteUI")
@SessionScoped
public class ClienteBeanUI implements Serializable {

    private final ClienteHelper helper = new ClienteHelper();
    private final BitacoraHelper bitacoraHelper = new BitacoraHelper();

    @Inject
    private LoginBeanUI loginUI;

    private List<Cliente> clientes = new ArrayList<>();
    private String filtro = "";

    /** Cliente que se está registrando o editando. */
    private Cliente cliente;
    /** Nombre y régimen al abrir la edición, para saber qué cambió. */
    private String nombreAntes;
    private String regimenAntes;

    /** Cliente cuyo historial se está viendo. */
    private Cliente clienteHistorial;
    private List<String> nombresHistorial = new ArrayList<>();

    /* ---- facturación mensual ---- */
    private static final String[] NOMBRES_MES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    /** Pestaña visible: CLIENTES o FACTURACION. */
    private String vista = "CLIENTES";
    private int anio;
    private int mes;
    private List<FilaFacturacion> facturacion = new ArrayList<>();
    private FilaFacturacion filaSeleccionada;
    private String folioFiscal;
    private String motivo;

    @PostConstruct
    public void init() {
        nuevo();
        cargar();
        LocalDate hoy = LocalDate.now();
        anio = hoy.getYear();
        mes = hoy.getMonthValue();
    }

    public void cargar() {
        clientes = helper.listar();
    }

    public void nuevo() {
        cliente = new Cliente();
        cliente.setActivo(true);
        nombreAntes = null;
        regimenAntes = null;
    }

    public void editar(Cliente seleccionado) {
        Cliente copia = new Cliente();
        copia.setId(seleccionado.getId());
        copia.setNombre(seleccionado.getNombre());
        copia.setRfc(seleccionado.getRfc());
        copia.setRegimenFiscal(seleccionado.getRegimenFiscal());
        copia.setActivo(seleccionado.getActivo());
        cliente = copia;
        nombreAntes = copia.getNombre();
        regimenAntes = copia.getRegimenFiscal();
    }

    public void guardar() {
        try {
            boolean esNuevo = cliente.getId() == null;
            if (esNuevo) {
                helper.guardar(cliente);
                bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.CREAR, "cliente", cliente.getId(), null,
                        HistorialCliente.texto(HistorialCliente.CAMPO_NOMBRE, cliente.getNombre(),
                                HistorialCliente.CAMPO_RFC, cliente.getRfc(),
                                HistorialCliente.CAMPO_REGIMEN, cliente.getRegimenFiscal()), null);
            } else {
                helper.modificar(cliente);
                registrarCambio();
            }
            mensaje(FacesMessage.SEVERITY_INFO, esNuevo ? "Cliente registrado" : "Cliente modificado",
                    cliente.getNombre());
            PrimeFaces.current().ajax().addCallbackParam("guardado", true);
            nuevo();
            cargar();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo guardar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al guardar el cliente", ex.getMessage());
        }
    }

    /** Solo registra en la bitácora los campos que cambiaron (formato que lee HistorialCliente). */
    private void registrarCambio() {
        List<String> anterior = new ArrayList<>();
        List<String> nuevo = new ArrayList<>();
        if (!java.util.Objects.equals(nombreAntes, cliente.getNombre())) {
            anterior.add(HistorialCliente.texto(HistorialCliente.CAMPO_NOMBRE, nombreAntes));
            nuevo.add(HistorialCliente.texto(HistorialCliente.CAMPO_NOMBRE, cliente.getNombre()));
        }
        if (!java.util.Objects.equals(regimenAntes, cliente.getRegimenFiscal())) {
            anterior.add(HistorialCliente.texto(HistorialCliente.CAMPO_REGIMEN, regimenAntes));
            nuevo.add(HistorialCliente.texto(HistorialCliente.CAMPO_REGIMEN, cliente.getRegimenFiscal()));
        }
        if (!nuevo.isEmpty()) {
            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.MODIFICAR, "cliente", cliente.getId(),
                    String.join(HistorialCliente.SEPARADOR, anterior),
                    String.join(HistorialCliente.SEPARADOR, nuevo), null);
        }
    }

    public void cambiarEstado(Cliente seleccionado) {
        try {
            helper.cambiarEstado(seleccionado);
            boolean activo = Boolean.TRUE.equals(seleccionado.getActivo());
            bitacoraHelper.registrar(loginUI.getUsuario(),
                    activo ? AccionBitacora.ACTIVAR : AccionBitacora.DESACTIVAR, "cliente", seleccionado.getId(),
                    "activo=" + (activo ? "No" : "Sí"), "activo=" + (activo ? "Sí" : "No"), null);
            mensaje(FacesMessage.SEVERITY_INFO, activo ? "Cliente activado" : "Cliente desactivado",
                    seleccionado.getNombre());
            cargar();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo cambiar el estado", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cambiar el estado", ex.getMessage());
        }
    }

    public void verHistorial(Cliente seleccionado) {
        clienteHistorial = seleccionado;
        try {
            nombresHistorial = helper.historial(seleccionado).nombres();
        } catch (RuntimeException ex) {
            nombresHistorial = new ArrayList<>();
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo leer el historial", ex.getMessage());
        }
    }

    /** Pasa el RFC a mayúsculas mientras se escribe (se valida de nuevo al guardar). */
    public void normalizarRfc() {
        String r = ValidadorRfc.normalizar(cliente.getRfc());
        cliente.setRfc(r);
    }

    /** Clientes que coinciden con el texto buscado (nombre o RFC). */
    public List<Cliente> getClientesFiltrados() {
        if (filtro == null || filtro.isBlank()) {
            return clientes;
        }
        String f = filtro.trim().toLowerCase();
        List<Cliente> r = new ArrayList<>();
        for (Cliente c : clientes) {
            if ((c.getNombre() != null && c.getNombre().toLowerCase().contains(f))
                    || (c.getRfc() != null && c.getRfc().toLowerCase().contains(f))) {
                r.add(c);
            }
        }
        return r;
    }

    public List<String> getRegimenes() {
        return CatalogoRegimenes.REGIMENES;
    }

    /* ======================== facturación mensual ======================== */

    public void cargarFacturacion() {
        try {
            facturacion = helper.facturacionMensual(anio, mes);
        } catch (IllegalArgumentException ex) {
            facturacion = new ArrayList<>();
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo consultar", ex.getMessage());
        } catch (RuntimeException ex) {
            facturacion = new ArrayList<>();
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al consultar la facturación", ex.getMessage());
        }
    }

    /** Cambia entre la lista de clientes y la facturación mensual. */
    public void cambiarVista() {
        if ("FACTURACION".equals(vista)) {
            cargarFacturacion();
        } else {
            cargar();
        }
    }

    /** Se llama al marcar la casilla: guarda de qué fila se trata y deja listo el diálogo del folio fiscal. */
    public void alMarcar(FilaFacturacion fila) {
        filaSeleccionada = fila;
        folioFiscal = null;
    }

    /** Si se cancela el diálogo, se recarga la tabla para que la casilla vuelva a quedar sin marcar. */
    public void cancelarFactura() {
        filaSeleccionada = null;
        folioFiscal = null;
        cargarFacturacion();
    }

    public void confirmarFactura() {
        try {
            FacturacionCliente f = helper.facturar(filaSeleccionada.getIdCliente(), anio, mes, folioFiscal);
            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.FACTURAR, "facturacion_cliente", f.getId(),
                    null, HistorialCliente.texto("cliente", filaSeleccionada.getNombre(),
                            "rfc", filaSeleccionada.getRfc(), "periodo", periodo(),
                            "total", f.getTotalCompras().toPlainString(), "folioFiscal", f.getFolioFiscal()), null);
            mensaje(FacesMessage.SEVERITY_INFO, "Mes facturado", filaSeleccionada.getNombre() + " · " + periodo());
            PrimeFaces.current().ajax().addCallbackParam("guardado", true);
            filaSeleccionada = null;
            folioFiscal = null;
            cargarFacturacion();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo facturar", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al marcar como facturado", ex.getMessage());
        }
    }

    public void prepararDeshacer(FilaFacturacion fila) {
        filaSeleccionada = fila;
        motivo = null;
    }

    public void confirmarDeshacer() {
        try {
            FacturacionCliente f = helper.deshacerFactura(filaSeleccionada.getIdCliente(), anio, mes, motivo);
            bitacoraHelper.registrar(loginUI.getUsuario(), AccionBitacora.DESHACER_FACTURA, "facturacion_cliente",
                    f.getId(), HistorialCliente.texto("cliente", filaSeleccionada.getNombre(),
                            "rfc", filaSeleccionada.getRfc(), "periodo", periodo(),
                            "total", f.getTotalCompras().toPlainString(), "folioFiscal", f.getFolioFiscal()),
                    null, motivo.trim());
            mensaje(FacesMessage.SEVERITY_INFO, "Factura deshecha", filaSeleccionada.getNombre() + " · " + periodo());
            PrimeFaces.current().ajax().addCallbackParam("guardado", true);
            filaSeleccionada = null;
            motivo = null;
            cargarFacturacion();
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se pudo deshacer", ex.getMessage());
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Error al deshacer la factura", ex.getMessage());
        }
    }

    private String periodo() {
        return String.format("%04d-%02d", anio, mes);
    }

    public String getTituloMes() {
        return NOMBRES_MES[Math.max(0, Math.min(11, mes - 1))] + " " + anio;
    }

    public Map<String, Integer> getMeses() {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (int i = 0; i < 12; i++) {
            m.put(NOMBRES_MES[i], i + 1);
        }
        return m;
    }

    public List<Integer> getAnios() {
        int actual = LocalDate.now().getYear();
        List<Integer> l = new ArrayList<>();
        for (int a = actual; a >= actual - 5; a--) {
            l.add(a);
        }
        return l;
    }

    private void mensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Cliente getClienteHistorial() {
        return clienteHistorial;
    }

    public List<String> getNombresHistorial() {
        return nombresHistorial;
    }

    public String getVista() { return vista; }
    public void setVista(String vista) { this.vista = vista; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }
    public List<FilaFacturacion> getFacturacion() { return facturacion; }
    public FilaFacturacion getFilaSeleccionada() { return filaSeleccionada; }
    public String getFolioFiscal() { return folioFiscal; }
    public void setFolioFiscal(String folioFiscal) { this.folioFiscal = folioFiscal; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
