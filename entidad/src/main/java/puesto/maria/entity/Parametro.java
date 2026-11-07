package puesto.maria.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "parametro")
public class Parametro {
    @Id
    @Size(max = 100)
    @Column(name = "clave", nullable = false, length = 100)
    private String clave;

    @Size(max = 255)
    @NotNull
    @Column(name = "valor", nullable = false)
    private String valor;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}