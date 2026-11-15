package Puesto.maria.isabel.delegate;

import java.io.Serializable;

/** Un rescate de piezas de una merma, listo para mostrarse en el historial. */
public class AnulacionRegistro implements Serializable {

    private final String fecha;
    private final int piezas;
    private final String descripcion;
    private final String usuario;

    public AnulacionRegistro(String fecha, int piezas, String descripcion, String usuario) {
        this.fecha = fecha;
        this.piezas = piezas;
        this.descripcion = descripcion;
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public int getPiezas() {
        return piezas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUsuario() {
        return usuario;
    }
}
