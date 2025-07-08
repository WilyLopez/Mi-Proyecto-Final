package Estructuras;

import Modelo.Producto;
import java.util.Arrays;

/**
 *
 * @author Wilian Lopez
 */
public class ArregloProductos {

    private Producto[] productos;
    private int tamaño;
    private static final int CAPACIDAD_INICIAL = 100;

    public ArregloProductos() {
        productos = new Producto[CAPACIDAD_INICIAL];
        tamaño = 0;
    }

    // Insertar un producto (al final)
    public void insertar(Producto producto) {
        if (tamaño >= productos.length) {
            productos = Arrays.copyOf(productos, productos.length * 2);
        }
        productos[tamaño++] = producto;
    }

    // Eliminación lógica (cambia un campo `activo` a false si se implementa)
    public boolean eliminarPorId(int id) {
        for (int i = 0; i < tamaño; i++) {
            if (productos[i] != null && productos[i].getIdProducto() == id) {
                productos[i].setActivo(false); // eliminación lógica
                return true;
            }
        }
        return false;
    }

    // Buscar por ID usando búsqueda binaria (requiere ordenar primero)
    public Producto buscarPorId(int id) {
        int izquierda = 0, derecha = tamaño - 1;
        while (izquierda <= derecha) {
            int medio = (izquierda + derecha) / 2;
            if (productos[medio] == null) {
                derecha--;
                continue;
            }
            if (productos[medio].getIdProducto() == id) {
                return productos[medio];
            } else if (productos[medio].getIdProducto() < id) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return null;
    }

    // Mostrar productos activos (no nulos)
    public void mostrar() {
        for (int i = 0; i < tamaño; i++) {
            if (productos[i] != null) {
                System.out.println(productos[i].getNombre() + " - " + productos[i].getTipo());
            }
        }
    }

    public Producto[] getProductos() {
        return productos;
    }

    public int getTamaño() {
        return tamaño;
    }

}
