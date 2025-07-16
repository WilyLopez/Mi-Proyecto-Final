package Estructuras;

import Modelo.Habitacion;
import Enums.PrioridadLimpieza;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ColaPrioridadLimpieza {

    private static class Nodo {
        Habitacion habitacion;
        Nodo siguiente;
        PrioridadLimpieza prioridad;
        LocalDateTime horaEncolado;

        public Nodo(Habitacion habitacion, PrioridadLimpieza prioridad) {
            this.habitacion = habitacion;
            this.prioridad = prioridad;
            this.siguiente = null;
            this.horaEncolado = LocalDateTime.now();
        }

        public long getMinutosEnCola() {
            return Duration.between(horaEncolado, LocalDateTime.now()).toMinutes();
        }
    }

    private Nodo cabeza;
    private int tamaño;

    public ColaPrioridadLimpieza() {
        cabeza = null;
        tamaño = 0;
    }

    public void enqueue(Habitacion habitacion, PrioridadLimpieza prioridad) {
        if (habitacion == null || prioridad == null) {
            throw new IllegalArgumentException("Habitación y prioridad no pueden ser nulos");
        }

        Nodo nuevoNodo = new Nodo(habitacion, prioridad);

        if (cabeza == null || prioridad.getValor() > cabeza.prioridad.getValor()) {
            nuevoNodo.siguiente = cabeza;
            cabeza = nuevoNodo;
            tamaño++;
            return;
        }

        Nodo actual = cabeza;
        while (actual.siguiente != null &&
                prioridad.getValor() <= actual.siguiente.prioridad.getValor()) {
            actual = actual.siguiente;
        }

        nuevoNodo.siguiente = actual.siguiente;
        actual.siguiente = nuevoNodo;
        tamaño++;
    }

    public Habitacion dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("No se puede desencolar, la cola está vacía");
        }

        Habitacion habitacion = cabeza.habitacion;
        cabeza = cabeza.siguiente;
        tamaño--;
        return habitacion;
    }

    public Habitacion peek() {
        if (isEmpty()) {
            throw new IllegalStateException("No hay elementos en la cola");
        }
        return cabeza.habitacion;
    }

    public boolean isEmpty() {
        return cabeza == null;
    }

    public int size() {
        return tamaño;
    }

    public boolean remove(int numeroHabitacion) {
        if (isEmpty()) return false;

        if (cabeza.habitacion.getNumero() == numeroHabitacion) {
            dequeue();
            return true;
        }

        Nodo actual = cabeza;
        while (actual.siguiente != null &&
                actual.siguiente.habitacion.getNumero() != numeroHabitacion) {
            actual = actual.siguiente;
        }

        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            tamaño--;
            return true;
        }

        return false;
    }

    public List<Habitacion> getAll() {
        List<Habitacion> habitaciones = new ArrayList<>();
        Nodo actual = cabeza;

        while (actual != null) {
            habitaciones.add(actual.habitacion);
            actual = actual.siguiente;
        }

        return habitaciones;
    }

    public List<Habitacion> getByPriority(PrioridadLimpieza prioridad) {
        List<Habitacion> resultado = new ArrayList<>();
        Nodo actual = cabeza;

        while (actual != null) {
            if (actual.prioridad == prioridad) {
                resultado.add(actual.habitacion);
            }
            actual = actual.siguiente;
        }

        return resultado;
    }

    public void clear() {
        cabeza = null;
        tamaño = 0;
    }

    public int getOrdenDeHabitacion(int numeroHabitacion) {
        Nodo actual = cabeza;
        int orden = 1;
        while (actual != null) {
            if (actual.habitacion.getNumero() == numeroHabitacion) {
                return orden;
            }
            actual = actual.siguiente;
            orden++;
        }
        return -1;
    }

    public long getMinutosEnCola(int numeroHabitacion) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.habitacion.getNumero() == numeroHabitacion) {
                return actual.getMinutosEnCola();
            }
            actual = actual.siguiente;
        }
        return -1;
    }
}
