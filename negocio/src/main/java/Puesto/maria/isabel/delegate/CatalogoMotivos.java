package Puesto.maria.isabel.delegate;

import java.util.List;

/**
 * Motivos de merma que se pueden elegir. Es una lista fija del sistema (la base de datos no tiene una tabla
 * de motivos): el motivo elegido se guarda como texto en cada merma.
 */
public final class CatalogoMotivos {

    public static final String EMPAQUE_DANADO = "Empaque dañado";
    public static final String CADUCADO = "Caducado";
    public static final String CONSUMO_PROPIO = "Consumo propio";
    public static final String FALTANTE_LOGISTICA = "Faltante o pérdida de logística";

    private static final List<String> MOTIVOS =
            List.of(EMPAQUE_DANADO, CADUCADO, CONSUMO_PROPIO, FALTANTE_LOGISTICA);

    private CatalogoMotivos() {
    }

    /** Motivos que se pueden elegir al mermar y al filtrar el historial. */
    public static List<String> motivos() {
        return MOTIVOS;
    }

    public static boolean esValido(String motivo) {
        return motivo != null && MOTIVOS.contains(motivo);
    }
}
