package Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author Wilian Lopez
 */
public class Reserva {

    private int idReserva;
    private String dniHuesped;
    private int numeroHabitacion;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Constructor para entrada de datos como texto
    public Reserva(int idReserva, String dniHuesped, int numeroHabitacion, String fechaHoraEntrada, String fechaHoraSalida) {
        setIdReserva(idReserva);
        setDniHuesped(dniHuesped);
        setNumeroHabitacion(numeroHabitacion);
        setFechaHoraEntrada(parseFechaHora(fechaHoraEntrada));
        setFechaHoraSalida(parseFechaHora(fechaHoraSalida));
    }

    // Constructor para uso con base de datos (usa LocalDateTime directamente)
    public Reserva(int idReserva, String dniHuesped, int numeroHabitacion, LocalDateTime fechaHoraEntrada, LocalDateTime fechaHoraSalida) {
        setIdReserva(idReserva);
        setDniHuesped(dniHuesped);
        setNumeroHabitacion(numeroHabitacion);
        setFechaHoraEntrada(fechaHoraEntrada);
        setFechaHoraSalida(fechaHoraSalida);
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        if (idReserva <= 0) {
            throw new IllegalArgumentException("El ID de reserva debe ser positivo.");
        }
        this.idReserva = idReserva;
    }

    public String getDniHuesped() {
        return dniHuesped;
    }

    public void setDniHuesped(String dniHuesped) {
        if (dniHuesped == null || !dniHuesped.matches("\\d{8}")) {
            throw new IllegalArgumentException("DNI del huésped no válido.");
        }
        this.dniHuesped = dniHuesped;
    }

    public int getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(int numeroHabitacion) {
        if (numeroHabitacion <= 100) {
            throw new IllegalArgumentException("Número de habitación inválido.");
        }
        this.numeroHabitacion = numeroHabitacion;
    }

    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) {
        if (fechaHoraEntrada == null || fechaHoraEntrada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora de entrada debe ser actual o futura.");
        }
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        if (fechaHoraSalida == null || !fechaHoraSalida.isAfter(this.fechaHoraEntrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
        }
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public static DateTimeFormatter getFORMATO() {
        return FORMATO;
    }

    public static LocalDateTime parseFechaHora(String fechaHoraStr) {
        try {
            return LocalDateTime.parse(fechaHoraStr, FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha/hora inválido. Usa yyyy-MM-dd HH:mm");
        }
    }

}
