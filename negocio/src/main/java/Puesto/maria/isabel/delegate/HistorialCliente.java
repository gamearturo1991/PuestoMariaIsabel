package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Historial del nombre y del régimen de UN cliente, armado con sus movimientos de bitácora (altas y
 * modificaciones). Sirve para saber qué nombre y régimen tenía el cliente el día de una venta.
 *
 * <p>Formato de los textos que se guardan en la bitácora (sin tocar la base de datos):
 * {@code nombre=Juan Pérez | rfc=PEPJ800101AB1 | regimen=612 - ...}. En una modificación solo van los campos
 * que cambiaron. Los campos van separados por " | " (por eso el nombre no puede llevar el carácter "|").
 */
public class HistorialCliente implements Serializable {

    public static final String SEPARADOR = " | ";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_REGIMEN = "regimen";
    public static final String CAMPO_RFC = "rfc";

    /** Un movimiento de la bitácora sobre el cliente. */
    public record Evento(Instant fecha, String accion, String anterior, String nuevo) implements Serializable {
    }

    /** Nombre y régimen en un momento dado. */
    public record Datos(String nombre, String regimen) implements Serializable {
    }

    private final List<Evento> eventos;
    private final String nombreActual;
    private final String regimenActual;

    /** Los eventos deben venir ordenados de más antiguo a más reciente. */
    public HistorialCliente(List<Evento> eventos, String nombreActual, String regimenActual) {
        this.eventos = eventos == null ? new ArrayList<>() : eventos;
        this.nombreActual = nombreActual;
        this.regimenActual = regimenActual;
    }

    /** Nombre y régimen que tenía el cliente en {@code momento}. */
    public Datos en(Instant momento) {
        return new Datos(valorEn(CAMPO_NOMBRE, momento, nombreActual), valorEn(CAMPO_REGIMEN, momento, regimenActual));
    }

    /** Nombres que ha tenido el cliente, del primero al actual, sin repetir seguidos. */
    public List<String> nombres() {
        List<String> nombres = new ArrayList<>();
        Instant antes = eventos.isEmpty() ? Instant.now() : eventos.get(0).fecha().minusSeconds(1);
        agregar(nombres, en(antes).nombre());
        for (Evento e : eventos) {
            agregar(nombres, en(e.fecha()).nombre());
        }
        agregar(nombres, nombreActual);
        return nombres;
    }

    private static void agregar(List<String> lista, String valor) {
        if (valor != null && (lista.isEmpty() || !lista.get(lista.size() - 1).equals(valor))) {
            lista.add(valor);
        }
    }

    private String valorEn(String campo, Instant momento, String actual) {
        String vigente = null;
        for (Evento e : eventos) {
            if (e.fecha().isAfter(momento)) {
                break;
            }
            String nuevo = leer(e.nuevo()).get(campo);
            if (nuevo != null) {
                vigente = nuevo;
            }
        }
        if (vigente != null) {
            return vigente;
        }
        // Todavía no había cambios registrados: el valor de entonces es el "anterior" del primer cambio posterior.
        for (Evento e : eventos) {
            if (e.fecha().isAfter(momento)) {
                String previo = leer(e.anterior()).get(campo);
                if (previo != null) {
                    return previo;
                }
            }
        }
        return actual;
    }

    /** Convierte "campo=valor | campo=valor" en un mapa. Ignora lo que no tenga ese formato. */
    public static Map<String, String> leer(String texto) {
        Map<String, String> mapa = new LinkedHashMap<>();
        if (texto == null || texto.isBlank()) {
            return mapa;
        }
        for (String parte : texto.split("\\s\\|\\s")) {
            int i = parte.indexOf('=');
            if (i > 0) {
                String valor = parte.substring(i + 1).trim();
                if (!valor.isEmpty() && !valor.equals("(vacío)")) {
                    mapa.put(parte.substring(0, i).trim(), valor);
                }
            }
        }
        return mapa;
    }

    /** "campo=valor | campo=valor" a partir de pares campo, valor. */
    public static String texto(String... pares) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < pares.length; i += 2) {
            if (sb.length() > 0) {
                sb.append(SEPARADOR);
            }
            sb.append(pares[i]).append('=').append(pares[i + 1] == null || pares[i + 1].isBlank() ? "(vacío)" : pares[i + 1]);
        }
        return sb.toString();
    }
}
