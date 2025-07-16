package CRUD;

import Modelo.Habitacion;
import Enums.EstadoHabitacion;
import Enums.TipoHabitacion;
import ConexionPostgreSQL.ConexionPostgreSQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class HabitacionesDAO {

    // Consultas SQL
    private static final String SQL_INSERT
            = "INSERT INTO habitacion(numhabitacion, idtipohabitacion, precio, detalles, idestado) "
            + "VALUES (?, (SELECT idtipohabitacion FROM tipohabitacion WHERE nombre = ?), ?, ?, "
            + "(SELECT idestado FROM estadohabitacion WHERE nombre = ?))";

    private static final String SQL_UPDATE
            = "UPDATE habitacion SET "
            + "idtipohabitacion = (SELECT idtipohabitacion FROM tipohabitacion WHERE nombre = ?), "
            + "precio = ?, detalles = ?, "
            + "idestado = (SELECT idestado FROM estadohabitacion WHERE nombre = ?) "
            + "WHERE numhabitacion = ?";

    private static final String SQL_DELETE
            = "UPDATE habitacion SET idestado = (SELECT idestado FROM estadohabitacion WHERE nombre = 'MANTENIMIENTO') "
            + "WHERE numhabitacion = ?";

    private static final String SQL_LISTAR_TODAS
            = "SELECT h.numhabitacion, t.nombre AS tipo, h.precio, h.detalles, e.nombre AS estado "
            + "FROM habitacion h "
            + "JOIN tipohabitacion t ON h.idtipohabitacion = t.idtipohabitacion "
            + "JOIN estadohabitacion e ON h.idestado = e.idestado "
            + "ORDER BY h.numhabitacion";

    private static final String SQL_FILTRAR_TIPO
            = "SELECT h.numhabitacion, t.nombre AS tipo, h.precio, h.detalles, e.nombre AS estado "
            + "FROM habitacion h "
            + "JOIN tipohabitacion t ON h.idtipohabitacion = t.idtipohabitacion "
            + "JOIN estadohabitacion e ON h.idestado = e.idestado "
            + "WHERE t.nombre = ? "
            + "ORDER BY h.numhabitacion";

    public List<Habitacion> listarHabitaciones() throws SQLException {
        List<Habitacion> habitaciones = new ArrayList<>();

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(SQL_LISTAR_TODAS)) {

            while (rs.next()) {
                habitaciones.add(mapearHabitacion(rs));
            }
        }
        return habitaciones;
    }

    public List<Habitacion> filtrarPorTipo(TipoHabitacion tipo) throws SQLException {
        List<Habitacion> habitaciones = new ArrayList<>();

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_FILTRAR_TIPO)) {

            stmt.setString(1, tipo.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    habitaciones.add(mapearHabitacion(rs));
                }
            }
        }
        return habitaciones;
    }

    public boolean eliminarHabitacion(int numeroHabitacion) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, numeroHabitacion);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarHabitacion(Habitacion habitacion) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, habitacion.getTipo().name());
            stmt.setDouble(2, habitacion.getPrecio());
            stmt.setString(3, habitacion.getDetalle());
            stmt.setString(4, habitacion.getEstado().name());
            stmt.setInt(5, habitacion.getNumero());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean insertarHabitacion(Habitacion habitacion) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setInt(1, habitacion.getNumero());
            stmt.setString(2, habitacion.getTipo().name());
            stmt.setDouble(3, habitacion.getPrecio());
            stmt.setString(4, habitacion.getDetalle());
            stmt.setString(5, habitacion.getEstado().name());

            return stmt.executeUpdate() > 0;
        }
    }

    private Habitacion mapearHabitacion(ResultSet rs) throws SQLException {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(rs.getInt("numhabitacion"));
        habitacion.setTipo(TipoHabitacion.valueOf(rs.getString("tipo")));
        habitacion.setPrecio(rs.getDouble("precio"));
        habitacion.setDetalle(rs.getString("detalles"));
        habitacion.setEstado(EstadoHabitacion.valueOf(rs.getString("estado")));
        return habitacion;
    }

    public Habitacion buscarPorNumero(int numero) throws SQLException {
        String sql = "SELECT h.numhabitacion, t.nombre AS tipo, h.precio, h.detalles, e.nombre AS estado "
                + "FROM habitacion h "
                + "JOIN tipohabitacion t ON h.idtipohabitacion = t.idtipohabitacion "
                + "JOIN estadohabitacion e ON h.idestado = e.idestado "
                + "WHERE h.numhabitacion = ?";
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearHabitacion(rs);
            }
        }
        return null;
    }
    
    

    public List<Habitacion> listarHabitacionesEnLimpieza() throws SQLException {
        String sql = """
                     SELECT h.numhabitacion, t.nombre AS tipo, h.precio, h.detalles, e.nombre AS estado 
                                      FROM habitacion h 
                                      JOIN tipohabitacion t ON h.idtipohabitacion = t.idtipohabitacion 
                                      JOIN estadohabitacion e ON h.idestado = e.idestado 
                                      WHERE e.nombre = 'EN_LIMPIEZA' OR e.nombre ='MANTENIMIENTO';
                     """;

        List<Habitacion> habitaciones = new ArrayList<>();
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                habitaciones.add(mapearHabitacion(rs));
            }
        }
        return habitaciones;
    }

}
