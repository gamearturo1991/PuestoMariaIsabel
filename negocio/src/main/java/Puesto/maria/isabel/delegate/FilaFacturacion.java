package Puesto.maria.isabel.delegate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Una fila de la pantalla de facturación mensual: lo que compró un cliente en el mes y si ya se le facturó. */
public class FilaFacturacion implements Serializable {

    private final Integer idCliente;
    private final String nombre;
    private final String rfc;
    private final String regimen;
    private final boolean clienteActivo;
    private final long tickets;
    private final BigDecimal total;
    private boolean facturada;
    private final LocalDate fechaFactura;
    private final String folioFiscal;
    private final BigDecimal totalFacturado;

    public FilaFacturacion(Integer idCliente, String nombre, String rfc, String regimen, boolean clienteActivo,
                           long tickets, BigDecimal total, boolean facturada, LocalDate fechaFactura,
                           String folioFiscal, BigDecimal totalFacturado) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.rfc = rfc;
        this.regimen = regimen;
        this.clienteActivo = clienteActivo;
        this.tickets = tickets;
        this.total = total;
        this.facturada = facturada;
        this.fechaFactura = fechaFactura;
        this.folioFiscal = folioFiscal;
        this.totalFacturado = totalFacturado;
    }

    public Integer getIdCliente() { return idCliente; }
    public String getNombre() { return nombre; }
    public String getRfc() { return rfc; }
    public String getRegimen() { return regimen; }
    public boolean isClienteActivo() { return clienteActivo; }
    public long getTickets() { return tickets; }
    /** Compras completadas del mes, calculadas al consultar. */
    public BigDecimal getTotal() { return total; }
    public boolean isFacturada() { return facturada; }
    public void setFacturada(boolean facturada) { this.facturada = facturada; }
    public String getFechaFactura() {
        return fechaFactura == null ? "" : fechaFactura.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    public String getFolioFiscal() { return folioFiscal; }
    /** Total que tenía el mes al marcarlo como facturado. */
    public BigDecimal getTotalFacturado() { return totalFacturado; }
    /** True si el mes ya estaba facturado y el total de hoy es distinto (por ejemplo, se anuló una venta). */
    public boolean isTotalCambio() {
        return facturada && totalFacturado != null && totalFacturado.compareTo(total) != 0;
    }
    /** Se puede marcar como facturado si aún no lo está y hubo compras. */
    public boolean isPuedeFacturar() { return !facturada && total.signum() > 0; }
}
