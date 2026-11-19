package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateBitacora;
import Puesto.maria.isabel.delegate.RegistroBitacora;
import puesto.maria.entity.Usuario;

import java.time.LocalDate;
import java.util.List;

public class FacadeBitacora {

    private final DelegateBitacora delegateBitacora = new DelegateBitacora();

    public void registrar(Integer idUsuario, String accion, String tabla, Integer idRegistro,
                          String valorAnterior, String valorNuevo, String motivo) {
        delegateBitacora.registrar(idUsuario, accion, tabla, idRegistro, valorAnterior, valorNuevo, motivo);
    }

    public List<RegistroBitacora> consultar(LocalDate desde, LocalDate hasta, Integer idUsuario,
                                            String accion, String tabla) {
        return delegateBitacora.consultar(desde, hasta, idUsuario, accion, tabla);
    }

    public List<Usuario> listarUsuarios() {
        return delegateBitacora.listarUsuarios();
    }

    public List<String> acciones() {
        return delegateBitacora.acciones();
    }

    public List<String> tablas() {
        return delegateBitacora.tablas();
    }
}
