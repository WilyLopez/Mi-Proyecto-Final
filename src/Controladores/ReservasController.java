package Controladores;

import CRUD.HabitacionesDAO;
import CRUD.HuespedDAO;
import Modelo.Habitacion;
import Modelo.Huesped;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class ReservasController implements Initializable {

    @FXML
    private AnchorPane AnchorPaneReservas;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnRegistrarReservas;
    @FXML
    private Button btnAgregarServicio;
    @FXML
    private Button btnVerReservas;
    @FXML
    private Button btnSelecionarHabitacion;
    @FXML
    private TextField textNumeroHabitacion;
    @FXML
    private TextField textDetallesHabitacion;
    @FXML
    private TextField textTipoHabitacion;
    @FXML
    private TextField TextEstadoHabitacion;
    @FXML
    private TextField textDNIHuesped;
    @FXML
    private TextField textNombreCompletoHuesped;
    @FXML
    private TextField textTelefonoHuesped;
    @FXML
    private TextField textCorreoHuesped;
    @FXML
    private TextField textPrecioHabitacion;
    @FXML
    private DatePicker DatePickerFechaEntrada;
    @FXML
    private TextField textHoraEntrada;
    @FXML
    private DatePicker DatePickerFechaSalida;
    @FXML
    private TextField textTotal;
    @FXML
    private TableView<?> TableProductoOServicio;
    @FXML
    private TableColumn<?, ?> ColumnTipo;
    @FXML
    private TableColumn<?, ?> ColumnNombre;
    @FXML
    private TableColumn<?, ?> ColunmCantidad;
    @FXML
    private TableColumn<?, ?> ColumnPrecioUnitario;
    @FXML
    private TableColumn<?, ?> ColumnSubTotal;
    @FXML
    private VBox VistaTablaServicioYProducto;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarRestriccionesFechaEntrada();
        configurarAutocompletadoHora();
    }

    @FXML
    private void cancelarReserva(ActionEvent event) {
    }

    @FXML
    private void registrarServicio(ActionEvent event) {
    }

    @FXML
    private void agregarServicio(ActionEvent event) {
    }

    @FXML
    private void verTotaslasReservas(ActionEvent event) {
    }

    @FXML
    private void seleccionarHabitacion(ActionEvent event) {
        String input = textNumeroHabitacion.getText().trim();

        if (input.isEmpty()) {
            AlertaUtil.mostrarAdvertencia("Por favor, ingrese el número de habitación en su campo correspondiente.");
            return;
        }

        int numero;
        try {
            numero = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarError("El número de habitación debe ser un valor numérico entero.");
            return;
        }

        try {
            HabitacionesDAO dao = new HabitacionesDAO();
            Habitacion habitacion = dao.buscarPorNumero(numero);

            if (habitacion == null) {
                AlertaUtil.mostrarInformacion("No se encontró una habitación con el número: " + numero);
                limpiarCamposHabitacion();
                return;
            }

            textTipoHabitacion.setText(habitacion.getTipo().name());
            TextEstadoHabitacion.setText(habitacion.getEstado().name());
            textPrecioHabitacion.setText(String.valueOf(habitacion.getPrecio()));
            textDetallesHabitacion.setText(habitacion.getDetalle());

            AlertaUtil.mostrarExitoTemporal("Habitación encontrada y cargada correctamente", 2);

        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("Ocurrió un error al buscar la habitación.");
        }
    }

    @FXML
    private void calcularTotal(ActionEvent event) {
    }

    @FXML
    private void buscarHuespedPorDNI(ActionEvent event) {
        String dni = textDNIHuesped.getText().trim();
        if (dni.matches("\\d{8}")) {
            try {
                HuespedDAO dao = new HuespedDAO();
                Huesped h = dao.buscarPorDni(dni);
                if (h != null) {
                    textNombreCompletoHuesped.setText(h.getNombreCompleto());
                    textTelefonoHuesped.setText(h.getTelefono());
                    textCorreoHuesped.setText(h.getCorreo());
                } else {
                    textNombreCompletoHuesped.clear();
                    textTelefonoHuesped.clear();
                    textCorreoHuesped.clear();
                }
            } catch (Exception e) {
                System.out.println("Error..");
                e.printStackTrace();
            }

        } else {
            textNombreCompletoHuesped.clear();
            textTelefonoHuesped.clear();
            textCorreoHuesped.clear();
        }
    }

    private void limpiarCamposHabitacion() {
        textTipoHabitacion.clear();
        TextEstadoHabitacion.clear();
        textPrecioHabitacion.clear();
        textDetallesHabitacion.clear();
    }

    private boolean validarFechas() {
        LocalDate entrada = DatePickerFechaEntrada.getValue();
        LocalDate salida = DatePickerFechaSalida.getValue();

        if (entrada == null || salida == null) {
            AlertaUtil.mostrarAdvertencia("Debe seleccionar ambas fechas.");
            return false;
        }

        if (!salida.isAfter(entrada)) {
            AlertaUtil.mostrarAdvertencia("La fecha de salida debe ser posterior a la fecha de entrada.");
            return false;
        }

        return true;
    }

    private void configurarRestriccionesFechaEntrada() {        
        DatePickerFechaEntrada.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void configurarAutocompletadoHora() {
        DatePickerFechaEntrada.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                LocalTime horaActual = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                textHoraEntrada.setText(horaActual.format(formatter));
               
                DatePickerFechaSalida.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || !date.isAfter(newVal));
                    }
                });
            }
        });
    }

}
