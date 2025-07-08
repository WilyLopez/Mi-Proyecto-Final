
package CRUD;

import ConexionPostgreSQL.ConexionPostgreSQL;
import Modelo.Huesped;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class HuespedDAO {
    private Connection conn;

    public HuespedDAO() throws SQLException {
        conn = ConexionPostgreSQL.getInstance().getConnection();
    }

    public boolean insertar(Huesped h) {
        String sql = "INSERT INTO Huesped (Dni, NombreCompleto, Telefono, Email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, h.getDni());
            stmt.setString(2, h.getNombreCompleto());
            stmt.setString(3, h.getTelefono());
            stmt.setString(4, h.getCorreo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Huesped buscarPorDni(String dni) {
        String sql = "SELECT * FROM Huesped WHERE Dni = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Huesped(
                    rs.getString("Dni"),
                    rs.getString("NombreCompleto"),
                    rs.getString("Telefono"),
                    rs.getString("Email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Huesped> listar() {
        List<Huesped> lista = new ArrayList<>();
        String sql = "SELECT * FROM Huesped";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Huesped h = new Huesped(
                    rs.getString("Dni"),
                    rs.getString("NombreCompleto"),
                    rs.getString("Telefono"),
                    rs.getString("Email")
                );
                lista.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizar(Huesped h) {
        String sql = "UPDATE Huesped SET NombreCompleto = ?, Telefono = ?, Email = ? WHERE Dni = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, h.getNombreCompleto());
            stmt.setString(2, h.getTelefono());
            stmt.setString(3, h.getCorreo());
            stmt.setString(4, h.getDni());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(String dni) {
        String sql = "DELETE FROM Huesped WHERE Dni = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
