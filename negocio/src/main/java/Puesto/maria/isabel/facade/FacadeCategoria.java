package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateCategoria;
import puesto.maria.entity.Categoria;

import java.util.List;
import java.util.Map;

public class FacadeCategoria {

    private final DelegateCategoria delegateCategoria;

    public FacadeCategoria() {
        this.delegateCategoria = new DelegateCategoria();
    }

    public List<Categoria> listarCategorias() {
        return delegateCategoria.listarCategorias();
    }

    public Map<Integer, Long> productosPorCategoria() {
        return delegateCategoria.productosPorCategoria();
    }

    public void saveCategoria(Categoria categoria) {
        delegateCategoria.saveCategoria(categoria);
    }

    public void updateCategoria(Categoria categoria) {
        delegateCategoria.updateCategoria(categoria);
    }

    public void eliminarCategoria(Categoria categoria) {
        delegateCategoria.eliminarCategoria(categoria);
    }
}
