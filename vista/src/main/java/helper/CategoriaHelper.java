package helper;

import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.Categoria;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CategoriaHelper implements Serializable {

    public List<Categoria> listar() {
        return ServiceFacadeLocator.getInstanceFacadeCategoria().listarCategorias();
    }

    public Map<Integer, Long> productosPorCategoria() {
        return ServiceFacadeLocator.getInstanceFacadeCategoria().productosPorCategoria();
    }

    public void guardar(Categoria categoria) {
        ServiceFacadeLocator.getInstanceFacadeCategoria().saveCategoria(categoria);
    }

    public void modificar(Categoria categoria) {
        ServiceFacadeLocator.getInstanceFacadeCategoria().updateCategoria(categoria);
    }

    public void eliminar(Categoria categoria) {
        ServiceFacadeLocator.getInstanceFacadeCategoria().eliminarCategoria(categoria);
    }
}
