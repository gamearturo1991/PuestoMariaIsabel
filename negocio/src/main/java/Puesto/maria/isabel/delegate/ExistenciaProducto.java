package Puesto.maria.isabel.delegate;

import puesto.maria.entity.Producto;

import java.io.Serializable;

/** Un producto con su existencia actual (suma de sus lotes) y su estado de stock. */
public class ExistenciaProducto implements Serializable {

    private final Producto producto;
    private final long existencia;
    private final EstadoStock estado;

    public ExistenciaProducto(Producto producto, long existencia) {
        this.producto = producto;
        this.existencia = existencia;
        this.estado = EstadoStock.de(existencia, producto.getStockMinimo(), producto.getStockMaximo());
    }

    public Producto getProducto() {
        return producto;
    }

    public long getExistencia() {
        return existencia;
    }

    public EstadoStock getEstado() {
        return estado;
    }

    /** True si el producto requiere atención: agotado o con stock bajo. */
    public boolean isAlerta() {
        return estado == EstadoStock.AGOTADO || estado == EstadoStock.BAJO;
    }
}
