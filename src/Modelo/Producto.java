package Modelo;

/**
 *
 * @author Wilian Lopez
 */
public class Producto {

    private int idProducto;
    private String nombre;
    private String tipo;
    private double precio;
    private String descripcion;
    private boolean activo;

    public Producto(int idProducto, String nombre, String tipo, double precio, String descripcion, boolean activo) {
        setIdProducto(idProducto);
        setNombre(nombre);
        setTipo(tipo);
        setPrecio(precio);
        setDescripcion(descripcion);
        setActivo(activo);
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("ID de producto inválido.");
        }
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo no puede estar vacío.");
        }
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo.");
        }
        this.precio = precio;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
