package Estructuras;

import Modelo.Huesped;
import javafx.collections.ObservableList;

/**
 *
 * @author Wilian Lopez
 */
class NodoHuesped {

    Huesped huesped;
    NodoHuesped izquierda;
    NodoHuesped derecha;

    NodoHuesped(Huesped huesped) {
        this.huesped = huesped;
        this.izquierda = null;
        this.derecha = null;
    }
}

public class ABBHuespedes {

    private NodoHuesped raiz;

    public ABBHuespedes() {
        raiz = null;
    }

    // Insertar un huesped 
    public void insertar(Huesped h) {
        if (h == null) {
            throw new IllegalArgumentException("El huésped no puede ser nulo");
        }
        raiz = insertarRec(raiz, h);
    }

    private NodoHuesped insertarRec(NodoHuesped nodo, Huesped h) {
        if (nodo == null) {
            return new NodoHuesped(h);
        }

        int cmp = h.getDni().compareTo(nodo.huesped.getDni());
        if (cmp < 0) {
            nodo.izquierda = insertarRec(nodo.izquierda, h);
        } else if (cmp > 0) {
            nodo.derecha = insertarRec(nodo.derecha, h);
        } else {
            throw new IllegalArgumentException("Ya existe un huésped con ese DNI.");
        }

        return nodo;
    }

    // Buscar un huésped por dnii
    public Huesped buscar(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        NodoHuesped nodo = buscarRec(raiz, dni);
        return nodo != null ? nodo.huesped : null;
    }

    private NodoHuesped buscarRec(NodoHuesped nodo, String dni) {
        if (nodo == null) {
            return null;
        }

        int cmp = dni.compareTo(nodo.huesped.getDni());
        if (cmp < 0) {
            return buscarRec(nodo.izquierda, dni);
        }
        if (cmp > 0) {
            return buscarRec(nodo.derecha, dni);
        }
        return nodo;
    }

    // Eliminar un husped por dni 
    public void eliminar(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        raiz = eliminarRec(raiz, dni);
    }

    private NodoHuesped eliminarRec(NodoHuesped nodo, String dni) {
        if (nodo == null) {
            return null;
        }

        int cmp = dni.compareTo(nodo.huesped.getDni());
        if (cmp < 0) {
            nodo.izquierda = eliminarRec(nodo.izquierda, dni);
        } else if (cmp > 0) {
            nodo.derecha = eliminarRec(nodo.derecha, dni);
        } else {
            if (nodo.izquierda == null && nodo.derecha == null) {
                return null;
            }

            if (nodo.izquierda == null) {
                return nodo.derecha;
            }
            if (nodo.derecha == null) {
                return nodo.izquierda;
            }

            NodoHuesped sucesor = encontrarMinimo(nodo.derecha);
            nodo.huesped = sucesor.huesped;
            nodo.derecha = eliminarRec(nodo.derecha, sucesor.huesped.getDni());
        }

        return nodo;
    }

    private NodoHuesped encontrarMinimo(NodoHuesped nodo) {
        if (nodo == null) {
            return null;
        }
        while (nodo.izquierda != null) {
            nodo = nodo.izquierda;
        }
        return nodo;
    }

    public void vaciar() {
        raiz = null;
    }

    // Métodos de recorrido 
    public void inorden(ObservableList<Huesped> lista) {
        inordenRec(raiz, lista);
    }

    private void inordenRec(NodoHuesped nodo, ObservableList<Huesped> lista) {
        if (nodo != null) {
            inordenRec(nodo.izquierda, lista);
            lista.add(nodo.huesped);
            inordenRec(nodo.derecha, lista);
        }
    }

    public void preorden(ObservableList<Huesped> lista) {
        preordenRec(raiz, lista);
    }

    private void preordenRec(NodoHuesped nodo, ObservableList<Huesped> lista) {
        if (nodo != null) {
            lista.add(nodo.huesped);
            preordenRec(nodo.izquierda, lista);
            preordenRec(nodo.derecha, lista);
        }
    }

    public void postorden(ObservableList<Huesped> lista) {
        postordenRec(raiz, lista);
    }

    private void postordenRec(NodoHuesped nodo, ObservableList<Huesped> lista) {
        if (nodo != null) {
            postordenRec(nodo.izquierda, lista);
            postordenRec(nodo.derecha, lista);
            lista.add(nodo.huesped);
        }
    }
}
