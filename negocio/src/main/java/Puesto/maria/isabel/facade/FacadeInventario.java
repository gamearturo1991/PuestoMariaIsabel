package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateInventario;
import Puesto.maria.isabel.delegate.ExistenciaProducto;

import java.util.List;

public class FacadeInventario {

    private final DelegateInventario delegateInventario;

    public FacadeInventario() {
        this.delegateInventario = new DelegateInventario();
    }

    public List<ExistenciaProducto> consultarExistencias() {
        return delegateInventario.consultarExistencias();
    }

    public List<ExistenciaProducto> listarAlertas() {
        return delegateInventario.listarAlertas();
    }

    public int contarAlertas() {
        return delegateInventario.contarAlertas();
    }
}
