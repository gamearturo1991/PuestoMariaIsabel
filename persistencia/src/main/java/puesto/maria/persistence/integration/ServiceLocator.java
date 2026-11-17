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
    private static EntradaInventarioDAO entradaInventarioDAO;
    private static MermaDAO mermaDAO;
    private static VentaDAO ventaDAO;
    private static ClienteDAO clienteDAO;

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

    /**
     * se crea la instancia de entradaInventarioDAO si esta no existe.
     * Usa el mismo EntityManager que productoDAO para que el producto actualizado por una entrada
     * sea el mismo objeto que ve el listado de productos.
     */
    public static EntradaInventarioDAO getInstanceEntradaInventarioDAO(){
        if(entradaInventarioDAO == null){
            entradaInventarioDAO = new EntradaInventarioDAO(getInstanceProductoDAO().getEntityManager());
        }
        return entradaInventarioDAO;
    }

    /**
     * se crea la instancia de mermaDAO si esta no existe.
     * Usa el mismo EntityManager que productoDAO para que los lotes que descuenta una merma sean los mismos
     * objetos que usan las entradas y la existencia.
     */
    public static MermaDAO getInstanceMermaDAO(){
        if(mermaDAO == null){
            mermaDAO = new MermaDAO(getInstanceProductoDAO().getEntityManager());
        }
        return mermaDAO;
    }

    /**
     * se crea la instancia de ventaDAO si esta no existe.
     * Usa el mismo EntityManager que productoDAO: la venta descuenta de los mismos lotes que usan las entradas
     * y las mermas, asi todos ven la misma existencia y no se pisan los cambios.
     */
    public static VentaDAO getInstanceVentaDAO(){
        if(ventaDAO == null){
            ventaDAO = new VentaDAO(getInstanceProductoDAO().getEntityManager());
        }
        return ventaDAO;
    }

    /**
     * se crea la instancia de clienteDAO si esta no existe
     */
    public static ClienteDAO getInstanceClienteDAO(){
        if(clienteDAO == null){
            clienteDAO = new ClienteDAO(getEntityManager());
        }
        return clienteDAO;
    }

}
