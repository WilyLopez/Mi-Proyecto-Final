package Estructuras;

import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author Wilian Lopez
 */
class NodoLimpieza {

    int numeroHabitacion;
    Set<LocalDate> fechas;
    NodoLimpieza izquierda, derecha;

    NodoLimpieza(int numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
        this.fechas = new TreeSet<>();
    }
}

public class TADAgendaLimpieza {

    private NodoLimpieza raiz;

    // Insertar fecha de limpieza para una habitación
    public void agendarLimpieza(int numeroHabitacion, LocalDate fecha) {
        raiz = insertarRec(raiz, numeroHabitacion, fecha);
    }

    private NodoLimpieza insertarRec(NodoLimpieza nodo, int numero, LocalDate fecha) {
        if (nodo == null) {
            NodoLimpieza nuevo = new NodoLimpieza(numero);
            nuevo.fechas.add(fecha);
            return nuevo;
        }

        if (numero < nodo.numeroHabitacion) {
            nodo.izquierda = insertarRec(nodo.izquierda, numero, fecha);
        } else if (numero > nodo.numeroHabitacion) {
            nodo.derecha = insertarRec(nodo.derecha, numero, fecha);
        } else {
            boolean agregado = nodo.fechas.add(fecha);
            if (!agregado) {
                System.out.println("⚠️ Ya existe esa fecha para la habitación " + numero);
            }
        }

        return nodo;
    }

    // Cancelar una fecha de limpieza
    public void cancelarLimpieza(int numeroHabitacion, LocalDate fecha) {
        raiz = eliminarFechaRec(raiz, numeroHabitacion, fecha);
    }

    private NodoLimpieza eliminarFechaRec(NodoLimpieza nodo, int numero, LocalDate fecha) {
        if (nodo == null) {
            System.out.println(" No existe la habitación " + numero);
            return null;
        }

        if (numero < nodo.numeroHabitacion) {
            nodo.izquierda = eliminarFechaRec(nodo.izquierda, numero, fecha);
        } else if (numero > nodo.numeroHabitacion) {
            nodo.derecha = eliminarFechaRec(nodo.derecha, numero, fecha);
        } else {
            boolean eliminado = nodo.fechas.remove(fecha);
            if (!eliminado) {
                System.out.println(" No hay limpieza programada en esa fecha para habitación " + numero);
            }

            if (nodo.fechas.isEmpty()) {
                if (nodo.izquierda == null) {
                    return nodo.derecha;
                }
                if (nodo.derecha == null) {
                    return nodo.izquierda;
                }

                NodoLimpieza sucesor = encontrarMinimo(nodo.derecha);
                nodo.numeroHabitacion = sucesor.numeroHabitacion;
                nodo.fechas = sucesor.fechas;
                nodo.derecha = eliminarNodoPorNumero(nodo.derecha, sucesor.numeroHabitacion);
            }
        }

        return nodo;
    }

    // Eliminar nodo completo por número
    private NodoLimpieza eliminarNodoPorNumero(NodoLimpieza nodo, int numero) {
        if (nodo == null) {
            return null;
        }

        if (numero < nodo.numeroHabitacion) {
            nodo.izquierda = eliminarNodoPorNumero(nodo.izquierda, numero);
        } else if (numero > nodo.numeroHabitacion) {
            nodo.derecha = eliminarNodoPorNumero(nodo.derecha, numero);
        } else {
            if (nodo.izquierda == null) {
                return nodo.derecha;
            }
            if (nodo.derecha == null) {
                return nodo.izquierda;
            }

            NodoLimpieza sucesor = encontrarMinimo(nodo.derecha);
            nodo.numeroHabitacion = sucesor.numeroHabitacion;
            nodo.fechas = sucesor.fechas;
            nodo.derecha = eliminarNodoPorNumero(nodo.derecha, sucesor.numeroHabitacion);
        }

        return nodo;
    }

    // Buscar fechas de limpieza de una habitación
    public Set<LocalDate> obtenerLimpiezas(int numeroHabitacion) {
        NodoLimpieza nodo = buscarHabitacion(raiz, numeroHabitacion);
        return nodo != null ? nodo.fechas : Collections.emptySet();
    }

    private NodoLimpieza buscarHabitacion(NodoLimpieza nodo, int numero) {
        if (nodo == null) {
            return null;
        }
        if (numero < nodo.numeroHabitacion) {
            return buscarHabitacion(nodo.izquierda, numero);
        }
        if (numero > nodo.numeroHabitacion) {
            return buscarHabitacion(nodo.derecha, numero);
        }
        return nodo;
    }

    // Recorrido inorden 
    public void mostrarAgendaCompleta() {
        if (raiz == null) {
            System.out.println("🚫 No hay limpiezas programadas.");
        } else {
            System.out.println("🧼 Agenda de limpieza:");
            inorden(raiz);
        }
    }

    private void inorden(NodoLimpieza nodo) {
        if (nodo != null) {
            inorden(nodo.izquierda);
            System.out.println("Habitación " + nodo.numeroHabitacion + ":");
            for (LocalDate fecha : nodo.fechas) {
                System.out.println("  - " + fecha);
            }
            inorden(nodo.derecha);
        }
    }

    private NodoLimpieza encontrarMinimo(NodoLimpieza nodo) {
        while (nodo.izquierda != null) {
            nodo = nodo.izquierda;
        }
        return nodo;
    }
}
