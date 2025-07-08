package Modelo;

import Enums.TipoAccionUsuario;

/**
 *
 * @author Wilian Lopez
 */
public class AccionUsuario {

    private TipoAccionUsuario tipoAccion;
    private Producto productoNuevo;
    private Producto productoAnterior;

    public AccionUsuario(TipoAccionUsuario tipoAccion, Producto productoNuevo, Producto productoAnterior) {
        this.tipoAccion = tipoAccion;
        this.productoNuevo = productoNuevo;
        this.productoAnterior = productoAnterior;
    }

    public TipoAccionUsuario getTipoAccion() {
        return tipoAccion;
    }

    public Producto getProductoNuevo() {
        return productoNuevo;
    }

    public Producto getProductoAnterior() {
        return productoAnterior;
    }

    @Override
    public String toString() {
        return tipoAccion + ": " + productoNuevo.getNombre();
    }
}
