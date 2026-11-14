/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package puesto.maria.persistence.integration;

import jakarta.persistence.EntityManager;
import puesto.maria.persistence.dao.*;
import puesto.maria.persistence.persistence.HibernateUtil;


/**
 *
 * @author total
 */
public class ServiceLocator {


    private static UsuarioDAO usuarioDAO;
    private static CategoriaDAO categoriaDAO;
    private static ProveedorDAO proveedorDAO;
    private static ProductoDAO productoDAO;

    private static EntityManager getEntityManager(){
        return HibernateUtil.getEntityManager();
    }

    /**
     * se crea la instancia de usuarioDAO si esta no existe
     */
    public static UsuarioDAO getInstanceUsuarioDAO(){
        if(usuarioDAO == null){
            usuarioDAO = new UsuarioDAO(getEntityManager());
            return usuarioDAO;
        } else{
            return usuarioDAO;
        }
    }

    /**
     * se crea la instancia de categoriaDAO si esta no existe
     */
    public static CategoriaDAO getInstanceCategoriaDAO(){
        if(categoriaDAO == null){
            categoriaDAO = new CategoriaDAO(getEntityManager());
        }
        return categoriaDAO;
    }

    /**
     * se crea la instancia de proveedorDAO si esta no existe
     */
    public static ProveedorDAO getInstanceProveedorDAO(){
        if(proveedorDAO == null){
            proveedorDAO = new ProveedorDAO(getEntityManager());
        }
        return proveedorDAO;
    }

    /**
     * se crea la instancia de productoDAO si esta no existe
     */
    public static ProductoDAO getInstanceProductoDAO(){
        if(productoDAO == null){
            productoDAO = new ProductoDAO(getEntityManager());
        }
        return productoDAO;
    }

}
