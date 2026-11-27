package Puesto.maria.isabel.delegate;

/**
 * Acciones que se registran en la bitácora (PBI-REP-US5).
 * Cada módulo usa estas constantes para que el filtro de la pantalla Bitácora muestre nombres consistentes.
 * Si un módulo necesita una acción nueva (por ejemplo DEVOLUCION o CORTE_CAJA) la agrega aquí.
 */
public final class AccionBitacora {

    public static final String CREAR = "CREAR";
    public static final String MODIFICAR = "MODIFICAR";
    public static final String ELIMINAR = "ELIMINAR";
    public static final String ACTIVAR = "ACTIVAR";
    public static final String DESACTIVAR = "DESACTIVAR";
    public static final String ENTRADA = "ENTRADA";
    public static final String VENTA = "VENTA";
    public static final String MERMA = "MERMA";
    public static final String ANULAR_MERMA = "ANULAR_MERMA";
    public static final String DEVOLUCION = "DEVOLUCION";
    public static final String CORTE_CAJA = "CORTE_CAJA";
    public static final String ARQUEO_CAJA = "ARQUEO_CAJA";
    public static final String INICIO_SESION = "INICIO_SESION";
    public static final String CIERRE_SESION = "CIERRE_SESION";
    public static final String FACTURAR = "FACTURAR";
    public static final String DESHACER_FACTURA = "DESHACER_FACTURA";

    private AccionBitacora() {
    }
}
