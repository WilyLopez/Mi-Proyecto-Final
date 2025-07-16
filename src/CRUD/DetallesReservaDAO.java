package CRUD;

import Modelo.DetalleReserva;
import ConexionPostgreSQL.ConexionPostgreSQL;
import Enums.TipoItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla DetalleReservaItem. Permite insertar, listar y eliminar los
 * ítems asociados a una reserva.
 */
public class DetallesReservaDAO {

    private Connection conn;

    public DetallesReservaDAO() throws SQLException {
        this.conn = ConexionPostgreSQL.getInstance().getConnection();
    }

    public boolean insertar(DetalleReserva detalle) {
        String sql = "INSERT INTO DetalleReservaItem (IdReserva, IdTipoItem, IdItem, Cantidad, PrecioUnitario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getIdReserva());
            stmt.setInt(2, detalle.getTipoItem().ordinal() + 1); // ordinal + 1 porque en BD empieza desde 1
            stmt.setInt(3, detalle.getIdItem());
            stmt.setInt(4, detalle.getCantidad());
            stmt.setDouble(5, detalle.getPrecioUnitario());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DetalleReserva> listarPorIdReserva(int idReserva) {
        List<DetalleReserva> lista = new ArrayList<>();
        String sql = """
                     SELECT 
                         dri.IdDetalle,
                         dri.IdReserva,
                         dri.IdTipoItem,
                         dri.IdItem,
                         dri.Cantidad,
                         dri.PrecioUnitario,
                         COALESCE(p.Nombre, s.Nombre) AS NombreItem
                     FROM DetalleReservaItem dri
                     LEFT JOIN Producto p ON dri.IdTipoItem = 1 AND p.IdProducto = dri.IdItem
                     LEFT JOIN Servicio s ON dri.IdTipoItem = 2 AND s.IdServicio = dri.IdItem
                     WHERE dri.IdReserva = ?
                     """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DetalleReserva detalle = new DetalleReserva(
                        rs.getInt("IdDetalle"),
                        rs.getInt("IdReserva"),
                        TipoItem.values()[rs.getInt("IdTipoItem") - 1],
                        rs.getInt("IdItem"),
                        rs.getString("NombreItem"),
                        rs.getInt("Cantidad"),
                        rs.getDouble("PrecioUnitario")
                );
                lista.add(detalle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminarPorIdDetalle(int idDetalle) {
        String sql = "DELETE FROM DetalleReservaItem WHERE IdDetalle = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDetalle);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarPorReserva(int idReserva) {
        String sql = "DELETE FROM DetalleReservaItem WHERE IdReserva = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
