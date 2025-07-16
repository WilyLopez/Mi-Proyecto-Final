package Estructuras;

import Modelo.Reserva;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
class NodoReserva {

    Reserva reserva;
    NodoReserva izquierda;
    NodoReserva derecha;
    int altura;

    NodoReserva(Reserva reserva) {
        this.reserva = reserva;
        this.altura = 1;
    }
}

public class AVLReservas {

    private NodoReserva raiz;

    private int altura(NodoReserva nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    // balanceo
    private int balance(NodoReserva nodo) {
        return nodo == null ? 0 : altura(nodo.izquierda) - altura(nodo.derecha);
    }

    private NodoReserva rotacionDerecha(NodoReserva y) {
        NodoReserva x = y.izquierda;
        NodoReserva T2 = x.derecha;

        x.derecha = y;
        y.izquierda = T2;

        y.altura = 1 + Math.max(altura(y.izquierda), altura(y.derecha));
        x.altura = 1 + Math.max(altura(x.izquierda), altura(x.derecha));
        return x;
    }

    private NodoReserva rotacionIzquierda(NodoReserva x) {
        NodoReserva y = x.derecha;
        NodoReserva T2 = y.izquierda;

        y.izquierda = x;
        x.derecha = T2;

        x.altura = 1 + Math.max(altura(x.izquierda), altura(x.derecha));
        y.altura = 1 + Math.max(altura(y.izquierda), altura(y.derecha));
        return y;
    }

    // insertar reserva
    public void insertar(Reserva reserva) {
        raiz = insertarRec(raiz, reserva);
    }

    private NodoReserva insertarRec(NodoReserva nodo, Reserva reserva) {
        if (nodo == null) {
            return new NodoReserva(reserva);
        }

        if (reserva.getFechaHoraEntrada().isBefore(nodo.reserva.getFechaHoraEntrada())) {
            nodo.izquierda = insertarRec(nodo.izquierda, reserva);
        } else if (reserva.getFechaHoraEntrada().isAfter(nodo.reserva.getFechaHoraEntrada())) {
            nodo.derecha = insertarRec(nodo.derecha, reserva);
        } else {
            throw new IllegalArgumentException("Ya existe una reserva en esa fecha y hora de entrada.");
        }

        nodo.altura = 1 + Math.max(altura(nodo.izquierda), altura(nodo.derecha));
        int balance = balance(nodo);

        if (balance > 1 && reserva.getFechaHoraEntrada().isBefore(nodo.izquierda.reserva.getFechaHoraEntrada())) {
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && reserva.getFechaHoraEntrada().isAfter(nodo.derecha.reserva.getFechaHoraEntrada())) {
            return rotacionIzquierda(nodo);
        }
        if (balance > 1 && reserva.getFechaHoraEntrada().isAfter(nodo.izquierda.reserva.getFechaHoraEntrada())) {
            nodo.izquierda = rotacionIzquierda(nodo.izquierda);
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && reserva.getFechaHoraEntrada().isBefore(nodo.derecha.reserva.getFechaHoraEntrada())) {
            nodo.derecha = rotacionDerecha(nodo.derecha);
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

    // buscar por fecha
    public Reserva buscarPorFecha(LocalDateTime fecha) {
        return buscarPorFechaRec(raiz, fecha);
    }

    private Reserva buscarPorFechaRec(NodoReserva nodo, LocalDateTime fecha) {
        if (nodo == null) {
            return null;
        }
        if (fecha.isEqual(nodo.reserva.getFechaHoraEntrada())) {
            return nodo.reserva;
        }
        if (fecha.isBefore(nodo.reserva.getFechaHoraEntrada())) {
            return buscarPorFechaRec(nodo.izquierda, fecha);
        }
        return buscarPorFechaRec(nodo.derecha, fecha);
    }

    // buscar por dni huesped
    public List<Reserva> buscarPorDNI(String dni) {
        List<Reserva> resultado = new ArrayList<>();
        buscarPorDNIRec(raiz, dni, resultado);
        return resultado;
    }

    private void buscarPorDNIRec(NodoReserva nodo, String dni, List<Reserva> resultado) {
        if (nodo == null) {
            return;
        }
        buscarPorDNIRec(nodo.izquierda, dni, resultado);
        if (nodo.reserva.getDniHuesped().equals(dni)) {
            resultado.add(nodo.reserva);
        }
        buscarPorDNIRec(nodo.derecha, dni, resultado);
    }

    public List<Reserva> buscarPorHabitacion(int numero) {
        List<Reserva> resultado = new ArrayList<>();
        buscarPorHabitacionRec(raiz, numero, resultado);
        return resultado;
    }

    private void buscarPorHabitacionRec(NodoReserva nodo, int numero, List<Reserva> resultado) {
        if (nodo == null) {
            return;
        }
        buscarPorHabitacionRec(nodo.izquierda, numero, resultado);
        if (nodo.reserva.getNumeroHabitacion() == numero) {
            resultado.add(nodo.reserva);
        }
        buscarPorHabitacionRec(nodo.derecha, numero, resultado);
    }

    public void eliminarPorFecha(LocalDateTime fecha) {
        raiz = eliminarPorFechaRec(raiz, fecha);
    }

    private NodoReserva eliminarPorFechaRec(NodoReserva nodo, LocalDateTime fecha) {
        if (nodo == null) {
            return null;
        }

        if (fecha.isBefore(nodo.reserva.getFechaHoraEntrada())) {
            nodo.izquierda = eliminarPorFechaRec(nodo.izquierda, fecha);
        } else if (fecha.isAfter(nodo.reserva.getFechaHoraEntrada())) {
            nodo.derecha = eliminarPorFechaRec(nodo.derecha, fecha);
        } else {

            if (nodo.izquierda == null) {
                return nodo.derecha;
            }
            if (nodo.derecha == null) {
                return nodo.izquierda;
            }

            NodoReserva sucesor = encontrarMin(nodo.derecha);
            nodo.reserva = sucesor.reserva;
            nodo.derecha = eliminarPorFechaRec(nodo.derecha, sucesor.reserva.getFechaHoraEntrada());
        }

        nodo.altura = 1 + Math.max(altura(nodo.izquierda), altura(nodo.derecha));
        return balancear(nodo);
    }

    public void eliminarPorDni(String dni) {
        List<Reserva> reservas = buscarPorDNI(dni);
        for (Reserva r : reservas) {
            eliminarPorFecha(r.getFechaHoraEntrada());
        }
    }

    // elimar por habitación
    public void eliminarPorHabitacion(int numero) {
        List<Reserva> reservas = buscarPorHabitacion(numero);
        for (Reserva r : reservas) {
            eliminarPorFecha(r.getFechaHoraEntrada());
        }
    }

    public List<Reserva> obtenerInOrdenPorNumeroHabitacion() {
        List<Reserva> resultado = new ArrayList<>();
        inordenRec(raiz, resultado); // Recorre normal por fecha
        resultado.sort((r1, r2) -> Integer.compare(r1.getNumeroHabitacion(), r2.getNumeroHabitacion())); // Ordena por habitación
        return resultado;
    }

    private void inordenRec(NodoReserva nodo, List<Reserva> lista) {
        if (nodo != null) {
            inordenRec(nodo.izquierda, lista);
            lista.add(nodo.reserva);
            inordenRec(nodo.derecha, lista);
        }
    }

    public void inorden() {
        System.out.println("Reservas en orden de entrada:");
        inordenRec(raiz);
    }

    private void inordenRec(NodoReserva nodo) {
        if (nodo != null) {
            inordenRec(nodo.izquierda);
            System.out.println(nodo.reserva);
            inordenRec(nodo.derecha);
        }
    }

    private NodoReserva encontrarMin(NodoReserva nodo) {
        while (nodo.izquierda != null) {
            nodo = nodo.izquierda;
        }
        return nodo;
    }

    //  Rebalancear
    private NodoReserva balancear(NodoReserva nodo) {
        int balance = balance(nodo);

        if (balance > 1 && balance(nodo.izquierda) >= 0) {
            return rotacionDerecha(nodo);
        }
        if (balance > 1 && balance(nodo.izquierda) < 0) {
            nodo.izquierda = rotacionIzquierda(nodo.izquierda);
            return rotacionDerecha(nodo);
        }

        if (balance < -1 && balance(nodo.derecha) <= 0) {
            return rotacionIzquierda(nodo);
        }
        if (balance < -1 && balance(nodo.derecha) > 0) {
            nodo.derecha = rotacionDerecha(nodo.derecha);
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

}
