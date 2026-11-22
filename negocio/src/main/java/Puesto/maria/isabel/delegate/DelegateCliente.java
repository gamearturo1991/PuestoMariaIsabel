package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Bitacora;
import puesto.maria.entity.Cliente;
import puesto.maria.entity.FacturacionCliente;
import puesto.maria.persistence.dao.ClienteDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * Reglas de negocio de clientes. Un cliente nunca se elimina: se desactiva, para no afectar ventas anteriores.
 * El RFC identifica al cliente y no se puede cambiar; el nombre y el régimen sí, y el historial queda en la bitácora.
 */
public class DelegateCliente {

    private ClienteDAO dao() {
        return ServiceLocator.getInstanceClienteDAO();
    }

    public List<Cliente> listarClientes() {
        return dao().listarTodos();
    }

    public void saveCliente(Cliente c) {
        validarNombreYRegimen(c);
        c.setRfc(ValidadorRfc.normalizar(c.getRfc()));
        String error = ValidadorRfc.error(c.getRfc());
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        if (dao().existeRfc(c.getRfc(), null)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese RFC.");
        }
        c.setActivo(true);
        dao().save(c);
    }

    /** Solo cambian nombre y régimen; el RFC y el estado se conservan como están en la base de datos. */
    public void updateCliente(Cliente editado) {
        if (editado == null || editado.getId() == null) {
            throw new IllegalArgumentException("Selecciona un cliente.");
        }
        validarNombreYRegimen(editado);
        Cliente actual = dao().findFresh(editado.getId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente ya no existe."));
        actual.setNombre(editado.getNombre());
        actual.setRegimenFiscal(editado.getRegimenFiscal());
        dao().update(actual);
        editado.setRfc(actual.getRfc());
        editado.setActivo(actual.getActivo());
    }

    public void cambiarEstado(Cliente c) {
        if (c == null || c.getId() == null) {
            throw new IllegalArgumentException("Selecciona un cliente.");
        }
        Cliente actual = dao().findFresh(c.getId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente ya no existe."));
        actual.setActivo(!Boolean.TRUE.equals(actual.getActivo()));
        dao().update(actual);
        c.setActivo(actual.getActivo());
    }

    /** Historial de nombre y régimen del cliente, armado con la bitácora. */
    public HistorialCliente historial(Cliente c) {
        List<HistorialCliente.Evento> eventos = new ArrayList<>();
        for (Bitacora b : dao().movimientos(c.getId())) {
            eventos.add(new HistorialCliente.Evento(b.getFechaHora(), b.getAccion(), b.getValorAnterior(), b.getValorNuevo()));
        }
        return new HistorialCliente(eventos, c.getNombre(), c.getRegimenFiscal());
    }

    public Cliente obtener(Integer id) {
        return id == null ? null : dao().find(id).orElse(null);
    }

    /* ======================== facturación mensual ======================== */

    private static ZoneId zona() {
        return ZoneId.systemDefault();
    }

    private PeriodoReporte periodoMes(int anio, int mes) {
        PeriodoReporte p;
        try {
            p = PeriodoReporte.mes(anio, mes);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("El mes indicado no es válido.");
        }
        if (p.esFuturo(LocalDate.now(zona()))) {
            throw new IllegalArgumentException("No se pueden consultar meses futuros.");
        }
        return p;
    }

    /**
     * Cuánto compró cada cliente en el mes (solo ventas completadas) y si ya se le facturó. Se calcula al
     * consultar, así que si una venta se anula el total cambia. El nombre y régimen son los del cierre del mes.
     */
    public List<FilaFacturacion> facturacionMensual(int anio, int mes) {
        PeriodoReporte p = periodoMes(anio, mes);
        Instant desde = p.desde(zona());
        Instant hasta = p.hasta(zona());

        Map<Integer, Object[]> totales = new HashMap<>();
        for (Object[] f : dao().totalesPorCliente(desde, hasta)) {
            totales.put((Integer) f[0], f);
        }
        Map<Integer, FacturacionCliente> facturas = new HashMap<>();
        for (FacturacionCliente f : dao().facturacionesDelMes(anio, mes)) {
            facturas.put(f.getIdCliente().getId(), f);
        }
        Set<Integer> ids = new HashSet<>(totales.keySet());
        ids.addAll(facturas.keySet());

        List<FilaFacturacion> filas = new ArrayList<>();
        for (Integer id : ids) {
            Cliente c = obtener(id);
            if (c == null) {
                continue;
            }
            HistorialCliente.Datos d = historial(c).en(hasta.minusSeconds(1));
            Object[] t = totales.get(id);
            long tickets = t == null ? 0 : ((Number) t[1]).longValue();
            BigDecimal total = t == null || t[2] == null ? BigDecimal.ZERO : (BigDecimal) t[2];
            FacturacionCliente fac = facturas.get(id);
            filas.add(new FilaFacturacion(id, d.nombre(), c.getRfc(), d.regimen(), Boolean.TRUE.equals(c.getActivo()),
                    tickets, total, fac != null && Boolean.TRUE.equals(fac.getFacturada()),
                    fac == null ? null : fac.getFechaFacturacion(), fac == null ? null : fac.getFolioFiscal(),
                    fac == null ? null : fac.getTotalCompras()));
        }
        filas.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
        return filas;
    }

    /** Marca el mes del cliente como facturado: guarda el total de ese momento, la fecha de hoy y el folio fiscal. */
    public FacturacionCliente facturar(Integer idCliente, int anio, int mes, String folioFiscal) {
        PeriodoReporte p = periodoMes(anio, mes);
        Cliente c = obtener(idCliente);
        if (c == null) {
            throw new IllegalArgumentException("El cliente ya no existe.");
        }
        String folio = folioFiscal == null ? "" : folioFiscal.trim();
        if (folio.isEmpty()) {
            throw new IllegalArgumentException("El folio fiscal es obligatorio.");
        }
        if (folio.length() > 100 || !folio.matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("El folio fiscal solo puede llevar letras, números y guiones (máximo 100).");
        }
        if (dao().buscarFacturacion(idCliente, anio, mes) != null) {
            throw new IllegalArgumentException("Ese mes ya está marcado como facturado.");
        }
        BigDecimal total = dao().totalDeCliente(idCliente, p.desde(zona()), p.hasta(zona()));
        if (total.signum() <= 0) {
            throw new IllegalArgumentException("El cliente no tiene compras completadas en ese mes.");
        }
        FacturacionCliente f = new FacturacionCliente();
        f.setIdCliente(c);
        f.setAnio(anio);
        f.setMes(mes);
        f.setTotalCompras(total);
        f.setFacturada(true);
        f.setFechaFacturacion(LocalDate.now(zona()));
        f.setFolioFiscal(folio);
        dao().guardarFacturacion(f);
        return f;
    }

    /** Quita la marca de facturado. El motivo es obligatorio y lo guarda el módulo en la bitácora. */
    public FacturacionCliente deshacerFactura(Integer idCliente, int anio, int mes, String motivo) {
        periodoMes(anio, mes);
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Escribe el motivo para deshacer la factura.");
        }
        if (motivo.trim().length() > 500) {
            throw new IllegalArgumentException("El motivo no puede pasar de 500 caracteres.");
        }
        FacturacionCliente f = dao().buscarFacturacion(idCliente, anio, mes);
        if (f == null) {
            throw new IllegalArgumentException("Ese mes no está marcado como facturado.");
        }
        dao().eliminarFacturacion(f.getId());
        return f;
    }

    private void validarNombreYRegimen(Cliente c) {
        if (c == null || c.getNombre() == null || c.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre o razón social es obligatorio.");
        }
        c.setNombre(c.getNombre().trim().replaceAll("\\s+", " "));
        if (c.getNombre().length() > 255) {
            throw new IllegalArgumentException("El nombre no puede pasar de 255 caracteres.");
        }
        if (c.getNombre().contains("|") || c.getNombre().contains("=")) {
            throw new IllegalArgumentException("El nombre no puede llevar los caracteres \"|\" ni \"=\".");
        }
        if (!CatalogoRegimenes.esValido(c.getRegimenFiscal())) {
            throw new IllegalArgumentException("Elige el régimen fiscal de la lista.");
        }
    }
}
