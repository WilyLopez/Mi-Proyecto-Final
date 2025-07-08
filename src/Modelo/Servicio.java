
package Modelo;

/**
 *
 * @author Wilian Lopez
 */
public class Servicio {
    private int idServicio;
    private String nombre;
    private String descripcion;
    private double costo;

    public Servicio(int idServicio, String nombre, String descripcion, double costo) {
        setIdServicio(idServicio);
        setNombre(nombre);
        setDescripcion(descripcion);
        setCosto(costo);
    }    

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        if (idServicio < 0) {
            throw new IllegalArgumentException("ID del servicio inválido.");
        }
        this.idServicio = idServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("Descripción no puede estar vacía.");
        }
        this.descripcion = descripcion;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        if (costo < 0) {
            throw new IllegalArgumentException("Costo no puede ser negativo.");
        }
        this.costo = costo;
    }
    
    

}
