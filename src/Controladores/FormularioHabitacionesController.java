package Controladores;

import Util.AlertaUtil;
import CRUD.HabitacionesDAO;
import Enums.EstadoHabitacion;
import Enums.TipoHabitacion;
import Modelo.Habitacion;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FormularioHabitacionesController  {

    @FXML
    private TextField textNumeroHabitacion;
    @FXML
    private ComboBox<TipoHabitacion> cBoxTipoHabitacion;
    @FXML
    private TextField textPrecioHabitacion;
    @FXML
    private TextArea textDetallesHabitacion;
    @FXML
    private Button btnGuardar;
    @FXML
    private ComboBox<EstadoHabitacion> cBoxEstadoHabitacion;
    
    private Habitacion habitacion;
    private boolean edicion = false;
    
    

    /**
     * Initializes the controller class.
     */
    public void initializarFormulario (Habitacion h, boolean esEdicion) {
        this.habitacion = h;
        this.edicion = esEdicion;
        
        cBoxTipoHabitacion.getItems().setAll(TipoHabitacion.values());
        cBoxEstadoHabitacion.getItems().setAll(EstadoHabitacion.values());
        
        if(esEdicion &&h != null){
            textNumeroHabitacion.setText(String.valueOf(h.getNumero()));
            textNumeroHabitacion.setDisable(true);
            cBoxTipoHabitacion.setValue(h.getTipo());
            textPrecioHabitacion.setText(String.valueOf(h.getPrecio()));
            textDetallesHabitacion.setText(h.getDetalle());
            cBoxEstadoHabitacion.setValue(h.getEstado());
        }
    }    

     @FXML
    private void guardarHabitacion() {
        try {
            int numero = Integer.parseInt(textNumeroHabitacion.getText());
            TipoHabitacion tipo = cBoxTipoHabitacion.getValue();
            double precio = Double.parseDouble(textPrecioHabitacion.getText());
            String detalle = textDetallesHabitacion.getText();
            EstadoHabitacion estado = cBoxEstadoHabitacion.getValue();

            Habitacion nueva = new Habitacion(numero, tipo, precio, detalle, estado);

            HabitacionesDAO dao = new HabitacionesDAO();
            boolean exito;
            if (edicion) {
                exito = dao.actualizarHabitacion(nueva);
            } else {
                exito = dao.insertarHabitacion(nueva);
            }

            if (exito) {                
                AlertaUtil.mostrarInformacion( edicion ? "Habitación actualizada" : "Habitación registrada");
                cerrarVentana();
            } else {
                AlertaUtil.mostrarError("No se pudo guardar la habitación");
            }
        } catch (Exception e) {
            AlertaUtil.mostrarError("Error ");
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) textNumeroHabitacion.getScene().getWindow();
        stage.close();
    }
}
