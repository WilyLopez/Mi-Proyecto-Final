package Controladores;

import CRUD.HabitacionLimpiezaDAO;
import Estructuras.ColaPrioridadLimpieza;
import Enums.EstadoLimpieza;
import Enums.PrioridadLimpieza;
import Modelo.Habitacion;
import Util.AlertaUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class GestionLimpiezaController implements Initializable {

    @FXML private TextField textNumeroHabitacion;
    @FXML private TextField textTipoHabitacion;
    @FXML private TextField textEstadoGeneral;
    @FXML private ComboBox<EstadoLimpieza> cboxEstadoLimpieza;
    @FXML private ComboBox<PrioridadLimpieza> cBoxPrioridadLimpieza;
    @FXML private DatePicker DateFechaUltimaLimpieza;
    @FXML private TextField textHoraUltimaLimpieza;
    @FXML private DatePicker DateFechaProximaLimpieza;
    @FXML private TextField textHoraProximaLimpieza;
    @FXML private TextArea textAreaNotaLimpieza;
    @FXML private Button btnMarcarComoLimpia;
    @FXML private Button btnProgrmarLimpieza;
    @FXML private Button btnCancelar;

    private Habitacion habitacion;
    private ColaPrioridadLimpieza cola;
    private HabitacionLimpiezaDAO dao = new HabitacionLimpiezaDAO();
    private LimpiezaGeneralController controladorPadre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboxEstadoLimpieza.getItems().setAll(EstadoLimpieza.values());
        cBoxPrioridadLimpieza.getItems().setAll(PrioridadLimpieza.values());
    }

    @FXML
    private void marcarComoLimpia(ActionEvent event) {
        try {
            PrioridadLimpieza prioridad = cBoxPrioridadLimpieza.getValue();
            String notas = textAreaNotaLimpieza.getText();

            dao.marcarComoLimpia(habitacion.getNumero(), notas, prioridad);
            cola.remove(habitacion.getNumero());
            notificarActualizacion();
            cerrarVentana();

        } catch (Exception e) {
            AlertaUtil.mostrarError("No se pudo marcar como limpia.");
            e.printStackTrace();
        }
    }

    @FXML
    private void programarLimpieza(ActionEvent event) {
        try {
            limpiarEstilosErrores();

            PrioridadLimpieza prioridad = cBoxPrioridadLimpieza.getValue();
            if (prioridad == null) {
                AlertaUtil.mostrarError("Debes seleccionar una prioridad.");
                return;
            }

            if (!validarHora(textHoraProximaLimpieza)) return;

            String notas = textAreaNotaLimpieza.getText();
            LocalDateTime proxima = null;

            if (DateFechaProximaLimpieza.getValue() != null && !textHoraProximaLimpieza.getText().isBlank()) {
                proxima = LocalDateTime.of(
                        DateFechaProximaLimpieza.getValue(),
                        LocalTime.parse(textHoraProximaLimpieza.getText())
                );
            }

            dao.solicitarLimpieza(habitacion.getNumero(), prioridad, notas, proxima);
            habitacion.setPrioridadLimpieza(prioridad);
            cola.enqueue(habitacion, prioridad);

            notificarActualizacion();
            cerrarVentana();

        } catch (Exception e) {
            AlertaUtil.mostrarError("Error al programar limpieza.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
        cargarDatosHabitacion();
    }

    public void setCola(ColaPrioridadLimpieza cola) {
        this.cola = cola;
    }

    public void setControladorPadre(LimpiezaGeneralController controller) {
        this.controladorPadre = controller;
    }

    private void cargarDatosHabitacion() {
        textNumeroHabitacion.setText(String.valueOf(habitacion.getNumero()));
        textTipoHabitacion.setText(habitacion.getTipo().name());
        textEstadoGeneral.setText(habitacion.getEstado().name());
        cboxEstadoLimpieza.setValue(habitacion.getEstadoLimpieza());
        cBoxPrioridadLimpieza.setValue(habitacion.getPrioridadLimpieza());

        if (habitacion.getUltimaLimpieza() != null) {
            DateFechaUltimaLimpieza.setValue(habitacion.getUltimaLimpieza().toLocalDate());
            textHoraUltimaLimpieza.setText(habitacion.getUltimaLimpieza().toLocalTime().toString());
        }

        if (habitacion.getProximaLimpieza() != null) {
            DateFechaProximaLimpieza.setValue(habitacion.getProximaLimpieza().toLocalDate());
            textHoraProximaLimpieza.setText(habitacion.getProximaLimpieza().toLocalTime().toString());
        }

        textAreaNotaLimpieza.setText(habitacion.getNotasLimpieza());
    }

    private boolean validarHora(TextField campoHora) {
        String hora = campoHora.getText();
        if (!hora.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
            campoHora.setStyle("-fx-border-color: #ff4c4c; -fx-border-width: 2;");
            AlertaUtil.mostrarError("Hora inválida. Usa el formato hh:mm");
            return false;
        }
        return true;
    }

    private void limpiarEstilosErrores() {
        textHoraProximaLimpieza.setStyle("");
        textHoraUltimaLimpieza.setStyle("");
    }

    private void notificarActualizacion() {
        if (controladorPadre != null) {
            controladorPadre.actualizarDesdeFormulario();
        }
    }
}