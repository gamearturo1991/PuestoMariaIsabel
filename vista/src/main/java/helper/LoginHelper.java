/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;


import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.Usuario;

import java.io.Serializable;

public class LoginHelper implements Serializable {
    

    /**
     * Metodo para hacer login llamara a la instancia de usuarioFacade
     * @param NombreUsuario
     * @param ContrasenaHash
     * @return 
     */
    public Usuario Login(String NombreUsuario, String ContrasenaHash){
        return ServiceFacadeLocator.getInstanceFacadeUsuario().login(ContrasenaHash, NombreUsuario);
    }
    
    
    
}
