package Controladores;

import CRUD.DetallesReservaDAO;
import CRUD.ReservaDAO;
import Estructuras.AVLReservas;
import Modelo.Reserva;
import Modelo.Usuario;
import Util.AlertaUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class VerReservasController implements Initializable {

    @FXML
    private ComboBox<String> cBoxFormatoBusqueda;
    @FXML
    private TextField textFormatoBusqueda;
    @FXML
    private ComboBox<String> cBoxOrdenarPor;
    @FXML
    private TableView<Reserva> tableReserva;
    @FXML
    private TableColumn<Reserva, Integer> ColumnNumero;
    @FXML
    private TableColumn<Reserva, String> ColumnDniHuesped;
    @FXML
    private TableColumn<Reserva, String> ColumnNumeroHabitacion;
    @FXML
    private TableColumn<Reserva, String> ColumnFechaEntrada;
    @FXML
    private TableColumn<Reserva, String> ColumnFechaSalida;
    @FXML
    private TableColumn<Reserva, String> ColumnTotal;

    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnNuevaReserva;

    private AVLReservas arbolReservas = new AVLReservas();
    private ReservaDAO reservaDAO;
    private ObservableList<Reserva> listaReservas = FXCollections.observableArrayList();
    private Usuario usuarioLogueado;
    private  DetallesReservaDAO detalleReservaItemDAO;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @FXML
    private AnchorPane AnchorPaneVerReservas;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reservaDAO = new ReservaDAO();
        detalleReservaItemDAO = new DetallesReservaDAO();
        configurarColumnas();
        configurarComboBoxes();
        configurarBusqueda();
        cargarReservasDesdeBD();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    private void eliminarReserva(ActionEvent event) throws SQLException {
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Enums.RolUsuario.ADMIN) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden eliminar reservas.");
            return;
        }

        Reserva seleccionado = tableReserva.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaUtil.mostrarAdvertencia("Seleccione una reserva para eliminar.");
            return;
        }

        if (!AlertaUtil.confirmar("¿Está seguro de eliminar la reserva?")) {
            return;
        }

        boolean detallesEliminados = detalleReservaItemDAO.eliminarPorReserva(seleccionado.getIdReserva());

        if (!detallesEliminados) {
            AlertaUtil.mostrarError("No se pudieron eliminar los ítems asociados a la reserva.");
            return;
        }

        if (reservaDAO.eliminar(seleccionado.getIdReserva())) {
            arbolReservas.eliminarPorFecha(seleccionado.getFechaHoraEntrada());
            listaReservas.remove(seleccionado);
            tableReserva.refresh();
            AlertaUtil.mostrarInformacion("Reserva eliminada correctamente.");
        } else {
            AlertaUtil.mostrarError("No se pudo eliminar la reserva.");
        }
    }

    @FXML
    private void nuevaReserva(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/Reservas.fxml"));
            Parent vista = loader.load();

            ReservasController controller = loader.getController();
            controller.setUsuario(usuarioLogueado);

            AnchorPaneVerReservas.getChildren().clear();
            AnchorPaneVerReservas.getChildren().add(vista);

            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

        } catch (IOException e) {
            AlertaUtil.mostrarError("No se pudo cargar la vista de todas las reservas.");
            e.printStackTrace();
        }

    }

    private void configurarColumnas() {
        ColumnNumero.setCellValueFactory(cellData -> {
            int index = tableReserva.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });
        ColumnDniHuesped.setCellValueFactory(new PropertyValueFactory<>("dniHuesped"));
        ColumnNumeroHabitacion.setCellValueFactory(new PropertyValueFactory<>("numeroHabitacion"));

        ColumnFechaEntrada.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaHoraEntrada();
            return new javafx.beans.property.SimpleStringProperty(fecha.format(FORMATTER));
        });
        ColumnFechaSalida.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaHoraSalida();
            return new javafx.beans.property.SimpleStringProperty(fecha.format(FORMATTER));
        });
        ColumnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableReserva.setItems(listaReservas);
    }

    private void configurarBusqueda() {
        textFormatoBusqueda.textProperty().addListener((obs, oldVal, newVal) -> actualizarTabla());
        cBoxFormatoBusqueda.setOnAction(e -> actualizarTabla());
        cBoxOrdenarPor.setOnAction(e -> actualizarTabla());
    }

    private void configurarComboBoxes() {
        cBoxFormatoBusqueda.setItems(FXCollections.observableArrayList("DNI", "Habitación", "Fecha"));
        cBoxFormatoBusqueda.getSelectionModel().selectFirst();
        cBoxOrdenarPor.setItems(FXCollections.observableArrayList("Fecha Entrada", "DNI", "Habitación"));
        cBoxOrdenarPor.getSelectionModel().selectFirst();
    }

    private void cargarReservasDesdeBD() {
        listaReservas.clear();
        arbolReservas = new AVLReservas();
        for (Reserva r : reservaDAO.listarReservas()) {
            arbolReservas.insertar(r);
            listaReservas.add(r);
        }
        tableReserva.setItems(listaReservas);
        tableReserva.refresh();
    }

    private void actualizarTabla() {
        String criterioBusqueda = cBoxFormatoBusqueda.getValue();
        String valorBusqueda = textFormatoBusqueda.getText().trim();
        String criterioOrden = cBoxOrdenarPor.getValue();

        List<Reserva> reservasFiltradas;
        if (valorBusqueda.isEmpty()) {
            reservasFiltradas = listaReservas;
        } else {
            reservasFiltradas = switch (criterioBusqueda) {
                case "DNI" ->
                    arbolReservas.buscarPorDNI(valorBusqueda);
                case "Habitación" -> {
                    try {
                        int numero = Integer.parseInt(valorBusqueda);
                        yield arbolReservas.buscarPorHabitacion(numero);
                    } catch (NumberFormatException e) {
                        AlertaUtil.mostrarAdvertencia("Ingrese un número de habitación válido.");
                        yield listaReservas;
                    }
                }
                case "Fecha" -> {
                    try {
                        LocalDateTime fecha = Reserva.parseFechaHora(valorBusqueda);
                        Reserva reserva = arbolReservas.buscarPorFecha(fecha);
                        yield reserva != null ? List.of(reserva) : List.of();
                    } catch (IllegalArgumentException e) {
                        AlertaUtil.mostrarAdvertencia("Formato de fecha inválido. Use yyyy-MM-dd HH:mm");
                        yield listaReservas;
                    }
                }
                default ->
                    listaReservas;
            };
        }

        Comparator<Reserva> comparador = switch (criterioOrden) {
            case "DNI" ->
                Comparator.comparing(Reserva::getDniHuesped);
            case "Habitación" ->
                Comparator.comparingInt(Reserva::getNumeroHabitacion);
            default ->
                Comparator.comparing(Reserva::getFechaHoraEntrada);
        };
        reservasFiltradas = reservasFiltradas.stream()
                .sorted(comparador)
                .collect(Collectors.toList());

        tableReserva.setItems(FXCollections.observableArrayList(reservasFiltradas));
        tableReserva.refresh();
    }

}
