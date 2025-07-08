package Estructuras;

import Modelo.AccionUsuario;

/**
 *
 * @author Wilian Lopez
 */
class NodoPila {

    AccionUsuario accion;
    NodoPila siguiente;

    NodoPila(AccionUsuario accion) {
        this.accion = accion;
        this.siguiente = null;
    }
}

public class PilaAccionesProducto {

    private NodoPila cima;
    private int tamaño;

    public PilaAccionesProducto() {
        cima = null;
        tamaño = 0;
    }

    public void push(AccionUsuario accion) {
        NodoPila nuevo = new NodoPila(accion);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamaño++;
    }

    public AccionUsuario pop() {
        if (estaVacia()) {
            throw new IllegalStateException("Pila vacía");
        }
        AccionUsuario accion = cima.accion;
        cima = cima.siguiente;
        tamaño--;
        return accion;
    }

    public AccionUsuario peek() {
        if (estaVacia()) {
            throw new IllegalStateException("Pila vacía");
        }
        return cima.accion;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamaño() {
        return tamaño;
    }

    public void mostrar() {
        NodoPila actual = cima;
        while (actual != null) {
            System.out.println(actual.accion);
            actual = actual.siguiente;
        }
    }
}
