package Puesto.maria.isabel.delegate;

import java.io.Serializable;

/** Un movimiento de la bitácora tal como lo ve el administrador (sin entidades perezosas). */
public class RegistroBitacora implements Serializable {

    private final Integer id;
    private final String fecha;
    private final String usuario;
    private final String accion;
    private final String tabla;
    private final Integer idRegistro;
    private final String valorAnterior;
    private final String valorNuevo;
    private final String motivo;

    public RegistroBitacora(Integer id, String fecha, String usuario, String accion, String tabla,
                            Integer idRegistro, String valorAnterior, String valorNuevo, String motivo) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.accion = accion;
        this.tabla = tabla;
        this.idRegistro = idRegistro;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.motivo = motivo;
    }

    public Integer getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getAccion() {
        return accion;
    }

    public String getTabla() {
        return tabla;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public String getValorNuevo() {
        return valorNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    /** True si hay algo que mostrar al expandir la fila. */
    public boolean isTieneDetalle() {
        return tiene(valorAnterior) || tiene(valorNuevo) || tiene(motivo);
    }

    private static boolean tiene(String s) {
        return s != null && !s.isBlank();
    }
}
