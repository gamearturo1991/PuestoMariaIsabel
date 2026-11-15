package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Proveedor;
import puesto.maria.persistence.dao.ProveedorDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.util.List;
import java.util.Map;

/** Reglas de negocio de proveedores. Los errores se lanzan como IllegalArgumentException con mensaje listo para mostrar. */
public class DelegateProveedor {

    private ProveedorDAO dao() {
        return ServiceLocator.getInstanceProveedorDAO();
    }

    public List<Proveedor> listarProveedores() {
        return dao().listarTodos();
    }

    public Map<Integer, Long> productosPorProveedor() {
        return dao().productosPorProveedor();
    }

    public void saveProveedor(Proveedor proveedor) {
        validar(proveedor);
        dao().save(proveedor);
    }

    public void updateProveedor(Proveedor proveedor) {
        validar(proveedor);
        dao().update(proveedor);
    }

    /** Activa o desactiva un proveedor (los desactivados ya no salen en nuevas entradas ni productos). */
    public void cambiarEstado(Proveedor proveedor) {
        if (proveedor == null || proveedor.getId() == null) {
            throw new IllegalArgumentException("Selecciona un proveedor.");
        }
        proveedor.setActivo(!Boolean.TRUE.equals(proveedor.getActivo()));
        dao().update(proveedor);
    }

    /** Solo se elimina si no tiene productos ni entradas; si los tiene, hay que desactivarlo. */
    public void eliminarProveedor(Proveedor proveedor) {
        if (proveedor == null || proveedor.getId() == null) {
            throw new IllegalArgumentException("Selecciona un proveedor.");
        }
        long productos = dao().contarProductos(proveedor.getId());
        long entradas = dao().contarEntradas(proveedor.getId());
        if (productos > 0 || entradas > 0) {
            throw new IllegalArgumentException("No se puede eliminar \"" + proveedor.getNombre() + "\": tiene "
                    + productos + " producto(s) y " + entradas + " entrada(s). Mejor desactívalo.");
        }
        dao().eliminarPorId(proveedor.getId());
    }

    private void validar(Proveedor p) {
        if (p == null || p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
        }
        p.setNombre(p.getNombre().trim().replaceAll("\\s+", " "));
        p.setEmpresa(vacioANulo(p.getEmpresa()));
        p.setCorreo(vacioANulo(p.getCorreo()));
        p.setTelefono(vacioANulo(p.getTelefono()));

        if (p.getNombre().length() > 255) {
            throw new IllegalArgumentException("El nombre no puede pasar de 255 caracteres.");
        }
        if (p.getEmpresa() != null && p.getEmpresa().length() > 255) {
            throw new IllegalArgumentException("La empresa no puede pasar de 255 caracteres.");
        }
        if (p.getCorreo() != null
                && (p.getCorreo().length() > 255 || !p.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))) {
            throw new IllegalArgumentException("El correo no es válido.");
        }
        if (p.getTelefono() != null
                && (p.getTelefono().length() > 50 || !p.getTelefono().matches("^[0-9+()\\-\\s]+$"))) {
            throw new IllegalArgumentException("El teléfono solo puede llevar números, espacios, +, - y paréntesis.");
        }
        if (p.getActivo() == null) {
            p.setActivo(true);
        }
        if (dao().existeNombre(p.getNombre(), p.getId())) {
            throw new IllegalArgumentException("Ya existe un proveedor con ese nombre.");
        }
    }

    private String vacioANulo(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
