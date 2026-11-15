package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateMerma;
import Puesto.maria.isabel.delegate.RegistroMerma;
import Puesto.maria.isabel.delegate.ResultadoMerma;
import Puesto.maria.isabel.delegate.ResumenMotivo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FacadeMerma {

    private final DelegateMerma delegateMerma;

    public FacadeMerma() {
        this.delegateMerma = new DelegateMerma();
    }

    public List<String> listarMotivos() {
        return delegateMerma.listarMotivos();
    }

    public BigDecimal estimarPerdida(Integer idProducto, Integer cantidad) {
        return delegateMerma.estimarPerdida(idProducto, cantidad);
    }

    public ResultadoMerma registrarMerma(Integer idProducto, Integer cantidad, String motivo,
                                         String observaciones, Integer idUsuario) {
        return delegateMerma.registrarMerma(idProducto, cantidad, motivo, observaciones, idUsuario);
    }

    public void anularMerma(RegistroMerma registro, Integer cantidad, String descripcion, Integer idUsuario) {
        delegateMerma.anularMerma(registro, cantidad, descripcion, idUsuario);
    }

    public List<RegistroMerma> consultarMermas(String motivo, LocalDate desde, LocalDate hasta) {
        return delegateMerma.consultarMermas(motivo, desde, hasta);
    }

    public List<ResumenMotivo> totalesPorMotivo(List<RegistroMerma> registros) {
        return delegateMerma.totalesPorMotivo(registros);
    }
}
