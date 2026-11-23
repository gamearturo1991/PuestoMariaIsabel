package helper;

import Puesto.maria.isabel.delegate.HistorialCliente;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import Puesto.maria.isabel.delegate.FilaFacturacion;
import puesto.maria.entity.Cliente;
import puesto.maria.entity.FacturacionCliente;

import java.io.Serializable;
import java.util.List;

public class ClienteHelper implements Serializable {

    public List<Cliente> listar() {
        return ServiceFacadeLocator.getInstanceFacadeCliente().listarClientes();
    }

    public void guardar(Cliente cliente) {
        ServiceFacadeLocator.getInstanceFacadeCliente().saveCliente(cliente);
    }

    public void modificar(Cliente cliente) {
        ServiceFacadeLocator.getInstanceFacadeCliente().updateCliente(cliente);
    }

    public void cambiarEstado(Cliente cliente) {
        ServiceFacadeLocator.getInstanceFacadeCliente().cambiarEstado(cliente);
    }

    public HistorialCliente historial(Cliente cliente) {
        return ServiceFacadeLocator.getInstanceFacadeCliente().historial(cliente);
    }

    public Cliente obtener(Integer id) {
        return ServiceFacadeLocator.getInstanceFacadeCliente().obtener(id);
    }

    public List<FilaFacturacion> facturacionMensual(int anio, int mes) {
        return ServiceFacadeLocator.getInstanceFacadeCliente().facturacionMensual(anio, mes);
    }

    public FacturacionCliente facturar(Integer idCliente, int anio, int mes, String folioFiscal) {
        return ServiceFacadeLocator.getInstanceFacadeCliente().facturar(idCliente, anio, mes, folioFiscal);
    }

    public FacturacionCliente deshacerFactura(Integer idCliente, int anio, int mes, String motivo) {
        return ServiceFacadeLocator.getInstanceFacadeCliente().deshacerFactura(idCliente, anio, mes, motivo);
    }
}
