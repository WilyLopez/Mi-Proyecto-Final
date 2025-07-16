package CRUD;

import ConexionPostgreSQL.ConexionPostgreSQL;
import Modelo.Ganancia;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dayanna Calderon
 */
public class GananciaDAO {

    private Connection conexion;

    public GananciaDAO() {
        try {
            this.conexion = ConexionPostgreSQL.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener la conexión en GananciaDAO: " + e.getMessage());
        }
    }

    // OBTENER TODAS
    public List<Ganancia> obtenerTodas() {
        List<Ganancia> lista = new ArrayList<>();
        String sql = "SELECT * FROM Ganancia ORDER BY Fecha ASC";
        try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ganancia g = new Ganancia(
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getDouble("MontoTotal"),
                        rs.getString("Detalle")
                );
                g.setId(rs.getInt("IdGanancia"));
                lista.add(g);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // BUSCAR POR ID
    public Ganancia buscarPorId(int idGanancia) {
        String sql = "SELECT * FROM Ganancia WHERE IdGanancia = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idGanancia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ganancia g = new Ganancia(
                            rs.getDate("Fecha").toLocalDate(),
                            rs.getDouble("MontoTotal"),
                            rs.getString("Detalle")
                    );
                    g.setId(idGanancia);
                    return g;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ACTUALIZAR
    public boolean actualizarGanancia(Ganancia g) {
        String sql = "UPDATE Ganancia SET Fecha = ?, MontoTotal = ?, Detalle = ? WHERE IdGanancia = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(g.getFecha()));
            stmt.setDouble(2, g.getMontoTotal());
            stmt.setString(3, g.getDetalle());
            stmt.setInt(4, g.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminarGanancia(int idGanancia) {
        String sql = "DELETE FROM Ganancia WHERE IdGanancia = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idGanancia);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // OBTENER POR FECHA
    public List<Ganancia> obtenerPorFecha(LocalDate fecha) {
        List<Ganancia> lista = new ArrayList<>();
        String sql = "SELECT * FROM Ganancia WHERE Fecha = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ganancia g = new Ganancia(
                            rs.getDate("Fecha").toLocalDate(),
                            rs.getDouble("MontoTotal"),
                            rs.getString("Detalle")
                    );
                    g.setId(rs.getInt("IdGanancia"));
                    lista.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Ganancia> obtenerPorMes(YearMonth mes) {
        List<Ganancia> lista = new ArrayList<>();
        LocalDate inicio = mes.atDay(1);
        LocalDate fin = mes.atEndOfMonth();
        String sql = "SELECT * FROM Ganancia WHERE Fecha BETWEEN ? AND ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fin));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ganancia g = new Ganancia(
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getDouble("MontoTotal"),
                        rs.getString("Detalle")
                );
                g.setId(rs.getInt("IdGanancia"));
                lista.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertarGanancia(Ganancia g) {
        String sql = "INSERT INTO Ganancia (ReservaID, Fecha, MontoTotal, Detalle) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, g.getReservaId());
            stmt.setDate(2, java.sql.Date.valueOf(g.getFecha()));
            stmt.setDouble(3, g.getMontoTotal());
            stmt.setString(4, g.getDetalle());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
