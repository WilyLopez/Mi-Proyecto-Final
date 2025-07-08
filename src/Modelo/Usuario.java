package Modelo;

import Enums.RolUsuario;
import java.util.regex.Pattern;

/**
 *
 * @author Wilian Lopez
 */
public class Usuario {

    private int idUsuario;
    private String nombre;
    private RolUsuario rol;
    private String correo;
    private String contrasenia;

    public Usuario(String nombre, RolUsuario rol, String correo, String contrasenia) {
        this.nombre = nombre;
        this.rol = rol;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    public Usuario(int idUsuario, String nombre, RolUsuario rol, String correo, String contrasenia) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rol = rol;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    public Usuario() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        if (idUsuario < 0) {
            throw new IllegalArgumentException("El ID de usuario no puede ser negativo.");
        }
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo.");
        }
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo == null || !esCorreoValido(correo)) {
            throw new IllegalArgumentException("Correo electrónico inválido.");
        }
        this.correo = correo.trim();
    }

    private boolean esCorreoValido(String correo) {
        String patron = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(patron, correo);
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        if (contrasenia == null || contrasenia.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.contrasenia = contrasenia;
    }

}
