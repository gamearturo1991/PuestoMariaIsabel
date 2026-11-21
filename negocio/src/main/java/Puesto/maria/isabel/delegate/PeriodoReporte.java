package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Periodo de un reporte. Semanal: de lunes a las 00:00 hasta domingo a las 23:59:59. Mensual: del día 1 a las
 * 00:00 hasta el último día del mes a las 23:59:59. Los límites se calculan en hora local y se convierten a
 * {@link Instant} para consultar la base (que guarda las fechas en UTC).
 */
public final class PeriodoReporte implements Serializable {

    private static final Locale ES_MX = Locale.forLanguageTag("es-MX");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public enum Tipo {
        SEMANAL("Semanal"), MENSUAL("Mensual");

        private final String etiqueta;

        Tipo(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public String getEtiqueta() {
            return etiqueta;
        }
    }

    private final Tipo tipo;
    private final LocalDate inicio;
    private final LocalDate fin;

    private PeriodoReporte(Tipo tipo, LocalDate inicio, LocalDate fin) {
        this.tipo = tipo;
        this.inicio = inicio;
        this.fin = fin;
    }

    /** Periodo del tipo indicado que contiene la fecha de referencia. */
    public static PeriodoReporte de(Tipo tipo, LocalDate referencia) {
        if (tipo == null || referencia == null) {
            throw new IllegalArgumentException("Elige el tipo de periodo y una fecha.");
        }
        if (tipo == Tipo.SEMANAL) {
            LocalDate lunes = referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            return new PeriodoReporte(tipo, lunes, lunes.plusDays(6));
        }
        LocalDate primero = referencia.withDayOfMonth(1);
        return new PeriodoReporte(tipo, primero, primero.with(TemporalAdjusters.lastDayOfMonth()));
    }

    /** Mes calendario indicado. */
    public static PeriodoReporte mes(int anio, int mes) {
        return de(Tipo.MENSUAL, LocalDate.of(anio, mes, 1));
    }

    public Tipo getTipo() {
        return tipo;
    }

    /** Primer día del periodo (inclusive). */
    public LocalDate getInicio() {
        return inicio;
    }

    /** Último día del periodo (inclusive). */
    public LocalDate getFin() {
        return fin;
    }

    /** Instante de inicio (00:00 del primer día, hora local). */
    public Instant desde(ZoneId zona) {
        return inicio.atStartOfDay(zona).toInstant();
    }

    /** Instante en que termina el periodo, excluido: 00:00 del día siguiente al último. */
    public Instant hasta(ZoneId zona) {
        return fin.plusDays(1).atStartOfDay(zona).toInstant();
    }

    /** True si el periodo todavía no termina o apenas empieza hoy: incluye la fecha dada. */
    public boolean enCurso(LocalDate hoy) {
        return !hoy.isBefore(inicio) && !hoy.isAfter(fin);
    }

    /** True si el periodo empieza después de hoy. */
    public boolean esFuturo(LocalDate hoy) {
        return inicio.isAfter(hoy);
    }

    /** Todos los días del periodo, en orden. */
    public List<LocalDate> getDias() {
        List<LocalDate> dias = new ArrayList<>();
        for (LocalDate d = inicio; !d.isAfter(fin); d = d.plusDays(1)) {
            dias.add(d);
        }
        return dias;
    }

    /** Texto para mostrar: "Semana del 17/11/2026 al 23/11/2026" o "Noviembre 2026". */
    public String getTitulo() {
        if (tipo == Tipo.SEMANAL) {
            return "Semana del " + FECHA.format(inicio) + " al " + FECHA.format(fin);
        }
        String mes = inicio.getMonth().getDisplayName(TextStyle.FULL, ES_MX);
        return Character.toUpperCase(mes.charAt(0)) + mes.substring(1) + " " + inicio.getYear();
    }

    @Override
    public String toString() {
        return getTitulo();
    }
}
