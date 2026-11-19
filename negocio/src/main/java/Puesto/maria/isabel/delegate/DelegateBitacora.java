package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Bitacora;
import puesto.maria.entity.Usuario;
import puesto.maria.persistence.dao.BitacoraDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reglas de la bitácora (PBI-REP-US5).
 * La bitácora es solo de lectura desde el sistema: aquí únicamente se registran y se consultan movimientos.
 */
public class DelegateBitacora {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final int MAX_ACCION = 100;
    private static final int MAX_TABLA = 100;
    private static final int MAX_TEXTO = 4000;
    /** Tope de movimientos que devuelve una consulta. */
    public static final int MAX_RESULTADOS = 1000;

    private BitacoraDAO dao() {
        return ServiceLocator.getInstanceBitacoraDAO();
    }

    /**
     * Guarda un movimiento con la fecha y hora del sistema.
     * Los textos de más de la longitud permitida se recortan para que el registro nunca falle por eso.
     */
    public void registrar(Integer idUsuario, String accion, String tabla, Integer idRegistro,
                          String valorAnterior, String valorNuevo, String motivo) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("No hay un usuario en sesión para registrar el movimiento.");
        }
        if (accion == null || accion.isBlank()) {
            throw new IllegalArgumentException("La acción del movimiento es obligatoria.");
        }
        if (tabla == null || tabla.isBlank()) {
            throw new IllegalArgumentException("La tabla afectada es obligatoria.");
        }
        dao().registrar(idUsuario,
                recortar(accion.trim().toUpperCase(), MAX_ACCION),
                recortar(tabla.trim().toLowerCase(), MAX_TABLA),
                idRegistro,
                vacioANulo(valorAnterior),
                vacioANulo(valorNuevo),
                vacioANulo(motivo));
    }

    /**
     * Consulta los movimientos del periodo. Si no se indican fechas se usan los últimos 7 días.
     * Los filtros vacíos no se aplican.
     */
    public List<RegistroBitacora> consultar(LocalDate desde, LocalDate hasta, Integer idUsuario,
                                            String accion, String tabla) {
        if (hasta == null) {
            hasta = LocalDate.now();
        }
        if (desde == null) {
            desde = hasta.minusDays(6);
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El rango de fechas no es válido: la fecha inicial es posterior a la final.");
        }

        ZoneId zona = ZoneId.systemDefault();
        Instant inicio = desde.atStartOfDay(zona).toInstant();
        Instant fin = hasta.plusDays(1).atStartOfDay(zona).toInstant();

        List<RegistroBitacora> registros = new ArrayList<>();
        for (Bitacora b : dao().consultar(inicio, fin, idUsuario, vacioANulo(accion), vacioANulo(tabla),
                MAX_RESULTADOS)) {
            registros.add(new RegistroBitacora(
                    b.getId(),
                    b.getFechaHora() == null ? "" : b.getFechaHora().atZone(zona).format(FORMATO_FECHA),
                    nombreDe(b.getIdUsuario()),
                    b.getAccion(),
                    b.getTablaAfectada(),
                    b.getIdRegistro(),
                    b.getValorAnterior(),
                    b.getValorNuevo(),
                    b.getMotivo()));
        }
        return registros;
    }

    /** Usuarios del sistema, para el filtro. */
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>(ServiceLocator.getInstanceUsuarioDAO().findAll());
        usuarios.sort(Comparator.comparing(u -> u.getNombreCompleto() == null ? "" : u.getNombreCompleto(),
                String.CASE_INSENSITIVE_ORDER));
        return usuarios;
    }

    public List<String> acciones() {
        return dao().accionesRegistradas();
    }

    public List<String> tablas() {
        return dao().tablasRegistradas();
    }

    private static String nombreDe(Usuario u) {
        if (u == null) {
            return "";
        }
        if (u.getNombreCompleto() != null && !u.getNombreCompleto().isBlank()) {
            return u.getNombreCompleto();
        }
        return u.getNombreUsuario() == null ? "" : u.getNombreUsuario();
    }

    private static String recortar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static String vacioANulo(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        String t = s.trim();
        return t.length() <= MAX_TEXTO ? t : t.substring(0, MAX_TEXTO);
    }
}
