package Estructuras;

import Modelo.Servicio;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
class NodoLista {

    Servicio servicio;
    NodoLista anterior;
    NodoLista siguiente;

    NodoLista(Servicio servicio) {
        this.servicio = servicio;
        this.anterior = null;
        this.siguiente = null;
    }
}

public class ListaServicios {

    private NodoLista cabeza;
    private NodoLista cola;
    private int tamaño;

    public ListaServicios() {
        cabeza = null;
        cola = null;
        tamaño = 0;
    }

    public void agregarAlFinal(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }
        NodoLista nuevo = new NodoLista(servicio);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        tamaño++;
    }

    public void agregarAlInicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }
        NodoLista nuevo = new NodoLista(servicio);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        tamaño++;
    }

    public boolean eliminarPorId(int idServicio) {
        NodoLista actual = cabeza;
        while (actual != null) {
            if (actual.servicio.getIdServicio() == idServicio) {
                if (actual.anterior != null) {
                    actual.anterior.siguiente = actual.siguiente;
                } else {
                    cabeza = actual.siguiente;
                }
                if (actual.siguiente != null) {
                    actual.siguiente.anterior = actual.anterior;
                } else {
                    cola = actual.anterior;
                }
                tamaño--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public List<Servicio> getServiciosDesdeInicio() {
        List<Servicio> lista = new ArrayList<>();
        NodoLista actual = cabeza;
        while (actual != null) {
            lista.add(actual.servicio);
            actual = actual.siguiente;
        }
        return lista;
    }

    public List<Servicio> getServiciosDesdeFin() {
        List<Servicio> lista = new ArrayList<>();
        NodoLista actual = cola;
        while (actual != null) {
            lista.add(actual.servicio);
            actual = actual.anterior;
        }
        return lista;
    }

    public void mostrarDesdeInicio() {
        NodoLista actual = cabeza;
        while (actual != null) {
            System.out.println(actual.servicio);
            actual = actual.siguiente;
        }
    }

    public void mostrarDesdeFin() {
        NodoLista actual = cola;
        while (actual != null) {
            System.out.println(actual.servicio);
            actual = actual.anterior;
        }
    }

    public void limpiar() {
        cabeza = null;
        cola = null;
        tamaño = 0;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }
}
