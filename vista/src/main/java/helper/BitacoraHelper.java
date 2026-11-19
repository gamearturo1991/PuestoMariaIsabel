package helper;

import Puesto.maria.isabel.delegate.AccionBitacora;
import Puesto.maria.isabel.delegate.RegistroBitacora;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Puente entre las pantallas y la bitácora (PBI-REP-US5).
 *
 * Cómo la usa un módulo, después de que su operación se concretó:
 * <pre>
 *   bitacora.registrar(loginUI.getUsuario(), AccionBitacora.CREAR, "cliente", cliente.getId(), null,
 *           BitacoraHelper.texto(campos), null);
 * </pre>
 * o con los atajos {@link #registrarAlta}, {@link #registrarCambio} y {@link #registrarBaja}.
 * Si guardar en la bitácora falla, el error queda en el log del servidor y la operación del negocio
 * no se deshace ni se interrumpe.
 */
public class BitacoraHelper implements Serializable {

    private static final Logger LOG = Logger.getLogger(BitacoraHelper.class.getName());

    /* ======================== registrar ======================== */

    /** Registra un movimiento. Nunca lanza excepciones. */
    public void registrar(Usuario usuario, String accion, String tabla, Integer idRegistro,
                          String valorAnterior, String valorNuevo, String motivo) {
        try {
            Integer idUsuario = usuario == null ? null : usuario.getId();
            ServiceFacadeLocator.getInstanceFacadeBitacora()
                    .registrar(idUsuario, accion, tabla, idRegistro, valorAnterior, valorNuevo, motivo);
        } catch (RuntimeException ex) {
            LOG.log(Level.WARNING, "No se pudo registrar en la bitácora: " + accion + " en " + tabla, ex);
        }
    }

    /** Alta de un registro: solo valor nuevo. */
    public void registrarAlta(Usuario usuario, String tabla, Integer idRegistro, Map<String, Object> nuevo) {
        registrar(usuario, AccionBitacora.CREAR, tabla, idRegistro, null, texto(nuevo), null);
    }

    /** Modificación: guarda solo los campos que cambiaron, con su valor anterior y su valor nuevo. */
    public void registrarCambio(Usuario usuario, String tabla, Integer idRegistro,
                                Map<String, Object> antes, Map<String, Object> despues) {
        String[] c = cambios(antes, despues);
        registrar(usuario, AccionBitacora.MODIFICAR, tabla, idRegistro, c[0], c[1], null);
    }

    /** Eliminación: solo valor anterior. */
    public void registrarBaja(Usuario usuario, String tabla, Integer idRegistro, Map<String, Object> anterior) {
        registrar(usuario, AccionBitacora.ELIMINAR, tabla, idRegistro, texto(anterior), null, null);
    }

    /* ======================== formato de valores ======================== */

    /** Crea un mapa ordenado campo -&gt; valor a partir de pares: campos("nombre", n, "precio", p). */
    public static Map<String, Object> campos(Object... pares) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        for (int i = 0; i + 1 < pares.length; i += 2) {
            mapa.put(String.valueOf(pares[i]), pares[i + 1]);
        }
        return mapa;
    }

    /** "campo=valor, campo=valor". Devuelve null si no hay campos. */
    public static String texto(Map<String, Object> campos) {
        if (campos == null || campos.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : campos.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(e.getKey()).append('=').append(valor(e.getValue()));
        }
        return sb.toString();
    }

    /**
     * Compara dos fotos del mismo registro y devuelve {valorAnterior, valorNuevo} solo con los campos que
     * cambiaron. Si no cambió nada, ambos dicen "(sin cambios)".
     */
    public static String[] cambios(Map<String, Object> antes, Map<String, Object> despues) {
        Map<String, Object> a = new LinkedHashMap<>();
        Map<String, Object> d = new LinkedHashMap<>();
        if (despues != null) {
            for (Map.Entry<String, Object> e : despues.entrySet()) {
                Object previo = antes == null ? null : antes.get(e.getKey());
                if (!Objects.equals(valor(previo), valor(e.getValue()))) {
                    a.put(e.getKey(), previo);
                    d.put(e.getKey(), e.getValue());
                }
            }
        }
        if (d.isEmpty()) {
            return new String[]{"(sin cambios)", "(sin cambios)"};
        }
        return new String[]{texto(a), texto(d)};
    }

    private static String valor(Object v) {
        if (v == null) {
            return "(vacío)";
        }
        if (v instanceof BigDecimal) {
            return ((BigDecimal) v).setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        if (v instanceof Boolean) {
            return ((Boolean) v) ? "Sí" : "No";
        }
        String s = v.toString();
        return s.isBlank() ? "(vacío)" : s;
    }

    /* ======================== consultar ======================== */

    public List<RegistroBitacora> consultar(LocalDate desde, LocalDate hasta, Integer idUsuario,
                                            String accion, String tabla) {
        return ServiceFacadeLocator.getInstanceFacadeBitacora().consultar(desde, hasta, idUsuario, accion, tabla);
    }

    public List<puesto.maria.entity.Usuario> usuarios() {
        return ServiceFacadeLocator.getInstanceFacadeBitacora().listarUsuarios();
    }

    public List<String> acciones() {
        return ServiceFacadeLocator.getInstanceFacadeBitacora().acciones();
    }

    public List<String> tablas() {
        return ServiceFacadeLocator.getInstanceFacadeBitacora().tablas();
    }
}
