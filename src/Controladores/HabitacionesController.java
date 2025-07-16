package Controladores;

import Util.AlertaUtil;
import CRUD.HabitacionesDAO;
import Enums.RolUsuario;
import Enums.TipoHabitacion;
import Estructuras.ArregloHabitaciones;
import Modelo.Habitacion;
import Modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class HabitacionesController implements Initializable {

    @FXML
    private AnchorPane AnchorPaneHabitaciones;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnInsertarHabitacion;
    @FXML
    private ComboBox<String> cBoxTipoHabitacion;
    @FXML
    private TableView<Habitacion> tablaHabitaciones;
    @FXML
    private TableColumn<Habitacion, String> ColumnNumero;
    @FXML
    private TableColumn<Habitacion, String> ColumnNumeroHabitacion;
    @FXML
    private TableColumn<Habitacion, String> ColumnTipoHabitacion;
    @FXML
    private TableColumn<Habitacion, Double> ColumnPrecio;
    @FXML
    private TableColumn<Habitacion, String> ColumnDetalles;

    private ArregloHabitaciones arregloHabitaciones;
    private Usuario usuarioLogueado;
    private ObservableList<Habitacion> habitacionesObservables;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarComboTipos();

        try {
            cargarHabitacionesDesdeBD();
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error al cargar habitaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarHabitacion(ActionEvent event) {
        if (usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden Eliminar habitaciones.");
            return;
        }

        Habitacion habitacionSeleccionada = tablaHabitaciones.getSelectionModel().getSelectedItem();
        if (habitacionSeleccionada == null) {
            AlertaUtil.mostrarAdvertencia("Por favor, selecciona una habitación.");
            return;
        }

        try {
            boolean eliminado = new HabitacionesDAO().eliminarHabitacion(habitacionSeleccionada.getNumero());
            if (eliminado) {
                cargarHabitacionesDesdeBD();
                AlertaUtil.mostrarInformacion("La habitación fue marcada como en mantenimiento.");
            } else {
                AlertaUtil.mostrarError("No se pudo eliminar la habitación.");
            }
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error al eliminar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editarHabitacion(ActionEvent event) {
        if (usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden editar habitaciones.");
            return;
        }
        Habitacion seleccionada = tablaHabitaciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            AlertaUtil.mostrarAdvertencia("Selecciona una para editar.");
            return;
        }

        abrirFormulario(seleccionada, true);
    }

    @FXML
    private void insertarHabitacion(ActionEvent event) {
        if (usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden insertar habitaciones.");
            return;
        }
        abrirFormulario(null, false);
    }

    @FXML
    private void filtralPorTipoHabitacion(ActionEvent event) {
        String tipoSeleccionado = cBoxTipoHabitacion.getValue();

        try {
            if (tipoSeleccionado == null || tipoSeleccionado.equals("TODAS")) {
                cargarHabitacionesDesdeBD();
            } else {
                TipoHabitacion tipo = TipoHabitacion.valueOf(tipoSeleccionado);
                List<Habitacion> filtradas = new HabitacionesDAO().filtrarPorTipo(tipo);
                habitacionesObservables = FXCollections.observableArrayList(filtradas);
                tablaHabitaciones.setItems(habitacionesObservables);
            }
        } catch (Exception e) {
            AlertaUtil.mostrarError("Error al filtrar habitaciones: " + e.getMessage());
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    private void configurarColumnas() {
        ColumnNumero.setCellValueFactory(cellData -> {
            int index = tablaHabitaciones.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(index));
        });

        ColumnNumeroHabitacion.setCellValueFactory(cellData
                -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumero())));

        ColumnTipoHabitacion.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getTipo().name()));

        ColumnPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        ColumnDetalles.setCellValueFactory(new PropertyValueFactory<>("detalle"));
    }

    private void cargarComboTipos() {
        cBoxTipoHabitacion.getItems().clear();
        cBoxTipoHabitacion.getItems().add("TODAS");
        for (TipoHabitacion tipo : TipoHabitacion.values()) {
            cBoxTipoHabitacion.getItems().add(tipo.name());
        }
        cBoxTipoHabitacion.setValue("TODAS");
    }

    private void cargarHabitacionesDesdeBD() throws SQLException {
        List<Habitacion> habitaciones = new HabitacionesDAO().listarHabitaciones();
        habitacionesObservables = FXCollections.observableArrayList(habitaciones);
        tablaHabitaciones.setItems(habitacionesObservables);
    }

    private void abrirFormulario(Habitacion habitacion, boolean esEdicion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Formularios/FormularioHabitacion.fxml"));
            Parent root = loader.load();

            FormularioHabitacionesController controller = loader.getController();
            controller.initializarFormulario(habitacion, esEdicion);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Editar Habitación" : "Registrar Habitación");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarHabitacionesDesdeBD();

        } catch (IOException | SQLException e) {
            AlertaUtil.mostrarError("Error al abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
