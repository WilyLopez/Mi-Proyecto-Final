package Controladores;

import CRUD.ServicioDAO;
import Enums.RolUsuario;
import Estructuras.ListaServicios;
import Modelo.Habitacion;
import Modelo.Servicio;
import Modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class ServiciosController implements Initializable {

    @FXML
    private Button btnEditarServicio;
    @FXML
    private Button btnEliminarServicio;
    @FXML
    private Button btnAgregarServicio;
    @FXML
    private TextField textNombreServicio;
    @FXML
    private TableView<Servicio> tableServicio;
    @FXML
    private TableColumn<Servicio, Integer> ColumnNumero;
    @FXML
    private TableColumn<Servicio, String> ColumnNombre;
    @FXML
    private TableColumn<Servicio, String> ColumnDescripcion;
    @FXML
    private TableColumn<Servicio, Double> ColumnPrecio;
    @FXML
    private ComboBox<String> cboxOrdenarPor;

    private Usuario usuarioLogueado;
    private ObservableList<Servicio> listaObservable;
    private ListaServicios listaServicios;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaServicios = new ListaServicios();
        listaObservable = FXCollections.observableArrayList();
        configurarTabla();
        configurarComboBox();
        configurarBusqueda();
        try {
            cargarServiciosDesdeBD();
        } catch (SQLException ex) {
            Logger.getLogger(ServiciosController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    private void editarServicio(ActionEvent event) {
        if (usuarioLogueado.getRol() != RolUsuario.ADMIN) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden editar servicios.");
            return;
        }

        Servicio seleccionado = tableServicio.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaUtil.mostrarAdvertencia("Seleccione un servicio para editar.");
            return;
        }

        abrirFormulario(seleccionado, true);
    }

    @FXML
    private void eliminarServicio(ActionEvent event) {
        if (usuarioLogueado.getRol() != RolUsuario.ADMIN) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden eliminar servicios.");
            return;
        }

        Servicio seleccionado = tableServicio.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaUtil.mostrarAdvertencia("Seleccione un servicio para eliminar.");
            return;
        }

        try {
            ServicioDAO dao = new ServicioDAO();
            if (dao.eliminar(seleccionado.getIdServicio())) {
                listaServicios.eliminarPorId(seleccionado.getIdServicio());
                AlertaUtil.mostrarInformacion("Servicio eliminado correctamente.");
                actualizarTabla();
            } else {
                AlertaUtil.mostrarError("No se pudo eliminar el servicio.");
            }
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error al eliminar servicio: " + e.getMessage());
        }
    }

    @FXML
    private void agregarServicio(ActionEvent event) {
        if (usuarioLogueado.getRol() != RolUsuario.ADMIN) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden agregar servicios.");
            return;
        }

        abrirFormulario(null, false);
    }

    private void configurarTabla() {
        ColumnNumero.setCellValueFactory(cellData -> {
            int index = tableServicio.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });
        ColumnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ColumnDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        ColumnPrecio.setCellValueFactory(new PropertyValueFactory<>("costo"));
        tableServicio.setItems(listaObservable);
    }

    private void configurarComboBox() {
        cboxOrdenarPor.setItems(FXCollections.observableArrayList(
                "Desde el primero", "Desde el último"
        ));
        cboxOrdenarPor.getSelectionModel().selectFirst();
        cboxOrdenarPor.setOnAction(e -> actualizarTabla());
    }

    private void configurarBusqueda() {
        textNombreServicio.textProperty().addListener((obs, oldVal, newVal) -> actualizarTabla());
    }

    private void cargarServiciosDesdeBD() throws SQLException {
        ServicioDAO dao = new ServicioDAO();
        List<Servicio> servicios = dao.listar();
        listaServicios.limpiar();
        for (Servicio s : servicios) {
            listaServicios.agregarAlFinal(s);
        }
        actualizarTabla();
    }

    private void actualizarTabla() {
        String orden = cboxOrdenarPor.getValue();
        String filtro = textNombreServicio.getText().trim().toLowerCase();

        List<Servicio> servicios = "Desde el último".equals(orden)
                ? listaServicios.getServiciosDesdeFin()
                : listaServicios.getServiciosDesdeInicio();

        listaObservable.setAll(servicios.stream()
                .filter(s -> s.getNombre().toLowerCase().contains(filtro))
                .toList());
    }

    private void abrirFormulario(Servicio servicio, boolean esEdicion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Formularios/FormularioServicio.fxml"));
            Parent root = loader.load();
            FormularioServicioController controller = loader.getController();
            controller.inicializarFormulario(servicio, esEdicion);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Editar Servicio" : "Agregar Servicio");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isGuardado()) {
                ServicioDAO dao = new ServicioDAO();
                List<Servicio> servicios = dao.listar();
                listaServicios = new ListaServicios();
                listaObservable.clear();

                for (Servicio s : servicios) {
                    if (s.getNombre().equals(controller.getNombre())) {
                        if (controller.isAgregarAlInicio()) {
                            listaServicios.agregarAlInicio(s);
                            listaObservable.add(0, s);
                        } else {
                            listaServicios.agregarAlFinal(s);
                            listaObservable.add(s);
                        }
                    } else {
                        listaServicios.agregarAlFinal(s);
                        listaObservable.add(s);
                    }
                }

                tableServicio.setItems(listaObservable);
                tableServicio.refresh();
            }

        } catch (IOException | SQLException e) {
            AlertaUtil.mostrarError("Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
