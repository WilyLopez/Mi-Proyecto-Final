
package Modelo;

import Enums.PrioridadAtencion;

/**
 *
 * @author Wilian Lopez
 */
public class SolicitudAtencion implements Comparable<SolicitudAtencion >{
    
    private String descripcion;
    private PrioridadAtencion prioridad;
    private long timestamp;

    public SolicitudAtencion(String descripcion, PrioridadAtencion prioridad) {
        setDescripcion(descripcion);
        setPrioridad(prioridad);
        this.timestamp = System.currentTimeMillis();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        this.descripcion = descripcion;
    }

    public PrioridadAtencion getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadAtencion prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula.");
        }
        this.prioridad = prioridad;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    

    @Override
    public int compareTo(SolicitudAtencion o) {
        int comp = Integer.compare(this.prioridad.ordinal(), o.prioridad.ordinal());
        if (comp != 0) return comp;
        return Long.compare(this.timestamp, o.timestamp);
    }

}
