
package Enums;

/**
 *
 * @author Wilian Lopez
 */
public enum PrioridadLimpieza {
    ALTA(3), MEDIA(2), BAJA(1);
    
    private final int valor;
    
    private PrioridadLimpieza(int valor) {
        this.valor = valor;
    }
    
    public int getValor() {
        return valor;
    }
}