package CRUD;

import Modelo.Habitacion;
import Enums.EstadoLimpieza;
import Enums.PrioridadLimpieza;
import ConexionPostgreSQL.ConexionPostgreSQL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class HabitacionLimpiezaDAO {

    // Consultas SQL
    private static final String SQL_INSERT
            = "INSERT INTO HabitacionLimpieza(NumHabitacion, IdEstadoLimpieza, IdPrioridad, UltimaLimpieza, ProximaLimpieza, Notas) "
            + "VALUES (?, (SELECT IdEstadoLimpieza FROM EstadoLimpieza WHERE Nombre = ?), "
            + "(SELECT IdPrioridad FROM PrioridadLimpieza WHERE Nombre = ?), ?, ?, ?)";

    private static final String SQL_UPDATE
            = "UPDATE HabitacionLimpieza SET "
            + "IdEstadoLimpieza = (SELECT IdEstadoLimpieza FROM EstadoLimpieza WHERE Nombre = ?), "
            + "IdPrioridad = (SELECT IdPrioridad FROM PrioridadLimpieza WHERE Nombre = ?), "
            + "UltimaLimpieza = ?, ProximaLimpieza = ?, Notas = ? "
            + "WHERE NumHabitacion = ?";

    private static final String SQL_UPDATE_ESTADO
            = "UPDATE HabitacionLimpieza SET "
            + "IdEstadoLimpieza = (SELECT IdEstadoLimpieza FROM EstadoLimpieza WHERE Nombre = ?) "
            + "WHERE NumHabitacion = ?";

    private static final String SQL_UPDATE_PRIORIDAD
            = "UPDATE HabitacionLimpieza SET "
            + "IdPrioridad = (SELECT IdPrioridad FROM PrioridadLimpieza WHERE Nombre = ?) "
            + "WHERE NumHabitacion = ?";

    private static final String SQL_SELECT_BY_NUMERO
            = "SELECT hl.*, el.Nombre AS EstadoNombre, pl.Nombre AS PrioridadNombre, "
            + "th.Nombre AS TipoNombre, eh.Nombre AS EstadoGeneralNombre "
            + "FROM HabitacionLimpieza hl "
            + "JOIN Habitacion h ON hl.NumHabitacion = h.NumHabitacion "
            + "JOIN EstadoLimpieza el ON hl.IdEstadoLimpieza = el.IdEstadoLimpieza "
            + "LEFT JOIN PrioridadLimpieza pl ON hl.IdPrioridad = pl.IdPrioridad "
            + "JOIN TipoHabitacion th ON h.IdTipoHabitacion = th.IdTipoHabitacion "
            + "JOIN EstadoHabitacion eh ON h.IdEstado = eh.IdEstado "
            + "WHERE hl.NumHabitacion = ?";

    private static final String SQL_SELECT_PENDIENTES
            = "SELECT hl.*, el.Nombre AS EstadoNombre, pl.Nombre AS PrioridadNombre, "
            + "th.Nombre AS TipoNombre, eh.Nombre AS EstadoGeneralNombre "
            + "FROM HabitacionLimpieza hl "
            + "JOIN Habitacion h ON hl.NumHabitacion = h.NumHabitacion "
            + "JOIN EstadoLimpieza el ON hl.IdEstadoLimpieza = el.IdEstadoLimpieza "
            + "LEFT JOIN PrioridadLimpieza pl ON hl.IdPrioridad = pl.IdPrioridad "
            + "JOIN TipoHabitacion th ON h.IdTipoHabitacion = th.IdTipoHabitacion "
            + "JOIN EstadoHabitacion eh ON h.IdEstado = eh.IdEstado "
            + "WHERE el.Nombre LIKE 'PENDIENTE_%' "
            + "ORDER BY pl.Valor DESC, hl.ProximaLimpieza ASC";

    private static final String SQL_DELETE
            = "DELETE FROM HabitacionLimpieza WHERE NumHabitacion = ?";

    public Habitacion obtenerDatosLimpieza(int numHabitacion) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_NUMERO)) {

            stmt.setInt(1, numHabitacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearHabitacionConLimpieza(rs);
                }
            }
        }
        return null;
    }

    public List<Habitacion> obtenerPendientesLimpieza() throws SQLException {
        List<Habitacion> habitaciones = new ArrayList<>();

        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(SQL_SELECT_PENDIENTES)) {

            while (rs.next()) {
                habitaciones.add(mapearHabitacionConLimpieza(rs));
            }
        }
        return habitaciones;
    }

    public boolean actualizarEstadoLimpieza(int numHabitacion, EstadoLimpieza estado) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_ESTADO)) {

            stmt.setString(1, estado.name());
            stmt.setInt(2, numHabitacion);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarPrioridadLimpieza(int numHabitacion, PrioridadLimpieza prioridad) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PRIORIDAD)) {

            stmt.setString(1, prioridad.name());
            stmt.setInt(2, numHabitacion);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean marcarComoLimpia(int numHabitacion, String notas, PrioridadLimpieza prioridad) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Actualiza los datos en HabitacionLimpieza
                try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
                    stmt.setString(1, EstadoLimpieza.LIMPIA.name()); // EstadoLimpieza
                    stmt.setString(2, prioridad != null ? prioridad.name() : null); // PrioridadLimpieza como String o null
                    stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // UltimaLimpieza = ahora
                    stmt.setNull(4, Types.TIMESTAMP); // ProximaLimpieza = null
                    stmt.setString(5, notas); // Notas
                    stmt.setInt(6, numHabitacion); // WHERE NumHabitacion = ?
                    stmt.executeUpdate();
                }

                // 2. Actualiza el estado general de la habitación
                try (PreparedStatement stmt2 = conn.prepareStatement(
                        "UPDATE Habitacion SET idestado = (SELECT idestado FROM EstadoHabitacion WHERE nombre = 'LIBRE') WHERE numhabitacion = ?")) {
                    stmt2.setInt(1, numHabitacion);
                    stmt2.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean marcarComoLimpiaSinPrioridad(int numHabitacion, String notas) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Actualiza los datos en HabitacionLimpieza sin tocar la prioridad
                String sql = """
                UPDATE HabitacionLimpieza SET 
                    IdEstadoLimpieza = (SELECT IdEstadoLimpieza FROM EstadoLimpieza WHERE Nombre = ?),
                    UltimaLimpieza = ?, 
                    ProximaLimpieza = ?, 
                    Notas = ?
                WHERE NumHabitacion = ?""";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, EstadoLimpieza.LIMPIA.name());
                    stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setNull(3, Types.TIMESTAMP); // ProximaLimpieza = null
                    stmt.setString(4, notas);
                    stmt.setInt(5, numHabitacion);
                    stmt.executeUpdate();
                }

                // 2. Actualiza el estado general de la habitación
                try (PreparedStatement stmt2 = conn.prepareStatement(
                        "UPDATE Habitacion SET idestado = (SELECT idestado FROM EstadoHabitacion WHERE nombre = 'LIBRE') WHERE numhabitacion = ?")) {
                    stmt2.setInt(1, numHabitacion);
                    stmt2.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean solicitarLimpieza(int numHabitacion, PrioridadLimpieza prioridad,
            String notas, LocalDateTime proximaLimpieza) throws SQLException {

        Habitacion habLimpieza = obtenerDatosLimpieza(numHabitacion);

        if (habLimpieza == null) {
            try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

                stmt.setInt(1, numHabitacion);
                stmt.setString(2, "PENDIENTE_" + prioridad.name());
                stmt.setString(3, prioridad.name());
                stmt.setNull(4, Types.TIMESTAMP);
                stmt.setTimestamp(5, proximaLimpieza != null
                        ? Timestamp.valueOf(proximaLimpieza) : null);
                stmt.setString(6, notas);

                return stmt.executeUpdate() > 0;
            }
        } else {
            try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

                stmt.setString(1, "PENDIENTE_" + prioridad.name());
                stmt.setString(2, prioridad.name());
                stmt.setTimestamp(3, habLimpieza.getUltimaLimpieza() != null
                        ? Timestamp.valueOf(habLimpieza.getUltimaLimpieza()) : null);
                stmt.setTimestamp(4, proximaLimpieza != null
                        ? Timestamp.valueOf(proximaLimpieza) : null);
                stmt.setString(5, notas);
                stmt.setInt(6, numHabitacion);

                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean eliminarDatosLimpieza(int numHabitacion) throws SQLException {
        try (Connection conn = ConexionPostgreSQL.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, numHabitacion);
            return stmt.executeUpdate() > 0;
        }
    }

    private Habitacion mapearHabitacionConLimpieza(ResultSet rs) throws SQLException {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(rs.getInt("NumHabitacion"));

        // Estado y prioridad de limpieza
        habitacion.setEstadoLimpieza(EstadoLimpieza.valueOf(rs.getString("EstadoNombre")));

        String prioridadNombre = rs.getString("PrioridadNombre");
        if (prioridadNombre != null) {
            habitacion.setPrioridadLimpieza(PrioridadLimpieza.valueOf(prioridadNombre));
        }

        // Fechas
        Timestamp ultimaLimpieza = rs.getTimestamp("UltimaLimpieza");
        if (ultimaLimpieza != null) {
            habitacion.setUltimaLimpieza(ultimaLimpieza.toLocalDateTime());
        }

        Timestamp proximaLimpieza = rs.getTimestamp("ProximaLimpieza");
        if (proximaLimpieza != null) {
            habitacion.setProximaLimpieza(proximaLimpieza.toLocalDateTime());
        }

        habitacion.setNotasLimpieza(rs.getString("Notas"));

        // Nuevo: tipo y estado general
        habitacion.setTipo(Enums.TipoHabitacion.valueOf(rs.getString("TipoNombre")));
        habitacion.setEstado(Enums.EstadoHabitacion.valueOf(rs.getString("EstadoGeneralNombre")));

        return habitacion;
    }

}
