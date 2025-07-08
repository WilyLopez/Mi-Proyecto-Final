
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
    private Connection conn;

    public ReservaDAO() throws SQLException {
        conn = ConexionPostgreSQL.getInstance().getConnection();
    }

    public boolean insertar(Reserva reserva) {
        String sql = "INSERT INTO Reserva (DniHuesped, NumHabitacion, FechaEntrada, FechaSalida) VALUES (?, ?, ?, ?) RETURNING IdReserva";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reserva.getDniHuesped());
            stmt.setInt(2, reserva.getNumeroHabitacion());
            stmt.setTimestamp(3, Timestamp.valueOf(reserva.getFechaHoraEntrada()));
            stmt.setTimestamp(4, Timestamp.valueOf(reserva.getFechaHoraSalida()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                reserva.setIdReserva(rs.getInt("IdReserva")); // Guarda el ID generado
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
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reserva r = new Reserva(
                    rs.getInt("IdReserva"),
                    rs.getString("DniHuesped"),
                    rs.getInt("NumHabitacion"),
                    rs.getTimestamp("FechaEntrada").toLocalDateTime(),
                    rs.getTimestamp("FechaSalida").toLocalDateTime()
                );
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Reserva buscarPorId(int id) {
        String sql = "SELECT * FROM Reserva WHERE IdReserva = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reserva(
                    rs.getInt("IdReserva"),
                    rs.getString("DniHuesped"),
                    rs.getInt("NumHabitacion"),
                    rs.getTimestamp("FechaEntrada").toLocalDateTime(),
                    rs.getTimestamp("FechaSalida").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
