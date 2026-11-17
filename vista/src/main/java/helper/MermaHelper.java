package helper;

import Puesto.maria.isabel.delegate.RegistroMerma;
import Puesto.maria.isabel.delegate.ResultadoMerma;
import Puesto.maria.isabel.delegate.ResumenMotivo;
import Puesto.maria.isabel.integration.ServiceFacadeLocator;
import puesto.maria.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Puente entre MermaBeanUI y las fachadas de mermas, productos y entradas.
 */
public class MermaHelper implements Serializable {

    public List<String> motivos() {
        return ServiceFacadeLocator.getInstanceFacadeMerma().listarMotivos();
    }

    /** Busca productos activos y deshabilitados: un producto deshabilitado todavía puede tener piezas que mermar. */
    public List<Producto> buscarProductos(String texto, int max) {
        return ServiceFacadeLocator.getInstanceFacadeProducto().buscarProductos(texto, false, max);
    }

    public int existencia(Integer idProducto) {
        return ServiceFacadeLocator.getInstanceFacadeEntradaInventario().consultarExistencia(idProducto);
    }

    public BigDecimal estimarPerdida(Integer idProducto, Integer cantidad) {
        return ServiceFacadeLocator.getInstanceFacadeMerma().estimarPerdida(idProducto, cantidad);
    }

    public ResultadoMerma registrar(Integer idProducto, Integer cantidad, String motivo, String observaciones,
                                    Integer idUsuario) {
        return ServiceFacadeLocator.getInstanceFacadeMerma().registrarMerma(
                idProducto, cantidad, motivo, observaciones, idUsuario);
    }

    public void anular(RegistroMerma registro, Integer cantidad, String descripcion, Integer idUsuario) {
        ServiceFacadeLocator.getInstanceFacadeMerma().anularMerma(registro, cantidad, descripcion, idUsuario);
    }

    public List<RegistroMerma> consultar(String motivo, LocalDate desde, LocalDate hasta) {
        return ServiceFacadeLocator.getInstanceFacadeMerma().consultarMermas(motivo, desde, hasta);
    }

    public List<ResumenMotivo> totalesPorMotivo(List<RegistroMerma> registros) {
        return ServiceFacadeLocator.getInstanceFacadeMerma().totalesPorMotivo(registros);
    }
}
