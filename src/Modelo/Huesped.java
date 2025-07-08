package Modelo;

/**
 *
 * @author Wilian Lopez
 */
public class Huesped {

    private String dni;
    private String nombreCompleto;
    private String telefono;
    private String correo;

    public Huesped() {
    }

    public Huesped(String dni, String nombreCompleto, String telefono, String correo) {
        setDni(dni);
        setNombreCompleto(nombreCompleto);
        setTelefono(telefono);
        setCorreo(correo);
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos numéricos.");
        }
        this.dni = dni;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || !telefono.matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe tener 9 dígitos.");
        }
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo == null || !correo.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Correo electrónico no válido.");
        }
        this.correo = correo;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (DNI: " + dni + ")";
    }

}
