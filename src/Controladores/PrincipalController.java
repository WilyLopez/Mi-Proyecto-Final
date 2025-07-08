package Controladores;

import Modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class PrincipalController implements Initializable {

    @FXML
    private VBox menuLateral;
    @FXML
    private Label labelInicio;
    @FXML
    private Label labelReservas;
    @FXML
    private Label labelHabitaciones;
    @FXML
    private Label labelHuespedes;
    @FXML
    private Label labelServicios;
    @FXML
    private Label labelCalendario;
    @FXML
    private AnchorPane anchorPanePrincipal;
    @FXML
    private TextField textUsuarioLogueado;

    private Usuario UsuarioLogueado;
    @FXML
    private Label labelProductos;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarVistaEnAnchorPane("/Vistas/inicio.fxml");
    }

    public void setUsuario(Usuario usuario) {
        this.UsuarioLogueado = usuario;
        textUsuarioLogueado.setText(usuario.getNombre());
    }

    @FXML
    private void mostrarInicio(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/inicio.fxml");
    }

    @FXML
    private void mostrarReservas(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Reservas.fxml");
    }

    @FXML
    private void mostrarHabitaciones(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Habitaciones.fxml");
    }

    @FXML
    private void mostrarHuespedes(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Huespedes.fxml");
    }

    @FXML
    private void mostrarservicios(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Servicios.fxml");
    }
    
     @FXML
    private void mostrarProductos(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Productos.fxml");
    }


    @FXML
    private void mostrarCalendario(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Calendario.fxml");
    }

    private void cargarVistaEnAnchorPane(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            
            Object controller = loader.getController();

            if (controller instanceof HabitacionesController) {
                HabitacionesController habitacionesController = (HabitacionesController) controller;
                habitacionesController.setUsuario(UsuarioLogueado);
            } else if (controller instanceof  HuespedesController) {
                HuespedesController huespedesController = (HuespedesController) controller;
                huespedesController.setUsuario(UsuarioLogueado);
            } else if (controller instanceof  ServiciosController) {
                ServiciosController serviciosController = (ServiciosController) controller;
                serviciosController.setUsuario(UsuarioLogueado);
            } else if (controller instanceof  ProductosController) {
                ProductosController productosController = (ProductosController) controller;
                productosController.setUsuario(UsuarioLogueado);
            }

            anchorPanePrincipal.getChildren().clear();
            anchorPanePrincipal.getChildren().add(vista);

            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("No se pudo cargar la vista: " + rutaFXML);
        }
    }

   
}
