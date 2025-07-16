package CRUD;

import Modelo.Reserva;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConexionPostgreSQL.ConexionPostgreSQL;

/**
 *
 * @author Wilian Lopez
 */
public class ReservaDAO {

    public boolean insertar(Reserva reserva) {
        String sql = "INSERT INTO Reserva (DniHuesped, NumHabitacion, FechaEntrada, FechaSalida, Total) VALUES (?, ?, ?, ?, ?) RETURNING IdReserva";

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reserva.getDniHuesped());
            stmt.setInt(2, reserva.getNumeroHabitacion());
            stmt.setTimestamp(3, Timestamp.valueOf(reserva.getFechaHoraEntrada()));
            stmt.setTimestamp(4, Timestamp.valueOf(reserva.getFechaHoraSalida()));
            stmt.setDouble(5, reserva.getTotal());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                reserva.setIdReserva(rs.getInt("IdReserva"));
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reserva> listarReservas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM Reserva";

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reserva r = new Reserva(
                        rs.getString("DniHuesped"),
                        rs.getInt("NumHabitacion"),
                        rs.getTimestamp("FechaEntrada").toLocalDateTime(),
                        rs.getTimestamp("FechaSalida").toLocalDateTime(),
                        rs.getDouble("Total")
                );
                r.setIdReserva(rs.getInt("IdReserva"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Reserva buscarPorId(int id) {
        String sql = "SELECT * FROM Reserva WHERE IdReserva = ?";

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Reserva r = new Reserva(
                        rs.getString("DniHuesped"),
                        rs.getInt("NumHabitacion"),
                        rs.getTimestamp("FechaEntrada").toLocalDateTime(),
                        rs.getTimestamp("FechaSalida").toLocalDateTime(),
                        rs.getDouble("Total")
                );
                r.setIdReserva(rs.getInt("IdReserva"));
                return r;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean eliminar(int idReserva) {
        String sql = "DELETE FROM Reserva WHERE IdReserva = ?";

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idReserva);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reserva> obtenerReservasSinGanancia() {
        List<Reserva> lista = new ArrayList<>();

        String sql = """
        SELECT r.*
        FROM Reserva r
        LEFT JOIN Ganancia g ON r.IdReserva = g.ReservaID
        WHERE g.ReservaID IS NULL
    """;

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reserva r = new Reserva(
                        rs.getString("DniHuesped"),
                        rs.getInt("NumHabitacion"),
                        rs.getTimestamp("FechaEntrada").toLocalDateTime(),
                        rs.getTimestamp("FechaSalida").toLocalDateTime(),
                        rs.getDouble("Total")
                );
                r.setIdReserva(rs.getInt("IdReserva"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

}
