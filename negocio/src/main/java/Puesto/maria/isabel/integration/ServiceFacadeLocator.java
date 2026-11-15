package Puesto.maria.isabel.integration;

import Puesto.maria.isabel.facade.FacadeEntradaInventario;
import Puesto.maria.isabel.facade.FacadeInventario;
import Puesto.maria.isabel.facade.FacadeProducto;
import Puesto.maria.isabel.facade.FacadeUsuario;
import puesto.maria.entity.Usuario;

public class ServiceFacadeLocator {

    private static FacadeUsuario facadeUsuario;
    private static FacadeProducto facadeProducto;
    private static FacadeEntradaInventario facadeEntradaInventario;
    private static FacadeInventario facadeInventario;


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
}
