package CRUD;

import ConexionPostgreSQL.ConexionPostgreSQL;
import Modelo.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class ServicioDAO {

    private Connection conn;

    public ServicioDAO() throws SQLException {
        conn = ConexionPostgreSQL.getInstance().getConnection();
    }

    public boolean insertar(Servicio s) {
        String sql = "INSERT INTO Servicio (Nombre, Descripcion, Precio) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNombre());
            stmt.setString(2, s.getDescripcion());
            stmt.setDouble(3, s.getCosto());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Servicio> listar() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM Servicio";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Servicio s = new Servicio(
                        rs.getInt("IdServicio"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getDouble("Precio")
                );
                lista.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Servicio buscarPorId(int id) {
        String sql = "SELECT * FROM Servicio WHERE IdServicio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Servicio(
                        rs.getInt("IdServicio"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getDouble("Precio")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizar(Servicio s) {
        String sql = "UPDATE Servicio SET Nombre = ?, Descripcion = ?, Precio = ? WHERE IdServicio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNombre());
            stmt.setString(2, s.getDescripcion());
            stmt.setDouble(3, s.getCosto());
            stmt.setInt(4, s.getIdServicio());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Servicio WHERE IdServicio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
