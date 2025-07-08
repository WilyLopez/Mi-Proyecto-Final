package CRUD;

import Modelo.Usuario;
import ConexionPostgreSQL.ConexionPostgreSQL;
import Enums.RolUsuario;

import java.sql.*;

/**
 *
 * @author Wilian Lopez
 */
public class UsuarioDAO {

    public Usuario obtenerPorCredenciales(String nombre, String contrasenia) {
        Usuario usuario = null;

        String sql = """
            SELECT u.IdUsuario, u.Nombre, u.Correo, u.Contrasenia, u.IdRolUsuario, r.Nombre AS RolNombre
            FROM Usuario u
            JOIN RolUsuario r ON u.IdRolUsuario = r.IdRolUsuario
            WHERE u.Nombre = ? AND u.Contrasenia = ?
        """;

        try (
                Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, contrasenia);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombreRol = rs.getString("RolNombre")
                        .toUpperCase()
                        .replace(" ", "_");

                RolUsuario rol = RolUsuario.valueOf(nombreRol);

                usuario = new Usuario(
                        rs.getInt("IdUsuario"),
                        rs.getString("Nombre"),
                        rol,
                        rs.getString("Correo"),
                        rs.getString("Contrasenia")
                );
            }

        } catch (SQLException e) {
            System.err.println("No se puedo obtener la  credenciales" );
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Rol no válido en la BD: " + e.getMessage());
        }

        return usuario;
    }

}
