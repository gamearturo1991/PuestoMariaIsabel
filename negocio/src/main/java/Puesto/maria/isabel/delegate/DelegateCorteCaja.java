package Puesto.maria.isabel.delegate;

import puesto.maria.entity.CorteCaja;
import puesto.maria.entity.Usuario;
import puesto.maria.persistence.dao.CorteCajaDAO;
import puesto.maria.persistence.dao.ReporteDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class DelegateCorteCaja {

    private CorteCajaDAO dao() {
        return ServiceLocator.getInstanceCorteCajaDAO();
    }

    private static ZoneId zona() {
        return ZoneId.systemDefault();
    }


    public CorteCaja previsualizarCorte(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Indica la fecha del corte.");
        }
        if (fecha.isAfter(LocalDate.now(zona()))) {
            throw new IllegalArgumentException("No se pueden hacer cortes de fechas futuras.");
        }

        CorteCaja existente = dao().buscarPorFecha(fecha);
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un corte registrado para la fecha " + fecha + ".");
        }

        Instant desde = fecha.atStartOfDay(zona()).toInstant();
        Instant hasta = fecha.plusDays(1).atStartOfDay(zona()).toInstant();

        CorteCaja corte = new CorteCaja();
        corte.setFecha(fecha);
        corte.setTotalEfectivo(BigDecimal.ZERO);
        corte.setTotalTransferencia(BigDecimal.ZERO);
        corte.setTotalTarjeta(BigDecimal.ZERO);
        corte.setTotalComision(BigDecimal.ZERO);

        List<Object[]> pagos = dao().sumarPagosPorMetodo(desde, hasta);
        for (Object[] fila : pagos) {
            String metodo = (String) fila[0];
            BigDecimal monto = fila[1] == null ? BigDecimal.ZERO : (BigDecimal) fila[1];
            BigDecimal comision = fila[2] == null ? BigDecimal.ZERO : (BigDecimal) fila[2];

            corte.setTotalComision(corte.getTotalComision().add(comision));

            if ("EFECTIVO".equalsIgnoreCase(metodo)) {
                corte.setTotalEfectivo(corte.getTotalEfectivo().add(monto));
            } else if ("TRANSFERENCIA".equalsIgnoreCase(metodo)) {
                corte.setTotalTransferencia(corte.getTotalTransferencia().add(monto));
            } else if ("TARJETA".equalsIgnoreCase(metodo)) {
                corte.setTotalTarjeta(corte.getTotalTarjeta().add(monto));
            }
        }

        BigDecimal devoluciones = dao().sumarDevoluciones(desde, hasta);
        corte.setTotalDevoluciones(devoluciones);

        // Calcular Total Neto (Efectivo + Transf + Tarjeta - Comisiones - Devoluciones)
        BigDecimal ingresosBrutos = corte.getTotalEfectivo()
                .add(corte.getTotalTransferencia())
                .add(corte.getTotalTarjeta());

        BigDecimal totalNeto = ingresosBrutos.subtract(corte.getTotalComision()).subtract(corte.getTotalDevoluciones());
        corte.setTotalNeto(totalNeto);

        return corte;
    }


    public void registrarCorte(LocalDate fecha, Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("No hay un usuario en sesión para registrar el corte.");
        }

        Instant desde = fecha.atStartOfDay(zona()).toInstant();
        Instant hasta = fecha.plusDays(1).atStartOfDay(zona()).toInstant();

        CorteCaja corte = new CorteCaja();
        corte.setFecha(fecha);
        corte.setTotalEfectivo(BigDecimal.ZERO);
        corte.setTotalTransferencia(BigDecimal.ZERO);
        corte.setTotalTarjeta(BigDecimal.ZERO);
        corte.setTotalComision(BigDecimal.ZERO);

        List<Object[]> pagos = dao().sumarPagosPorMetodo(desde, hasta);
        for (Object[] fila : pagos) {
            String metodo = (String) fila[0];
            BigDecimal monto = fila[1] == null ? BigDecimal.ZERO : (BigDecimal) fila[1];
            BigDecimal comision = fila[2] == null ? BigDecimal.ZERO : (BigDecimal) fila[2];

            corte.setTotalComision(corte.getTotalComision().add(comision));

            if ("EFECTIVO".equalsIgnoreCase(metodo)) {
                corte.setTotalEfectivo(corte.getTotalEfectivo().add(monto));
            } else if ("TRANSFERENCIA".equalsIgnoreCase(metodo)) {
                corte.setTotalTransferencia(corte.getTotalTransferencia().add(monto));
            } else if ("TARJETA".equalsIgnoreCase(metodo)) {
                corte.setTotalTarjeta(corte.getTotalTarjeta().add(monto));
            }
        }

        BigDecimal devoluciones = dao().sumarDevoluciones(desde, hasta);
        corte.setTotalDevoluciones(devoluciones);

        BigDecimal ingresosBrutos = corte.getTotalEfectivo()
                .add(corte.getTotalTransferencia())
                .add(corte.getTotalTarjeta());

        BigDecimal totalNeto = ingresosBrutos.subtract(corte.getTotalComision()).subtract(corte.getTotalDevoluciones());
        corte.setTotalNeto(totalNeto);
        corte.setFechaHoraCierre(Instant.now());

        dao().guardarCorte(idUsuario, corte);
    }

    public CorteCaja obtenerCorte(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return dao().buscarPorFecha(fecha);
    }
}
