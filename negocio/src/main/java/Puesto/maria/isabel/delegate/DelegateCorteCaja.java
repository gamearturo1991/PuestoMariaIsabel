package Puesto.maria.isabel.delegate;

import puesto.maria.entity.CorteCaja;
import puesto.maria.persistence.dao.CorteCajaDAO;
import puesto.maria.persistence.integration.ServiceLocator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Corte de caja (PBI-CAJ).
 *
 * <ul>
 *   <li><b>Arqueo</b>: se cuenta el efectivo de la caja y se compara con lo que debería haber. Se pueden hacer
 *       los que se quieran durante el día; cada uno cuenta todo el efectivo del día hasta ese momento y queda
 *       en la bitácora (acción ARQUEO_CAJA).</li>
 *   <li><b>Corte final</b>: cierra el día. Guarda los totales en {@code corte_caja} (uno por fecha) y lo
 *       contado y la diferencia en la bitácora (acción CORTE_CAJA), porque la tabla no tiene esas columnas.</li>
 * </ul>
 *
 * Efectivo esperado = efectivo cobrado del día (ya sin el cambio) - devoluciones (se reembolsan en efectivo).
 * Diferencia = esperado - contado: positiva = faltante, negativa = sobrante.
 */
public class DelegateCorteCaja {

    private static final String TABLA_BITACORA = "corte_caja";
    private static final Pattern CAMPO = Pattern.compile("(\\w+)=(-?\\d+(?:\\.\\d+)?)");

    private final DelegateBitacora bitacora = new DelegateBitacora();

    private CorteCajaDAO dao() {
        return ServiceLocator.getInstanceCorteCajaDAO();
    }

    private static ZoneId zona() {
        return ZoneId.systemDefault();
    }

    /** Suma lo cobrado y devuelto en {@code fecha}. No guarda nada. */
    private CorteCaja calcular(LocalDate fecha) {
        Instant desde = fecha.atStartOfDay(zona()).toInstant();
        Instant hasta = fecha.plusDays(1).atStartOfDay(zona()).toInstant();

        CorteCaja corte = new CorteCaja();
        corte.setFecha(fecha);
        corte.setTotalEfectivo(BigDecimal.ZERO);
        corte.setTotalTransferencia(BigDecimal.ZERO);
        corte.setTotalTarjeta(BigDecimal.ZERO);
        corte.setTotalComision(BigDecimal.ZERO);

        for (Object[] fila : dao().sumarPagosPorMetodo(desde, hasta)) {
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
        corte.setTotalDevoluciones(dao().sumarDevoluciones(desde, hasta));

        BigDecimal ingresos = corte.getTotalEfectivo()
                .add(corte.getTotalTransferencia())
                .add(corte.getTotalTarjeta());
        corte.setTotalNeto(ingresos.subtract(corte.getTotalComision()).subtract(corte.getTotalDevoluciones()));
        return corte;
    }

    private static void validarFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Indica la fecha del corte.");
        }
        if (fecha.isAfter(LocalDate.now(zona()))) {
            throw new IllegalArgumentException("No se pueden hacer cortes de fechas futuras.");
        }
    }

    private static BigDecimal validarContado(BigDecimal contado) {
        if (contado == null) {
            throw new IllegalArgumentException("Captura el efectivo que hay en la caja.");
        }
        if (contado.signum() < 0) {
            throw new IllegalArgumentException("El efectivo contado no puede ser negativo.");
        }
        return contado.setScale(2, RoundingMode.HALF_UP);
    }

    /** Efectivo que debería haber en la caja: lo cobrado en efectivo menos las devoluciones. */
    public static BigDecimal efectivoEsperado(CorteCaja corte) {
        BigDecimal efectivo = corte.getTotalEfectivo() == null ? BigDecimal.ZERO : corte.getTotalEfectivo();
        BigDecimal devoluciones = corte.getTotalDevoluciones() == null ? BigDecimal.ZERO : corte.getTotalDevoluciones();
        return efectivo.subtract(devoluciones);
    }

    /** Totales del día para mostrarlos antes de contar. */
    public CorteCaja previsualizarCorte(LocalDate fecha) {
        validarFecha(fecha);
        return calcular(fecha);
    }

    /** Fondo provisional: el efectivo del día anterior, de donde se toma el cambio. Solo informativo. */
    public BigDecimal fondoProvisional(LocalDate fecha) {
        validarFecha(fecha);
        BigDecimal fondo = efectivoEsperado(calcular(fecha.minusDays(1)));
        return fondo.signum() < 0 ? BigDecimal.ZERO : fondo;
    }

    /** Compara lo contado con lo esperado. No guarda nada (la pantalla lo anota en la bitácora). */
    public ResultadoArqueo arqueo(LocalDate fecha, BigDecimal contado) {
        validarFecha(fecha);
        BigDecimal efectivo = validarContado(contado);
        return new ResultadoArqueo(fecha, efectivoEsperado(calcular(fecha)), efectivo);
    }

    /** Cierra el día: guarda los totales y devuelve el resultado del arqueo final. */
    public ResultadoArqueo registrarCorte(LocalDate fecha, Integer idUsuario, BigDecimal contado) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("No hay un usuario en sesión para registrar el corte.");
        }
        validarFecha(fecha);
        BigDecimal efectivo = validarContado(contado);
        if (dao().buscarPorFecha(fecha) != null) {
            throw new IllegalArgumentException("Ya se hizo el corte final del día " + fecha + ".");
        }

        CorteCaja corte = calcular(fecha);
        corte.setFechaHoraCierre(Instant.now());
        dao().guardarCorte(idUsuario, corte);

        ResultadoArqueo resultado = new ResultadoArqueo(fecha, efectivoEsperado(corte), efectivo);
        resultado.setIdCorte(corte.getId());
        return resultado;
    }

    public CorteCaja obtenerCorte(LocalDate fecha) {
        return fecha == null ? null : dao().buscarPorFecha(fecha);
    }

    /** Cortes del periodo con sus sumas, lo contado y la diferencia de cada uno, y los arqueos hechos. */
    public ResumenCortes resumen(PeriodoCortes tipo, LocalDate referencia) {
        if (tipo == null) {
            throw new IllegalArgumentException("Elige el periodo.");
        }
        if (referencia == null) {
            referencia = LocalDate.now(zona());
        }
        LocalDate inicio = tipo.inicio(referencia);
        LocalDate fin = tipo.fin(referencia);

        List<CorteCaja> cortes = dao().listarPorRango(inicio, fin);

        Map<Integer, Map<String, BigDecimal>> contadoPorCorte = new HashMap<>();
        for (RegistroBitacora r : bitacora.consultar(inicio, fin, null, AccionBitacora.CORTE_CAJA, TABLA_BITACORA)) {
            if (r.getIdRegistro() != null) {
                contadoPorCorte.putIfAbsent(r.getIdRegistro(), leer(r.getValorNuevo()));
            }
        }

        List<ResumenCortes.FilaCorte> filas = new ArrayList<>();
        Set<LocalDate> diasConCorte = new HashSet<>();
        for (CorteCaja c : cortes) {
            diasConCorte.add(c.getFecha());
            Map<String, BigDecimal> datos = contadoPorCorte.get(c.getId());
            filas.add(new ResumenCortes.FilaCorte(c,
                    datos == null ? null : datos.get("esperado"),
                    datos == null ? null : datos.get("contado"),
                    datos == null ? null : datos.get("diferencia")));
        }

        List<ResumenCortes.Arqueo> arqueos = new ArrayList<>();
        for (RegistroBitacora r : bitacora.consultar(inicio, fin, null, AccionBitacora.ARQUEO_CAJA, TABLA_BITACORA)) {
            Map<String, BigDecimal> datos = leer(r.getValorNuevo());
            arqueos.add(new ResumenCortes.Arqueo(r.getFecha(), r.getUsuario(),
                    datos.get("esperado"), datos.get("contado"), datos.get("diferencia")));
        }

        // Días ya terminados del periodo en los que no se hizo corte final.
        int sinCorte = 0;
        LocalDate ultimo = LocalDate.now(zona()).minusDays(1);
        if (fin.isBefore(ultimo)) {
            ultimo = fin;
        }
        for (LocalDate d = inicio; !d.isAfter(ultimo); d = d.plusDays(1)) {
            if (!diasConCorte.contains(d)) {
                sinCorte++;
            }
        }

        return new ResumenCortes(tipo, inicio, fin, tipo.titulo(referencia), filas, arqueos, sinCorte);
    }

    /** Lee "esperado=10.00, contado=9.00, diferencia=1.00" de la bitácora. */
    static Map<String, BigDecimal> leer(String texto) {
        Map<String, BigDecimal> datos = new HashMap<>();
        if (texto == null) {
            return datos;
        }
        Matcher m = CAMPO.matcher(texto);
        while (m.find()) {
            datos.put(m.group(1), new BigDecimal(m.group(2)));
        }
        return datos;
    }
}
