package helper;

import Puesto.maria.isabel.delegate.ExistenciaProducto;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;

import java.io.Serializable;
import java.util.List;

public class InventarioHelper implements Serializable {

    public List<ExistenciaProducto> consultarExistencias() {
        return ServiceFacadeLocator.getInstanceFacadeInventario().consultarExistencias();
    }

    public int contarAlertas() {
        return ServiceFacadeLocator.getInstanceFacadeInventario().contarAlertas();
    }
}
