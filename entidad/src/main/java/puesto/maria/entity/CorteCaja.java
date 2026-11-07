package puesto.maria.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "corte_caja")
public class CorteCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corte", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @ColumnDefault("0.00")
    @Column(name = "total_efectivo", precision = 12, scale = 2)
    private BigDecimal totalEfectivo;

    @ColumnDefault("0.00")
    @Column(name = "total_transferencia", precision = 12, scale = 2)
    private BigDecimal totalTransferencia;

    @ColumnDefault("0.00")
    @Column(name = "total_tarjeta", precision = 12, scale = 2)
    private BigDecimal totalTarjeta;

    @ColumnDefault("0.00")
    @Column(name = "total_devoluciones", precision = 12, scale = 2)
    private BigDecimal totalDevoluciones;

    @ColumnDefault("0.00")
    @Column(name = "total_comision", precision = 12, scale = 2)
    private BigDecimal totalComision;

    @ColumnDefault("0.00")
    @Column(name = "total_neto", precision = 12, scale = 2)
    private BigDecimal totalNeto;

    @Column(name = "fecha_hora_cierre")
    private Instant fechaHoraCierre;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getTotalEfectivo() {
        return totalEfectivo;
    }

    public void setTotalEfectivo(BigDecimal totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    public BigDecimal getTotalTransferencia() {
        return totalTransferencia;
    }

    public void setTotalTransferencia(BigDecimal totalTransferencia) {
        this.totalTransferencia = totalTransferencia;
    }

    public BigDecimal getTotalTarjeta() {
        return totalTarjeta;
    }

    public void setTotalTarjeta(BigDecimal totalTarjeta) {
        this.totalTarjeta = totalTarjeta;
    }

    public BigDecimal getTotalDevoluciones() {
        return totalDevoluciones;
    }

    public void setTotalDevoluciones(BigDecimal totalDevoluciones) {
        this.totalDevoluciones = totalDevoluciones;
    }

    public BigDecimal getTotalComision() {
        return totalComision;
    }

    public void setTotalComision(BigDecimal totalComision) {
        this.totalComision = totalComision;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public Instant getFechaHoraCierre() {
        return fechaHoraCierre;
    }

    public void setFechaHoraCierre(Instant fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }

}