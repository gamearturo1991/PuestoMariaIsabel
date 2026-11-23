package ui;

import helper.ClienteHelper;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import puesto.maria.entity.Cliente;

/** Convierte un Cliente a su id (y de regreso) para el selector de cliente de la pantalla de ventas. */
@FacesConverter(forClass = Cliente.class)
public class ClienteConverter implements Converter<Cliente> {

    @Override
    public Cliente getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new ClienteHelper().obtener(Integer.valueOf(value.trim()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Cliente cliente) {
        return cliente == null || cliente.getId() == null ? "" : String.valueOf(cliente.getId());
    }
}
