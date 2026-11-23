package Puesto.maria.isabel.delegate;

import java.util.List;

/** Regímenes fiscales del SAT más comunes entre quienes piden factura. Se guarda el texto completo. */
public final class CatalogoRegimenes {

    public static final List<String> REGIMENES = List.of(
            "601 - General de Ley Personas Morales",
            "603 - Personas Morales con Fines no Lucrativos",
            "605 - Sueldos y Salarios e Ingresos Asimilados a Salarios",
            "606 - Arrendamiento",
            "612 - Personas Físicas con Actividades Empresariales y Profesionales",
            "616 - Sin obligaciones fiscales",
            "625 - Actividades Empresariales con ingresos a través de Plataformas Tecnológicas",
            "626 - Régimen Simplificado de Confianza");

    private CatalogoRegimenes() {
    }

    public static boolean esValido(String regimen) {
        return regimen != null && REGIMENES.contains(regimen);
    }
}
