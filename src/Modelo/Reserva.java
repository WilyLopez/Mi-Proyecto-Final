package Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

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
    private double total;

    private boolean validarFechas = true;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Reserva(String dniHuesped, int numeroHabitacion, String fechaHoraEntrada, String fechaHoraSalida, double total) {
        setDniHuesped(dniHuesped);
        setNumeroHabitacion(numeroHabitacion);
        setFechaHoraEntrada(parseFechaHora(fechaHoraEntrada));
        setFechaHoraSalida(parseFechaHora(fechaHoraSalida));
        setTotal(total);
    }

    public Reserva(String dniHuesped, int numeroHabitacion, LocalDateTime fechaHoraEntrada, LocalDateTime fechaHoraSalida, double total) {
        this.validarFechas = false;
        setDniHuesped(dniHuesped);
        setNumeroHabitacion(numeroHabitacion);
        setFechaHoraEntrada(fechaHoraEntrada);
        setFechaHoraSalida(fechaHoraSalida);
        setTotal(total);
        this.validarFechas = true;
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
        if (fechaHoraEntrada == null) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser nula.");
        }

        LocalDateTime entradaSinSegundos = fechaHoraEntrada.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime ahoraSinSegundos = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        if (validarFechas && entradaSinSegundos.isBefore(ahoraSinSegundos)) {
            throw new IllegalArgumentException("La fecha y hora de entrada debe ser actual o futura.");
        }

        this.fechaHoraEntrada = entradaSinSegundos;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        if (fechaHoraSalida == null) {
            throw new IllegalArgumentException("La fecha de salida no puede ser nula.");
        }

        LocalDateTime salidaSinSegundos = fechaHoraSalida.truncatedTo(ChronoUnit.MINUTES);

        if (this.fechaHoraEntrada != null && salidaSinSegundos.isBefore(this.fechaHoraEntrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
        }

        this.fechaHoraSalida = salidaSinSegundos;
    }

    public static DateTimeFormatter getFORMATO() {
        return FORMATO;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        if (total <= 0) {
            throw new IllegalArgumentException("Total invalido");
        }
        this.total = total;
    }

    public static LocalDateTime parseFechaHora(String fechaHoraStr) {
        try {
            return LocalDateTime.parse(fechaHoraStr, FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha/hora inválido. Usa yyyy-MM-dd HH:mm");
        }
    }

}
