package puesto.maria.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "detalle_venta_lote")
public class DetalleVentaLote {
    @EmbeddedId
    private DetalleVentaLoteId id;

    @MapsId("idDetalle")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_detalle", nullable = false)
    private DetalleVenta idDetalle;

    @MapsId("idLote")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote idLote;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public DetalleVentaLoteId getId() {
        return id;
    }

    public void setId(DetalleVentaLoteId id) {
        this.id = id;
    }

    public DetalleVenta getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(DetalleVenta idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Lote getIdLote() {
        return idLote;
    }

    public void setIdLote(Lote idLote) {
        this.idLote = idLote;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}