package Util;

import Modelo.Producto;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class SeleccionProducto {

    private static List<Producto> productosSeleccionados;

    public static void setProductosSeleccionados(List<Producto> productos) {
        productosSeleccionados = productos;
    }

    public static List<Producto> getProductosSeleccionados() {
        return productosSeleccionados;
    }

    public static void limpiar() {
        productosSeleccionados = null;
    }

}
