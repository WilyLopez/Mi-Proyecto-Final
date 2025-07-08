
package Estructuras;

import Modelo.SolicitudAtencion;

/**
 *
 * @author Wilian Lopez
 */
class NodoUsuario {
        SolicitudAtencion solicitud;
        NodoUsuario siguiente;

        NodoUsuario(SolicitudAtencion solicitud) {
            this.solicitud = solicitud;
        }
    }
public class ColaPrioridad {
    private NodoUsuario frenteAlta, finAlta;
    private NodoUsuario frenteMedia, finMedia;
    private NodoUsuario frenteBaja, finBaja;

    private int tamaño;

    public ColaPrioridad() {
        frenteAlta = finAlta = null;
        frenteMedia = finMedia = null;
        frenteBaja = finBaja = null;
        tamaño = 0;
    }

    public void enqueue(SolicitudAtencion solicitud) {
        NodoUsuario nuevo = new NodoUsuario(solicitud);
        switch (solicitud.getPrioridad()) {
            case ALTA -> {
                if (frenteAlta == null) {
                    frenteAlta = finAlta = nuevo;
                } else {
                    finAlta.siguiente = nuevo;
                    finAlta = nuevo;
                }
            }
            case MEDIA -> {
                if (frenteMedia == null) {
                    frenteMedia = finMedia = nuevo;
                } else {
                    finMedia.siguiente = nuevo;
                    finMedia = nuevo;
                }
            }
            case BAJA -> {
                if (frenteBaja == null) {
                    frenteBaja = finBaja = nuevo;
                } else {
                    finBaja.siguiente = nuevo;
                    finBaja = nuevo;
                }
            }
        }
        tamaño++;
    }

    public SolicitudAtencion dequeue() {
        NodoUsuario nodo = null;

        if (frenteAlta != null) {
            nodo = frenteAlta;
            frenteAlta = frenteAlta.siguiente;
            if (frenteAlta == null) finAlta = null;
        } else if (frenteMedia != null) {
            nodo = frenteMedia;
            frenteMedia = frenteMedia.siguiente;
            if (frenteMedia == null) finMedia = null;
        } else if (frenteBaja != null) {
            nodo = frenteBaja;
            frenteBaja = frenteBaja.siguiente;
            if (frenteBaja == null) finBaja = null;
        }

        if (nodo != null) {
            tamaño--;
            return nodo.solicitud;
        }

        return null;
    }

    public SolicitudAtencion peek() {
        if (frenteAlta != null) return frenteAlta.solicitud;
        if (frenteMedia != null) return frenteMedia.solicitud;
        if (frenteBaja != null) return frenteBaja.solicitud;
        return null;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    public int getTamaño() {
        return tamaño;
    }

    public void mostrar() {
        System.out.println("Prioridad ALTA:");
        mostrarCola(frenteAlta);
        System.out.println("Prioridad MEDIA:");
        mostrarCola(frenteMedia);
        System.out.println("Prioridad BAJA:");
        mostrarCola(frenteBaja);
    }

    private void mostrarCola(NodoUsuario frente) {
        NodoUsuario actual = frente;
        while (actual != null) {
            System.out.println(actual.solicitud);
            actual = actual.siguiente;
        }
    }

}
