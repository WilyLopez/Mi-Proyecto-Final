package Modelo;

import Enums.*;
import java.time.LocalDateTime;

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

    //Atributos para limpieza
    private EstadoLimpieza estadoLimpieza;
    private LocalDateTime ultimaLimpieza;
    private LocalDateTime proximaLimpieza;
    private String notasLimpieza;
    private PrioridadLimpieza prioridadLimpieza;

    public Habitacion() {
        this.estadoLimpieza = EstadoLimpieza.LIMPIA;
    }

    public Habitacion(int numero, TipoHabitacion tipo, double precio, String detalle, EstadoHabitacion estado) {
        this();
        setNumero(numero);
        setTipo(tipo);
        setPrecio(precio);
        setDetalle(detalle);
        setEstado(estado);
    }

    public Habitacion(int numero, TipoHabitacion tipo, double precio, String detalle,
            EstadoHabitacion estado, EstadoLimpieza estadoLimpieza,
            LocalDateTime ultimaLimpieza, String notasLimpieza) {
        this(numero, tipo, precio, detalle, estado);
        setEstadoLimpieza(estadoLimpieza);
        setUltimaLimpieza(ultimaLimpieza);
        setNotasLimpieza(notasLimpieza);
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
    
    public EstadoLimpieza getEstadoLimpieza() {
        return estadoLimpieza;
    }

    public void setEstadoLimpieza(EstadoLimpieza estadoLimpieza) {
        if (estadoLimpieza == null) {
            throw new IllegalArgumentException("El estado de limpieza no puede ser nulo.");
        }
        this.estadoLimpieza = estadoLimpieza;
        
        switch (estadoLimpieza) {
            case PENDIENTE_ALTA:
                this.prioridadLimpieza = PrioridadLimpieza.ALTA;
                break;
            case PENDIENTE_MEDIA:
                this.prioridadLimpieza = PrioridadLimpieza.MEDIA;
                break;
            case PENDIENTE_BAJA:
                this.prioridadLimpieza = PrioridadLimpieza.BAJA;
                break;
            default:
                this.prioridadLimpieza = null;
        }
    }

    public LocalDateTime getUltimaLimpieza() {
        return ultimaLimpieza;
    }

    public void setUltimaLimpieza(LocalDateTime ultimaLimpieza) {
        // Validar que no sea una fecha futura
        if (ultimaLimpieza != null && ultimaLimpieza.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de limpieza no puede ser futura.");
        }
        this.ultimaLimpieza = ultimaLimpieza;
    }

    public String getNotasLimpieza() {
        return notasLimpieza;
    }

    public void setNotasLimpieza(String notasLimpieza) {
        this.notasLimpieza = notasLimpieza;
    }

    public PrioridadLimpieza getPrioridadLimpieza() {
        return prioridadLimpieza;
    }

    public void setPrioridadLimpieza(PrioridadLimpieza prioridadLimpieza) {
        this.prioridadLimpieza = prioridadLimpieza;
    }

    public LocalDateTime getProximaLimpieza() {
        return proximaLimpieza;
    }

    public void setProximaLimpieza(LocalDateTime proximaLimpieza) {
        this.proximaLimpieza = proximaLimpieza;
    }
    
    
    public void marcarComoLimpia(String notas) {
        this.estadoLimpieza = EstadoLimpieza.LIMPIA;
        this.ultimaLimpieza = LocalDateTime.now();
        this.notasLimpieza = notas;
        this.prioridadLimpieza = null;
    }
    
    public void solicitarLimpieza(PrioridadLimpieza prioridad, String notas) {
        switch (prioridad) {
            case ALTA:
                this.estadoLimpieza = EstadoLimpieza.PENDIENTE_ALTA;
                break;
            case MEDIA:
                this.estadoLimpieza = EstadoLimpieza.PENDIENTE_MEDIA;
                break;
            case BAJA:
                this.estadoLimpieza = EstadoLimpieza.PENDIENTE_BAJA;
                break;
        }
        this.prioridadLimpieza = prioridad;
        this.notasLimpieza = notas;
    }

}
