package Controladores;

import CRUD.HabitacionLimpiezaDAO;
import CRUD.HabitacionesDAO;
import Enums.EstadoHabitacion;
import Enums.EstadoLimpieza;
import Enums.PrioridadLimpieza;
import Estructuras.ColaPrioridadLimpieza;
import Modelo.Habitacion;
import Util.AlertaUtil;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LimpiezaGeneralController implements Initializable {

    @FXML
    private ComboBox<PrioridadLimpieza> combxPrioridadReserva;
    @FXML
    private TableView<Habitacion> tablHabitacionesLimpieza;
    @FXML
    private TableColumn<Habitacion, Integer> ColumnNumeroOrden;
    @FXML
    private TableColumn<Habitacion, Integer> ColumnNumeroHabitacion;
    @FXML
    private TableColumn<Habitacion, String> ColumnEstadoLimpieza;
    @FXML
    private TableColumn<Habitacion, String> ColumnPrioridad;
    @FXML
    private TableColumn<Habitacion, String> ColumnTiempo;
    @FXML
    private Button btnGestionar;

    private ColaPrioridadLimpieza cola = new ColaPrioridadLimpieza();
    private HabitacionLimpiezaDAO dao = new HabitacionLimpiezaDAO();
    private ObservableList<Habitacion> habitacionesObservable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        sincronizarHabitacionesEnLimpieza();
        combxPrioridadReserva.getItems().setAll(PrioridadLimpieza.values());
        combxPrioridadReserva.setOnAction(event -> actualizarDesdeFormulario());
        cargarDesdeBD();
    }

    @FXML
    private void gestiorLimpieza(ActionEvent event) {
        Habitacion habitacionSeleccionada = tablHabitacionesLimpieza.getSelectionModel().getSelectedItem();
        if (habitacionSeleccionada == null) {
            AlertaUtil.mostrarAdvertencia("Selecciona una habitación primero.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/GestionLimpieza.fxml"));
            Parent root = loader.load();
            GestionLimpiezaController controller = loader.getController();
            controller.setHabitacion(habitacionSeleccionada);
            controller.setCola(cola);
            controller.setControladorPadre(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Gestionar Limpieza");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Actualización automática al cerrar modal
            cargarDesdeBD();

        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("Error al abrir el formulario de limpieza.");
        }
    }

    private void configurarColumnas() {
        ColumnNumeroOrden.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(() ->
                        cola.getOrdenDeHabitacion(cellData.getValue().getNumero())));

        ColumnNumeroHabitacion.setCellValueFactory(new PropertyValueFactory<>("numero"));

        ColumnEstadoLimpieza.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getEstadoLimpieza().name()));

        ColumnPrioridad.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    var prioridad = cellData.getValue().getPrioridadLimpieza();
                    return prioridad != null ? prioridad.name() : "—";
                }));

        ColumnTiempo.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    long minutos = cola.getMinutosEnCola(cellData.getValue().getNumero());
                    return minutos >= 0 ? minutos + " min" : "—";
                }));
    }

    private void cargarDesdeBD() {
        try {
            List<Habitacion> habitacionesBD = dao.obtenerPendientesLimpieza();
            cola.clear();
            for (Habitacion h : habitacionesBD) {
                cola.enqueue(h, h.getPrioridadLimpieza());
            }
            actualizarDesdeFormulario();
        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("Error al cargar habitaciones desde la base de datos.");
        }
    }

    private void sincronizarHabitacionesEnLimpieza() {
        HabitacionesDAO habitacionesDAO = new HabitacionesDAO();
        try {
            List<Habitacion> enLimpieza = habitacionesDAO.listarHabitacionesEnLimpieza();
            for (Habitacion h : enLimpieza) {
                Habitacion habLimpieza = dao.obtenerDatosLimpieza(h.getNumero());

                if (habLimpieza == null || habLimpieza.getEstadoLimpieza() == EstadoLimpieza.LIMPIA) {
                    PrioridadLimpieza prioridad = h.getEstado() == EstadoHabitacion.MANTENIMIENTO
                            ? PrioridadLimpieza.BAJA
                            : PrioridadLimpieza.ALTA;

                    dao.solicitarLimpieza(
                            h.getNumero(),
                            prioridad,
                            "Sincronizado automáticamente desde estado general (" + h.getEstado() + ")",
                            null
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("Error al sincronizar habitaciones en limpieza.");
        }
    }

    public void actualizarDesdeFormulario() {
        habitacionesObservable.clear();
        List<Habitacion> habitaciones = (combxPrioridadReserva.getValue() != null)
                ? cola.getByPriority(combxPrioridadReserva.getValue())
                : cola.getAll();

        habitacionesObservable.addAll(habitaciones);
        tablHabitacionesLimpieza.setItems(habitacionesObservable);
        tablHabitacionesLimpieza.refresh();
    }
}
