package Puesto.maria.isabel.integration;

import Puesto.maria.isabel.facade.FacadeUsuario;
import puesto.maria.entity.Usuario;

public class ServiceFacadeLocator {

    private static FacadeUsuario facadeUsuario;


    public static FacadeUsuario getInstanceFacadeUsuario() {
        if (facadeUsuario == null) {
            facadeUsuario = new FacadeUsuario();
            return facadeUsuario;
        } else {
            return facadeUsuario;
        }
    }
}
