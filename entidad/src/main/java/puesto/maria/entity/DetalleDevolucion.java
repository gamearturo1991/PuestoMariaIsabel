package puesto.maria.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "detalle_devolucion")
public class DetalleDevolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_devolucion", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_devolucion", nullable = false)
    private Devolucion idDevolucion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_detalle", nullable = false)
    private DetalleVenta idDetalle;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote idLote;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @ColumnDefault("1")
    @Column(name = "revendible")
    private Boolean revendible;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Devolucion getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Devolucion idDevolucion) {
        this.idDevolucion = idDevolucion;
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

    public Boolean getRevendible() {
        return revendible;
    }

    public void setRevendible(Boolean revendible) {
        this.revendible = revendible;
    }

}