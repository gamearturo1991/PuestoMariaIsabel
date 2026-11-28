package Puesto.maria.isabel.delegate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/**
 * Periodos en los que se suman los cortes de caja (para la contabilidad): día, semana (lunes a domingo), mes,
 * bimestre (ene-feb, mar-abr...), semestre (ene-jun, jul-dic) y año.
 */
public enum PeriodoCortes {
    DIA("Día"),
    SEMANA("Semana"),
    MES("Mes"),
    BIMESTRE("Bimestre"),
    SEMESTRE("Semestre"),
    ANIO("Año");

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es-MX"));

    private final String etiqueta;

    PeriodoCortes(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Primer día del periodo que contiene a {@code referencia}. */
    public LocalDate inicio(LocalDate referencia) {
        return switch (this) {
            case DIA -> referencia;
            case SEMANA -> referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MES -> referencia.withDayOfMonth(1);
            case BIMESTRE -> LocalDate.of(referencia.getYear(), ((referencia.getMonthValue() - 1) / 2) * 2 + 1, 1);
            case SEMESTRE -> LocalDate.of(referencia.getYear(), referencia.getMonthValue() <= 6 ? 1 : 7, 1);
            case ANIO -> LocalDate.of(referencia.getYear(), 1, 1);
        };
    }

    /** Último día del periodo que contiene a {@code referencia}. */
    public LocalDate fin(LocalDate referencia) {
        LocalDate inicio = inicio(referencia);
        return switch (this) {
            case DIA -> inicio;
            case SEMANA -> inicio.plusDays(6);
            case MES -> inicio.plusMonths(1).minusDays(1);
            case BIMESTRE -> inicio.plusMonths(2).minusDays(1);
            case SEMESTRE -> inicio.plusMonths(6).minusDays(1);
            case ANIO -> inicio.plusYears(1).minusDays(1);
        };
    }

    /** Texto del periodo, por ejemplo "Semana del 23/11/2026 al 29/11/2026". */
    public String titulo(LocalDate referencia) {
        LocalDate inicio = inicio(referencia);
        LocalDate fin = fin(referencia);
        if (this == DIA) {
            return "Día " + inicio.format(FECHA);
        }
        return etiqueta + " del " + inicio.format(FECHA) + " al " + fin.format(FECHA);
    }
}
