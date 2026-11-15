package ui;

import helper.EntradaInventarioHelper;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import puesto.maria.entity.Producto;

/** Convierte un Producto a su id (y de regreso) para los componentes de selección, como la búsqueda. */
@FacesConverter(forClass = Producto.class)
public class ProductoConverter implements Converter<Producto> {

    @Override
    public Producto getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new EntradaInventarioHelper().obtenerProducto(Integer.valueOf(value.trim()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Producto producto) {
        return producto == null || producto.getId() == null ? "" : String.valueOf(producto.getId());
    }
}
