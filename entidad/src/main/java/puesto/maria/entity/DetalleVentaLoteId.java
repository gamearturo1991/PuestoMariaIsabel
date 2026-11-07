package puesto.maria.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DetalleVentaLoteId implements Serializable {
    private static final long serialVersionUID = -4454367873081794028L;
    @NotNull
    @Column(name = "id_detalle", nullable = false)
    private Integer idDetalle;

    @NotNull
    @Column(name = "id_lote", nullable = false)
    private Integer idLote;

    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVentaLoteId entity = (DetalleVentaLoteId) o;
        return Objects.equals(this.idDetalle, entity.idDetalle) &&
                Objects.equals(this.idLote, entity.idLote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDetalle, idLote);
    }
}