
package ConexionPostgreSQL;
import java.sql.*;

/**
 *
 * @author Dayanna Calderon
 */
public class ConexionPostgreSQL {
    private static ConexionPostgreSQL instance;
    private Connection connection;
    
    private String url = "jdbc:postgresql://localhost:5432/BDHoteleria";
    private String usuario = "postgres";
    private String contrasenia = "123456";
    
    private ConexionPostgreSQL() throws SQLException {
        conectar();
    }
    
    private void conectar() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, usuario, contrasenia);
        } catch (ClassNotFoundException ex) {
            System.out.println("No se pudo cargar el driver: " + ex.getMessage());
            throw new SQLException("Error al cargar el driver", ex);
        } catch (SQLException ex) {
            System.out.println("Error al conectar a la base de datos: " + ex.getMessage());
            throw ex;
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            conectar(); 
        }
        return connection;
    }
    
    public static ConexionPostgreSQL getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConexionPostgreSQL();
        } else if (instance.getConnection().isClosed()) {
            instance = new ConexionPostgreSQL(); 
        }
        return instance;
    }
    
}
