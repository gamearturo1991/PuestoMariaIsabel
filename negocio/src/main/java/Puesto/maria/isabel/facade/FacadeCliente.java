package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateCliente;
import Puesto.maria.isabel.delegate.HistorialCliente;
import Puesto.maria.isabel.delegate.FilaFacturacion;
import puesto.maria.entity.Cliente;
import puesto.maria.entity.FacturacionCliente;

import java.util.List;

public class FacadeCliente {

    private final DelegateCliente delegateCliente = new DelegateCliente();

    public List<Cliente> listarClientes() {
        return delegateCliente.listarClientes();
    }

    public void saveCliente(Cliente cliente) {
        delegateCliente.saveCliente(cliente);
    }

    public void updateCliente(Cliente cliente) {
        delegateCliente.updateCliente(cliente);
    }

    public void cambiarEstado(Cliente cliente) {
        delegateCliente.cambiarEstado(cliente);
    }

    public HistorialCliente historial(Cliente cliente) {
        return delegateCliente.historial(cliente);
    }

    public Cliente obtener(Integer id) {
        return delegateCliente.obtener(id);
    }

    public List<FilaFacturacion> facturacionMensual(int anio, int mes) {
        return delegateCliente.facturacionMensual(anio, mes);
    }

    public FacturacionCliente facturar(Integer idCliente, int anio, int mes, String folioFiscal) {
        return delegateCliente.facturar(idCliente, anio, mes, folioFiscal);
    }

    public FacturacionCliente deshacerFactura(Integer idCliente, int anio, int mes, String motivo) {
        return delegateCliente.deshacerFactura(idCliente, anio, mes, motivo);
    }
}
