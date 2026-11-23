package Puesto.maria.isabel.delegate;

import java.util.regex.Pattern;

/** Valida el formato de un RFC del SAT (persona moral de 12 caracteres o persona física de 13). */
public final class ValidadorRfc {

    /** RFC genéricos para ventas al público en general o a extranjeros: no son clientes. */
    private static final String GENERICO_NACIONAL = "XAXX010101000";
    private static final String GENERICO_EXTRANJERO = "XEXX010101000";

    private static final Pattern MORAL = Pattern.compile("^[A-ZÑ&]{3}(\\d{2})(\\d{2})(\\d{2})[A-Z0-9]{3}$");
    private static final Pattern FISICA = Pattern.compile("^[A-ZÑ&]{4}(\\d{2})(\\d{2})(\\d{2})[A-Z0-9]{3}$");

    private ValidadorRfc() {
    }

    /** Quita espacios y lo pasa a mayúsculas. Devuelve null si viene vacío. */
    public static String normalizar(String rfc) {
        if (rfc == null) {
            return null;
        }
        String limpio = rfc.replaceAll("\\s+", "").toUpperCase();
        return limpio.isEmpty() ? null : limpio;
    }

    /** Devuelve el mensaje del error, o null si el RFC (ya normalizado) es válido. */
    public static String error(String rfc) {
        if (rfc == null || rfc.isEmpty()) {
            return "El RFC es obligatorio.";
        }
        if (rfc.length() != 12 && rfc.length() != 13) {
            return "El RFC debe tener 12 caracteres (persona moral) o 13 (persona física).";
        }
        if (GENERICO_NACIONAL.equals(rfc) || GENERICO_EXTRANJERO.equals(rfc)) {
            return "Ese RFC es genérico (público en general): para esas ventas no se elige cliente.";
        }
        var m = (rfc.length() == 12 ? MORAL : FISICA).matcher(rfc);
        if (!m.matches()) {
            return "El RFC no tiene el formato del SAT (letras, fecha de 6 dígitos y homoclave de 3).";
        }
        int mes = Integer.parseInt(m.group(2));
        int dia = Integer.parseInt(m.group(3));
        if (mes < 1 || mes > 12 || dia < 1 || dia > 31) {
            return "El RFC tiene una fecha no válida.";
        }
        return null;
    }
}
