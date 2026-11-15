package Puesto.maria.isabel.delegate;

/** Estado de un producto según su existencia frente a sus stocks mínimo y máximo. */
public enum EstadoStock {
    AGOTADO("Agotado"),
    BAJO("Stock bajo"),
    SOBRE_MAXIMO("Sobre el máximo"),
    NORMAL("Normal");

    private final String etiqueta;

    EstadoStock(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Agotado: existencia 0. Stock bajo: existencia menor o igual al mínimo. Sobre el máximo: existencia mayor
     * al máximo (solo es una referencia, no bloquea nada). Normal: cualquier otro caso.
     */
    public static EstadoStock de(long existencia, Integer minimo, Integer maximo) {
        if (existencia <= 0) {
            return AGOTADO;
        }
        if (minimo != null && existencia <= minimo) {
            return BAJO;
        }
        if (maximo != null && existencia > maximo) {
            return SOBRE_MAXIMO;
        }
        return NORMAL;
    }
}
