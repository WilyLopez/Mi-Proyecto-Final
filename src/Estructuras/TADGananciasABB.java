package Estructuras;

import Modelo.Ganancia;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dayanna Calderon
 */

class Nodo {
    Ganancia ganancia;
    Nodo izquierda, derecha;

    Nodo(Ganancia g) {
        this.ganancia = g;
    }
}

public class TADGananciasABB {

    private Nodo raiz;

    public void insertar(Ganancia g) {
        raiz = insertarRecursivo(raiz, g);
    }

    private Nodo insertarRecursivo(Nodo actual, Ganancia g) {
        if (actual == null) return new Nodo(g);

        int comparacion = g.compareTo(actual.ganancia);
        if (comparacion < 0) {
            actual.izquierda = insertarRecursivo(actual.izquierda, g);
        } else if (comparacion > 0) {
            actual.derecha = insertarRecursivo(actual.derecha, g);
        } else {
           
            actual.ganancia.setMontoTotal(actual.ganancia.getMontoTotal() + g.getMontoTotal());
        }
        return actual;
    }

    public double obtenerGananciaPorDia(LocalDate fecha) {
        return buscarGananciaPorFecha(raiz, fecha);
    }

    private double buscarGananciaPorFecha(Nodo nodo, LocalDate fecha) {
        if (nodo == null) return 0;

        if (fecha.isEqual(nodo.ganancia.getFecha())) {
            return nodo.ganancia.getMontoTotal();
        } else if (fecha.isBefore(nodo.ganancia.getFecha())) {
            return buscarGananciaPorFecha(nodo.izquierda, fecha);
        } else {
            return buscarGananciaPorFecha(nodo.derecha, fecha);
        }
    }

    public double obtenerGananciaPorSemana(LocalDate inicioSemana) {
        double total = 0;
        for (int i = 0; i < 7; i++) {
            total += obtenerGananciaPorDia(inicioSemana.plusDays(i));
        }
        return total;
    }

    public double obtenerGananciaPorMes(YearMonth mes) {
        return obtenerGananciaMesRec(raiz, mes);
    }

    private double obtenerGananciaMesRec(Nodo nodo, YearMonth mes) {
        if (nodo == null) return 0;

        double total = 0;
        YearMonth fechaNodo = YearMonth.from(nodo.ganancia.getFecha());

        if (fechaNodo.equals(mes)) {
            total += nodo.ganancia.getMontoTotal();
        }

        total += obtenerGananciaMesRec(nodo.izquierda, mes);
        total += obtenerGananciaMesRec(nodo.derecha, mes);
        return total;
    }

    // Obtener ganancia en un rango de fechas
    public double obtenerGananciaEntreFechas(LocalDate inicio, LocalDate fin) {
        return obtenerGananciaEntreFechasRec(raiz, inicio, fin);
    }

    private double obtenerGananciaEntreFechasRec(Nodo nodo, LocalDate inicio, LocalDate fin) {
        if (nodo == null) return 0;

        double total = 0;
        LocalDate fechaNodo = nodo.ganancia.getFecha();

        if (!fechaNodo.isBefore(inicio) && !fechaNodo.isAfter(fin)) {
            total += nodo.ganancia.getMontoTotal();
        }

        total += obtenerGananciaEntreFechasRec(nodo.izquierda, inicio, fin);
        total += obtenerGananciaEntreFechasRec(nodo.derecha, inicio, fin);

        return total;
    }

    public void eliminarGananciaPorFecha(LocalDate fecha) {
        raiz = eliminarRec(raiz, fecha);
    }

    private Nodo eliminarRec(Nodo nodo, LocalDate fecha) {
        if (nodo == null) return null;

        if (fecha.isBefore(nodo.ganancia.getFecha())) {
            nodo.izquierda = eliminarRec(nodo.izquierda, fecha);
        } else if (fecha.isAfter(nodo.ganancia.getFecha())) {
            nodo.derecha = eliminarRec(nodo.derecha, fecha);
        } else {
            // Caso: nodo con un hijo o sin hijos
            if (nodo.izquierda == null) return nodo.derecha;
            else if (nodo.derecha == null) return nodo.izquierda;

            // Caso: dos hijos, buscar el mínimo del subárbol derecho
            Nodo sucesor = encontrarMinimo(nodo.derecha);
            nodo.ganancia = sucesor.ganancia;
            nodo.derecha = eliminarRec(nodo.derecha, sucesor.ganancia.getFecha());
        }

        return nodo;
    }

    private Nodo encontrarMinimo(Nodo nodo) {
        while (nodo.izquierda != null) {
            nodo = nodo.izquierda;
        }
        return nodo;
    }

    public List<Ganancia> obtenerListaOrdenada() {
        List<Ganancia> lista = new ArrayList<>();
        llenarListaInOrden(raiz, lista);
        return lista;
    }

    private void llenarListaInOrden(Nodo nodo, List<Ganancia> lista) {
        if (nodo != null) {
            llenarListaInOrden(nodo.izquierda, lista);
            lista.add(nodo.ganancia);
            llenarListaInOrden(nodo.derecha, lista);
        }
    }

    public void inOrden() {
        inOrdenRec(raiz);
    }

    private void inOrdenRec(Nodo nodo) {
        if (nodo != null) {
            inOrdenRec(nodo.izquierda);
            System.out.println(nodo.ganancia);
            inOrdenRec(nodo.derecha);
        }
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}
