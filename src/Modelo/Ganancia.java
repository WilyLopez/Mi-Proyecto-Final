package Modelo;

import java.time.LocalDate;
/**
 *
 * @author Wilian Lopez
 */
public class Ganancia implements Comparable<Ganancia> {
    private int id;
    private int reservaId;
    private LocalDate fecha;
    private double montoTotal;
    private String detalle;

    public Ganancia(LocalDate fecha, double montoTotal, String detalle) {
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.detalle = detalle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReservaId() {
        return reservaId;
    }

    public void setReservaId(int reservaId) {
        this.reservaId = reservaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public int compareTo(Ganancia otra) {
        return this.fecha.compareTo(otra.fecha);
    }
}
