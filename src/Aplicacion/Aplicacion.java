
package Aplicacion;

import ConexionPostgreSQL.ConexionPostgreSQL;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author Wilian Lopez
 */
public class Aplicacion extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/Vistas/IniciarSesion.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestor hoteleria");
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            System.out.println("Conectando... ");
            ConexionPostgreSQL conexion = ConexionPostgreSQL.getInstance();
            System.out.println("Conexion exitosa...");
            launch(args);
        } catch (SQLException e) {
            System.err.println("Error en la conexion. " + e.getMessage());
        }
    }

}
