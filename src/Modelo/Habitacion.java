package Modelo;

import Enums.*;

/**
 *
 * @author Wilian Lopez
 */
public class Habitacion {

    private int numero;
    private TipoHabitacion tipo;
    private double precio;
    private String detalle;
    private EstadoHabitacion estado;

    public Habitacion() {
    }

    public Habitacion(int numero, TipoHabitacion tipo, double precio, String detalle, EstadoHabitacion estado) {
        setNumero(numero);
        setTipo(tipo);
        setPrecio(precio);
        setDetalle(detalle);
        setEstado(estado);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        if (numero <= 100) {
            throw new IllegalArgumentException("El número de habitación debe ser mayor a 100.");
        }
        this.numero = numero;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de habitación no puede ser nulo.");
        }
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("La tarifa no puede ser negativa.");
        }
        this.precio = precio;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = estado;
    } 

}
