package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Categoria;
import puesto.maria.persistence.dao.CategoriaDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.util.List;
import java.util.Map;

/**
 * Reglas de negocio de categorías (PBI-PRO-US6).
 * Los errores de validación se lanzan como IllegalArgumentException con un mensaje listo para mostrar.
 */
public class DelegateCategoria {

    private static final int MAX_NOMBRE = 100;

    private CategoriaDAO dao() {
        return ServiceLocator.getInstanceCategoriaDAO();
    }

    public List<Categoria> listarCategorias() {
        return dao().listarOrdenadas();
    }

    /** Cuántos productos (activos y deshabilitados) tiene cada categoría: id de categoría -> cantidad. */
    public Map<Integer, Long> productosPorCategoria() {
        return dao().productosPorCategoria();
    }

    /** Registra una categoría nueva. */
    public void saveCategoria(Categoria categoria) {
        validar(categoria);
        dao().save(categoria);
    }

    /** Modifica el nombre de una categoría existente. Sus productos la conservan (se enlazan por id). */
    public void updateCategoria(Categoria categoria) {
        validar(categoria);
        dao().update(categoria);
    }

    /** Elimina una categoría solo si no tiene productos (activos ni deshabilitados). */
    public void eliminarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new IllegalArgumentException("Selecciona una categoría.");
        }
        long productos = dao().contarProductos(categoria.getId());
        if (productos > 0) {
            throw new IllegalArgumentException("No se puede eliminar \"" + categoria.getNombre() + "\": tiene "
                    + productos + " producto(s). Cámbialos de categoría primero.");
        }
        dao().eliminarPorId(categoria.getId());
    }

    private void validar(Categoria categoria) {
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        String nombre = categoria.getNombre().trim().replaceAll("\\s+", " ");
        if (nombre.length() > MAX_NOMBRE) {
            throw new IllegalArgumentException("El nombre no puede pasar de " + MAX_NOMBRE + " caracteres.");
        }
        categoria.setNombre(nombre);
        if (dao().existeNombre(nombre, categoria.getId())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }
    }
}
