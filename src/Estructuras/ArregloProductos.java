package Estructuras;

import Modelo.Producto;
import java.util.Arrays;
import java.util.Comparator;

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

    // Insertar un producto al final
    public void insertar(Producto producto) {
        if (tamaño >= productos.length) {
            productos = Arrays.copyOf(productos, productos.length * 2);
        }
        productos[tamaño++] = producto;
    }

    // Eliminación lógica
    public boolean eliminarPorId(int id) {
        for (int i = 0; i < tamaño; i++) {
            if (productos[i] != null && productos[i].getIdProducto() == id) {
                productos[i].setActivo(false); // eliminación lógica
                return true;
            }
        }
        return false;
    }

    // Buscar por ID usando busqueda binaria
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

    // Mostrar productos activos
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
    
    public void ordenarPorNombre() {
        Arrays.sort(productos, 0, tamaño, Comparator.comparing(Producto::getNombre));
    }
    
    public void ordenarPorPrecio() {
        Arrays.sort(productos, 0, tamaño, Comparator.comparingDouble(Producto::getPrecio));
    }
    
    public void ordenarPorPrecioDescendente() {
        Arrays.sort(productos, 0, tamaño, Comparator.comparingDouble(Producto::getPrecio).reversed());
    }
    
    
}
