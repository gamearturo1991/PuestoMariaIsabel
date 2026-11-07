/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;


import puesto.maria.persistence.integration.ServiceLocator;
import puesto.maria.entity.Usuario;

import java.io.Serializable;

public class LoginHelper implements Serializable {
    

    /**
     * Metodo para hacer login llamara a la instancia de usuarioFacade
     * @param correo
     * @param password
     * @return 
     */
    public Usuario Login(String nombreUsuario, String contrasenaHash){
        return ServiceFacadeLocator.getInstanceFacadeUsuario().login(contrasenaHash, nombreUsuario);
    }
    
    
    
}
