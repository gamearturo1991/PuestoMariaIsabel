package Puesto.maria.isabel.facade;

import Puesto.maria.isabel.delegate.DelegateCorteCaja;
import puesto.maria.entity.CorteCaja;

import java.time.LocalDate;

public class FacadeCorteCaja {

    private final DelegateCorteCaja delegateCorteCaja = new DelegateCorteCaja();

    public CorteCaja previsualizarCorte(LocalDate fecha) {
        return delegateCorteCaja.previsualizarCorte(fecha);
    }

    public void registrarCorte(LocalDate fecha, Integer idUsuario) {
        delegateCorteCaja.registrarCorte(fecha, idUsuario);
    }
    public CorteCaja obtenerCorte(LocalDate fecha) {
        return delegateCorteCaja.obtenerCorte(fecha);
    }
}