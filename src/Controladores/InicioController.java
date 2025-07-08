package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class InicioController implements Initializable {

    @FXML
    private AnchorPane AnchorPaneInicio;
    @FXML
    private TextField textFechaHoy;
    @FXML
    private TextField textTotalHabitacionesOcupadas;
    @FXML
    private TextField textTotalReservasHoy;
    @FXML
    private TextField textTotalLimpiezasAhendatas;
    @FXML
    private TextField textTotalSolicitudAtencion;
    @FXML
    private Pane PaneHabitaciones;
    @FXML
    private Button btnReservas;
    @FXML
    private Button btnHabitaciones;
    @FXML
    private Button btnCalendario;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void mostrarReservas(ActionEvent event) {
    }

    @FXML
    private void mostrarHabitaciones(ActionEvent event) {
    }

    @FXML
    private void mostrarCalendario(ActionEvent event) {
    }
    
}
