package CRUD;

import ConexionPostgreSQL.ConexionPostgreSQL;
import Modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class ProductoDAO {

    private Connection conexion;

    public ProductoDAO() throws SQLException {
        conexion = ConexionPostgreSQL.getInstance().getConnection();
    }

    public boolean insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO Producto (Nombre, Tipo, Precio, Descripcion, Activo) VALUES (?, ?, ?, ?, TRUE)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getTipo());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setString(4, producto.getDescripcion());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE Producto SET Nombre = ?, Tipo = ?, Precio = ?, Descripcion = ? WHERE IdProducto = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getTipo());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setString(4, producto.getDescripcion());
            stmt.setInt(5, producto.getIdProducto());
            return stmt.executeUpdate() > 0;
        }
    }

    // Eliminación lógica (no se borra el registro)
    public boolean eliminar(int idProducto) throws SQLException {
        String sql = "UPDATE Producto SET Activo = FALSE WHERE IdProducto = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            return stmt.executeUpdate() > 0;
        }
    }

    // Revertir eliminación lógica
    public boolean reactivar(int idProducto) throws SQLException {
        String sql = "UPDATE Producto SET Activo = TRUE WHERE IdProducto = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            return stmt.executeUpdate() > 0;
        }
    }

    public Producto buscarPorId(int idProducto) throws SQLException {
        String sql = "SELECT * FROM Producto WHERE IdProducto = ? AND Activo = TRUE";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearProducto(rs);
            }
        }
        return null;
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Activo = TRUE";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
        return productos;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("IdProducto"),
                rs.getString("Nombre"),
                rs.getString("Tipo"),
                rs.getDouble("Precio"),
                rs.getString("Descripcion"),
                rs.getBoolean("Activo")
        );
    }
}
