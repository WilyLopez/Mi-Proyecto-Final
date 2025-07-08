package Estructuras;

import Modelo.Habitacion;
import java.util.Arrays;

/**
 *
 * @author Wilian Lopez
 */
public class ArregloHabitaciones {

    private Habitacion[] habitaciones;
    private int contador;

    public ArregloHabitaciones(int capacidadInicial) {
        habitaciones = new Habitacion[capacidadInicial];
        contador = 0;
    }

    public void insertar(Habitacion h) {
        if (buscar(h.getNumero()) != null) {
            throw new IllegalArgumentException("Ya existe una habitación con ese número.");
        }

        if (contador == habitaciones.length) {
            expandir();
        }

        habitaciones[contador++] = h;
    }

    // Eliminación lógica
    public boolean eliminar(int numero) {
        Habitacion h = buscar(numero);
        
        return false;
    }

    private void expandir() {
        habitaciones = Arrays.copyOf(habitaciones, habitaciones.length * 2);
    }

    // Buscar habitación por número - busqueda binaria
    public Habitacion buscar(int numero) {
        ordenarPorNumero();
        int izquierda = 0, derecha = contador - 1;
        while (izquierda <= derecha) {
            int medio = (izquierda + derecha) / 2;
            Habitacion actual = habitaciones[medio];
            if (actual.getNumero() == numero) {
                return null;
            } else if (numero < actual.getNumero()) {
                derecha = medio - 1;
            } else {
                izquierda = medio + 1;
            }
        }
        return null;
    }

    // Ordenar habitaciones de forma ascendente
    public void ordenarPorNumero() {
        Arrays.sort(habitaciones, 0, contador, (a, b) -> Integer.compare(a.getNumero(), b.getNumero()));
    }

    //ordenar habitaciones por tarifa
    public void ordenarPorTarifa() {
        Arrays.sort(habitaciones, 0, contador, (a, b) -> Double.compare(a.getPrecio(), b.getPrecio()));
    }

    //mostrar habitaciones activas
   

}
