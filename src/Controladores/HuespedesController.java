package Controladores;

import CRUD.HuespedDAO;
import Enums.RolUsuario;
import Estructuras.ABBHuespedes;
import Modelo.Huesped;
import Modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
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

public class HuespedesController implements Initializable {

    @FXML
    private Button editarDatosHuesped;
    @FXML
    private Button btnEliminarHuesped;
    @FXML
    private Button btnAgregarHuesped;
    @FXML
    private ComboBox<String> cBoxBuscarHuesped;
    @FXML
    private TextField textBuscarHuesped;
    @FXML
    private ComboBox<String> cBoxOrdenarHuesped;
    @FXML
    private TableView<Huesped> tablaHuesped;
    @FXML
    private TableColumn<Huesped, Integer> ColumnNumeroOrden;
    @FXML
    private TableColumn<Huesped, String> columnNombreHuesped;
    @FXML
    private TableColumn<Huesped, String> ColumnDNI;
    @FXML
    private TableColumn<Huesped, String> ColumnTelefono;
    @FXML
    private TableColumn<Huesped, String> ColumnCorreo;

    private ABBHuespedes arbol;
    private ObservableList<Huesped> listaHuespedes;
    private Usuario usuarioLogueado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arbol = new ABBHuespedes();
        listaHuespedes = FXCollections.observableArrayList();
        configurarTabla();
        cargarDatosDesdeBD();
        configurarComboBoxes();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    private void editarHuesped(ActionEvent event) {
        Huesped seleccionado = tablaHuesped.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaUtil.mostrarAdvertencia("Selecciona un huésped para editar.");
            return;
        }
        abrirFormulario(seleccionado, true);

    }

    @FXML
    private void eliminarHuesped(ActionEvent event) {
        if (usuarioLogueado.getRol() != RolUsuario.ADMIN) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden eliminar huéspedes.");
            return;
        }

        Huesped seleccionado = tablaHuesped.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                HuespedDAO dao = new HuespedDAO();
                if (dao.eliminar(seleccionado.getDni())) {
                    arbol.eliminar(seleccionado.getDni());
                    listaHuespedes.remove(seleccionado);
                    AlertaUtil.mostrarInformacion("Huésped eliminado correctamente.");
                } else {
                    AlertaUtil.mostrarError("No se pudo eliminar al huésped.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                AlertaUtil.mostrarError("Error al eliminar: " + e.getMessage());
            }
        } else {
            AlertaUtil.mostrarAdvertencia("Seleccione un huésped de la tabla.");
        }
    }

    @FXML
    private void agregarHuesped(ActionEvent event) {
        abrirFormulario(null, false);
    }

    @FXML
    private void buscarHuesped(ActionEvent event) {
        String criterio = cBoxBuscarHuesped.getValue();
        if (criterio == null || criterio.isEmpty()) {
            return;
        }

        String texto = textBuscarHuesped.getText().toLowerCase().trim();
        tablaHuesped.setItems(listaHuespedes.filtered(h -> {
            switch (criterio) {
                case "DNI":
                    return h.getDni().toLowerCase().contains(texto);
                case "Nombre":
                    return h.getNombreCompleto().toLowerCase().contains(texto);
                case "Teléfono":
                    return h.getTelefono().toLowerCase().contains(texto);
                case "Correo":
                    return h.getCorreo().toLowerCase().contains(texto);
                default:
                    return false;
            }
        }));
    }

    @FXML
    private void ordenarHuesped(ActionEvent event) {
        listaHuespedes.clear();
        String orden = cBoxOrdenarHuesped.getValue();
        if (orden == null) {
            return;
        }

        switch (orden) {
            case "InOrden":
                arbol.inorden(listaHuespedes); // ✅ Se asume que ya has corregido tu ABB
                break;
            case "PreOrden":
                arbol.preorden(listaHuespedes);
                break;
            case "PostOrden":
                arbol.postorden(listaHuespedes);
                break;
        }
        tablaHuesped.setItems(listaHuespedes);
    }

    private void configurarTabla() {
        ColumnNumeroOrden.setCellValueFactory(cellData -> {
            int index = tablaHuesped.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });
        ColumnDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        columnNombreHuesped.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        ColumnTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        ColumnCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        tablaHuesped.setItems(listaHuespedes);
    }

    private void configurarComboBoxes() {
        cBoxBuscarHuesped.setItems(FXCollections.observableArrayList("DNI", "Nombre", "Teléfono", "Correo"));
        cBoxBuscarHuesped.getSelectionModel().selectFirst();
        cBoxOrdenarHuesped.setItems(FXCollections.observableArrayList("InOrden", "PreOrden", "PostOrden"));
        cBoxOrdenarHuesped.getSelectionModel().selectFirst();
    }

    private void cargarDatosDesdeBD() {
        try {
            HuespedDAO dao = new HuespedDAO();
            List<Huesped> huespedes = dao.listar();
            listaHuespedes.clear();
            arbol.vaciar();
            for (Huesped h : huespedes) {
                arbol.insertar(h);
                listaHuespedes.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("Error al cargar huéspedes: " + e.getMessage());
        }
    }

    private void recargarDatos() {
        cargarDatosDesdeBD();
        ordenarHuesped(null);
    }

    private void abrirFormulario(Huesped huesped, boolean esEdicion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Formularios/FormularioHuesped.fxml"));
            Parent root = loader.load();

            FormularioHuespedController controller = loader.getController();
            controller.inicializarFormulario(huesped, esEdicion);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Editar Huésped" : "Registrar Huésped");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isGuardado()) {
                cargarDatosDesdeBD();
            }

        } catch (IOException e) {
            AlertaUtil.mostrarError("Error al abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
