package Controladores;

import CRUD.UsuarioDAO;
import Modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Wilian Lopez
 */
public class IniciarSesionController implements Initializable {

    @FXML
    private TextField textNombre;
    @FXML
    private TextField textContraseñaVisible;
    @FXML
    private PasswordField PasswContraseña;
    @FXML
    private CheckBox CBoxMostrarContraseña;
    @FXML
    private Button btnIngresar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textContraseñaVisible.setVisible(false);
    }    

    @FXML
    private void mostrarContraseña(ActionEvent event) {
        if (CBoxMostrarContraseña.isSelected()) {
            textContraseñaVisible.setText(PasswContraseña.getText());
            textContraseñaVisible.setVisible(true);
            PasswContraseña.setVisible(false);
        } else{
            PasswContraseña.setText(textContraseñaVisible.getText());
            PasswContraseña.setVisible(true);
            textContraseñaVisible.setVisible(false);
        }
    }

    @FXML
    private void ingresar(ActionEvent event) {
        String nombre = textNombre.getText().trim();
        String contrasenia = PasswContraseña.isVisible()
                ? PasswContraseña.getText().trim()
                : textContraseñaVisible.getText().trim();

        if (nombre.isEmpty() || contrasenia.isEmpty()) {
            AlertaUtil.mostrarAdvertencia("Por favor, complete todos los campos.");
            return;
        }

        Usuario usuario = new UsuarioDAO().obtenerPorCredenciales(nombre, contrasenia);

        if (usuario != null) {
            AlertaUtil.mostrarExitoTemporal("Bienvenido, " + usuario.getNombre(), 2);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/Principal.fxml"));
                Parent root = loader.load();
                
                PrincipalController controller = loader.getController();
                controller.setUsuario(usuario);
           
                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                AlertaUtil.mostrarError("No se pudo cargar el menú principal.");
            }

        } else {
            AlertaUtil.mostrarError("Usuario o contraseña incorrectos.");
        }
    }
    
}
