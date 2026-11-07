package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Usuario;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.persistence.integration.ServiceLocator;

import java.util.List;

public class DelegateUsuario {
    public Usuario login(String contrasenaHash, String nombreUsuario){
        Usuario usuario = new Usuario();
        List<Usuario> usuarios = ServiceLocator.getInstanceUsuarioDAO().findAll();

        for(Usuario us:usuarios){
            if(us.getContrasenaHash().equalsIgnoreCase(contrasenaHash) && us.getNombreUsuario().equalsIgnoreCase(nombreUsuario)){
                usuario = us;
            }
        }
        return usuario;
    }

    public void saveUsario(Usuario usuario){
        ServiceLocator.getInstanceUsuarioDAO().save(usuario);
    }

}