package Enums;

/**
 *
 * @author Wilian Lopez
 */
public enum TipoItem {
    PRODUCTO,
    SERVICIO;

    public static TipoItem fromString(String nombre) {
        return TipoItem.valueOf(nombre.toUpperCase());
    }
}
