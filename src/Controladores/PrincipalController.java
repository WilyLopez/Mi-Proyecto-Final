package Controladores;

import Util.AlertaUtil;
import Modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    @FXML
    private Label labelProductos;
    @FXML
    private TextField textTipoUsuario;
    @FXML
    private Label labelCerrarSecion;
    @FXML
    private Label labelLimpieza;
    private Usuario UsuarioLogueado;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarVistaEnAnchorPane("/Vistas/inicio.fxml");
        marcarItemMenuSeleccionado(labelInicio);
    }

    public void setUsuario(Usuario usuario) {
        this.UsuarioLogueado = usuario;
        textUsuarioLogueado.setText(usuario.getNombre());
        textTipoUsuario.setText(usuario.getRol().name());
    }

    @FXML
    private void mostrarInicio(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/inicio.fxml");
        marcarItemMenuSeleccionado(labelInicio);
    }

    @FXML
    private void mostrarReservas(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Reservas.fxml");
        marcarItemMenuSeleccionado(labelReservas);
    }

    @FXML
    private void mostrarHabitaciones(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Habitaciones.fxml");
        marcarItemMenuSeleccionado(labelHabitaciones);
    }

    @FXML
    private void mostrarHuespedes(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Huespedes.fxml");
        marcarItemMenuSeleccionado(labelHuespedes);
    }

    @FXML
    private void mostrarservicios(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Servicios.fxml");
        marcarItemMenuSeleccionado(labelServicios);
    }

    @FXML
    private void mostrarProductos(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Productos.fxml");
        marcarItemMenuSeleccionado(labelProductos);
    }

    @FXML
    private void mostrarLimpieza(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/LimpiezaGeneral.fxml");
        marcarItemMenuSeleccionado(labelLimpieza);
    }

    @FXML
    private void mostrarCalendario(MouseEvent event) {
        cargarVistaEnAnchorPane("/Vistas/Calendarios.fxml");
        marcarItemMenuSeleccionado(labelCalendario);
    }

    @FXML
    private void cerrarSesion(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/IniciarSesion.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) anchorPanePrincipal.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("No se pudo cerrar sesión.");
        }
    }

    private void cargarVistaEnAnchorPane(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();

            Object controller = loader.getController();

            if (controller instanceof HabitacionesController habitacionesController) {
                habitacionesController.setUsuario(UsuarioLogueado);
            } else if (controller instanceof HuespedesController huespedesController) {
                huespedesController.setUsuario(UsuarioLogueado);
            } else if (controller instanceof ServiciosController serviciosController) {
                serviciosController.setUsuario(UsuarioLogueado);
            } else if (controller instanceof ProductosController productosController) {
                productosController.setUsuario(UsuarioLogueado);
            } else if (controller instanceof ReservasController reservasController) {
                reservasController.setUsuario(UsuarioLogueado); // ⬅️ ESTA LÍNEA FALTABA
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

    private void marcarItemMenuSeleccionado(Label seleccionado) {
        Label[] labels = {
            labelInicio, labelReservas, labelHabitaciones,
            labelHuespedes, labelServicios, labelProductos,
            labelLimpieza, labelCalendario, labelCerrarSecion
        };

        for (Label label : labels) {
            label.getStyleClass().remove("selected");
        }

        if (!seleccionado.getStyleClass().contains("selected")) {
            seleccionado.getStyleClass().add("selected");
        }
    }

}
