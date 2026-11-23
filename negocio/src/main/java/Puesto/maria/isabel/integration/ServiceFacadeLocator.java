package Puesto.maria.isabel.integration;

import Puesto.maria.isabel.facade.FacadeBitacora;
import Puesto.maria.isabel.facade.FacadeCategoria;
import Puesto.maria.isabel.facade.FacadeEntradaInventario;
import Puesto.maria.isabel.facade.FacadeInventario;
import Puesto.maria.isabel.facade.FacadeMerma;
import Puesto.maria.isabel.facade.FacadeCliente;
import Puesto.maria.isabel.facade.FacadeProducto;
import Puesto.maria.isabel.facade.FacadeProveedor;
import Puesto.maria.isabel.facade.FacadeReportes;
import Puesto.maria.isabel.facade.FacadeUsuario;
import puesto.maria.entity.Usuario;
import Puesto.maria.isabel.facade.FacadeVenta;
import Puesto.maria.isabel.facade.FacadeDevolucion;


public class ServiceFacadeLocator {

    private static FacadeUsuario facadeUsuario;
    private static FacadeProducto facadeProducto;
    private static FacadeEntradaInventario facadeEntradaInventario;
    private static FacadeInventario facadeInventario;
    private static FacadeCategoria facadeCategoria;
    private static FacadeMerma facadeMerma;
    private static FacadeDevolucion facadeDevolucion;
    private static FacadeVenta facadeVenta;
    private static FacadeProveedor facadeProveedor;
    private static FacadeBitacora facadeBitacora;
    private static FacadeReportes facadeReportes;
    private static FacadeCliente facadeCliente;

    public static FacadeUsuario getInstanceFacadeUsuario() {
        if (facadeUsuario == null) {
            facadeUsuario = new FacadeUsuario();
            return facadeUsuario;
        } else {
            return facadeUsuario;
        }
    }

    public static FacadeProducto getInstanceFacadeProducto() {
        if (facadeProducto == null) {
            facadeProducto = new FacadeProducto();
        }
        return facadeProducto;
    }

    public static FacadeEntradaInventario getInstanceFacadeEntradaInventario() {
        if (facadeEntradaInventario == null) {
            facadeEntradaInventario = new FacadeEntradaInventario();
        }
        return facadeEntradaInventario;
    }

    public static FacadeInventario getInstanceFacadeInventario() {
        if (facadeInventario == null) {
            facadeInventario = new FacadeInventario();
        }
        return facadeInventario;
    }

    public static FacadeCategoria getInstanceFacadeCategoria() {
        if (facadeCategoria == null) {
            facadeCategoria = new FacadeCategoria();
        }
        return facadeCategoria;
    }

    public static FacadeMerma getInstanceFacadeMerma() {
        if (facadeMerma == null) {
            facadeMerma = new FacadeMerma();
        }
        return facadeMerma;
    }

    public static FacadeVenta getInstanceFacadeVenta() {
        if (facadeVenta == null) {
            facadeVenta = new FacadeVenta();
        }
        return facadeVenta;
    }

    public static FacadeProveedor getInstanceFacadeProveedor() {
        if (facadeProveedor == null) {
            facadeProveedor = new FacadeProveedor();
        }
        return facadeProveedor;
    }

    public static FacadeBitacora getInstanceFacadeBitacora() {
        if (facadeBitacora == null) {
            facadeBitacora = new FacadeBitacora();
        }
        return facadeBitacora;
    }

    public static FacadeDevolucion getInstanceFacadeDevolucion() {
        if (facadeDevolucion == null) {
            facadeDevolucion = new FacadeDevolucion();
        }
        return facadeDevolucion;
    }

    public static FacadeReportes getInstanceFacadeReportes() {
        if (facadeReportes == null) {
            facadeReportes = new FacadeReportes();
        }
        return facadeReportes;
    }

    public static FacadeCliente getInstanceFacadeCliente() {
        if (facadeCliente == null) {
            facadeCliente = new FacadeCliente();
        }
        return facadeCliente;
    }
}
