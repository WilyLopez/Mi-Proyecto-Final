package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;



public class GananciasController implements Initializable {

    @FXML
    private DatePicker datePickerFecha;
    @FXML
    private ComboBox<?> comboFiltro;
    @FXML
    private Button btnBuscar;
    @FXML
    private TableView<?> tablaGanancias;
    @FXML
    private TableColumn<?, ?> CoumnNumeroOrden;
    @FXML
    private TableColumn<?, ?> ColumnFecha;
    @FXML
    private TableColumn<?, ?> ColumnDetalle;
    @FXML
    private TableColumn<?, ?> ColumnMontoTotal;
    @FXML
    private TextField textTotal;
    @FXML
    private DatePicker datePickerInicio;
    @FXML
    private DatePicker datePickerFin;
    @FXML
    private Button btnCalcularRango;
    @FXML
    private TextField textTotalRango;
    @FXML
    private BarChart<?, ?> graficoGanancias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
    }

    @FXML
    private void buscar(ActionEvent event) {
    }

    @FXML
    private void CalcultarTotalEnRango(ActionEvent event) {
    }


}
