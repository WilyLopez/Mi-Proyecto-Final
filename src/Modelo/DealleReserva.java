package Modelo;

import Enums.TipoItem;

/**
 *
 * @author Wilian Lopez
 */
public class DealleReserva {

    private int idDetalle;
    private int idReserva;
    private TipoItem tipoItem;
    private int idItem;
    private int cantidad;
    private double precioUnitario;

    public DealleReserva(int idDetalle, int idReserva, TipoItem tipoItem, int idItem, int cantidad, double precioUnitario) {
        this.idDetalle = idDetalle;
        this.idReserva = idReserva;
        this.tipoItem = tipoItem;
        this.idItem = idItem;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(TipoItem tipoItem) {
        this.tipoItem = tipoItem;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor que 0");
        }
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        this.precioUnitario = precioUnitario;
    }

    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }
}
