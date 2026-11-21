package Puesto.maria.isabel.delegate;

import java.math.BigDecimal;
import java.util.List;

/** Arma el texto CSV de los tickets con las mismas cifras que se ven en pantalla. */
public final class ExportadorCsv {

    private ExportadorCsv() {
    }

    /** Tickets del periodo: una fila por producto vendido, con el folio y los pagos repetidos en cada fila. */
    public static String tickets(String titulo, List<ReporteVentas.Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        linea(sb, "Tickets", titulo);
        sb.append('\n');
        linea(sb, "Folio", "Fecha y hora", "Pagos", "Código", "Producto", "Cantidad", "Precio",
                "Subtotal", "Total del ticket");
        for (ReporteVentas.Ticket t : tickets) {
            for (ReporteVentas.LineaTicket l : t.getLineas()) {
                linea(sb, t.getFolio(), t.getFecha(), t.getResumenPagos(), l.getCodigo(), l.getNombre(),
                        l.getCantidad(), l.getPrecio(), l.getSubtotal(), t.getTotal());
            }
        }
        return sb.toString();
    }

    private static void linea(StringBuilder sb, Object... campos) {
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(campo(campos[i]));
        }
        sb.append('\n');
    }

    private static String campo(Object valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor instanceof BigDecimal b ? b.toPlainString() : valor.toString();
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
