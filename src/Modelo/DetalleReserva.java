package Modelo;

import Enums.TipoItem;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Wilian Lopez
 */
public class DetalleReserva {

    private int idDetalle;
    private int idReserva;
    private TipoItem tipoItem;
    private int idItem;
    private String nombreItem;
    private double precioUnitario;

    private SimpleIntegerProperty cantidad;

    public DetalleReserva(int idDetalle, int idReserva, TipoItem tipoItem, int idItem, String nombreItem, int cantidad, double precioUnitario) {
        this.idDetalle = idDetalle;
        this.idReserva = idReserva;
        this.tipoItem = tipoItem;
        this.idItem = idItem;
        this.nombreItem = nombreItem;
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precioUnitario = precioUnitario;
    }

    public DetalleReserva(TipoItem tipoItem, int idItem, String nombreItem, int cantidad, double precioUnitario) {
        this(0, 0, tipoItem, idItem, nombreItem, cantidad, precioUnitario);
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

    public String getNombreItem() {
        return nombreItem;
    }

    public void setNombreItem(String nombreItem) {
        this.nombreItem = nombreItem;
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(int cantidad) {
        this.cantidad.set(cantidad);
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
        return getCantidad() * precioUnitario;
    }

    @Override
    public String toString() {
        return tipoItem + " - " + nombreItem + " x" + cantidad;
    }

    public SimpleIntegerProperty cantidadProperty() {
        return cantidad;
    }
}
