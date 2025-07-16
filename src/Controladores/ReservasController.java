package Controladores;

import CRUD.DetallesReservaDAO;
import Util.AlertaUtil;
import CRUD.HabitacionesDAO;
import CRUD.HuespedDAO;
import CRUD.ProductoDAO;
import CRUD.ReservaDAO;
import CRUD.ServicioDAO;
import Enums.EstadoHabitacion;
import Enums.TipoItem;
import Modelo.DetalleReserva;
import Modelo.Habitacion;
import Modelo.Huesped;
import Modelo.Producto;
import Modelo.Reserva;
import Modelo.Servicio;
import Modelo.Usuario;
import Util.CeldaCantidadConBotones;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
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
    private TableView<DetalleReserva> TableProductoOServicio;
    @FXML
    private TableColumn<DetalleReserva, String> ColumnTipo;
    @FXML
    private TableColumn<DetalleReserva, String> ColumnNombre;
    @FXML
    private TableColumn<DetalleReserva, Integer> ColunmCantidad;
    @FXML
    private TableColumn<DetalleReserva, Double> ColumnPrecioUnitario;
    @FXML
    private TableColumn<DetalleReserva, Double> ColumnSubTotal;
    @FXML
    private VBox VistaTablaServicioYProducto;

    private ObservableList<DetalleReserva> detallesReserva = FXCollections.observableArrayList();
    private Usuario usuarioLogueado;
    @FXML
    private Button btnAgregarProducto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarRestriccionesFechaEntrada();
        configurarAutocompletadoHora();
        configurarTablaDetalle();
        configurarValidacionCampos();
        TableProductoOServicio.setItems(detallesReserva);
        DatePickerFechaEntrada.valueProperty().addListener((obs, oldVal, newVal) -> calcularTotal(null));
        DatePickerFechaSalida.valueProperty().addListener((obs, oldVal, newVal) -> calcularTotal(null));
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        this.usuarioLogueado = usuario;
    }

    @FXML
    private void registrarReserva(ActionEvent event) {
        try {
            if (!validarCamposObligatorios()) {
                AlertaUtil.mostrarAdvertencia("Por favor, complete todos los campos obligatorios.");
                return;
            }

            if (!TextEstadoHabitacion.getText().equalsIgnoreCase("LIBRE")) {
                AlertaUtil.mostrarAdvertencia("La habitación no se encuentra disponible.");
                return;
            }

            String dni = sanitizarEntrada(textDNIHuesped.getText().trim());
            if (!dni.matches("\\d{8}")) {
                AlertaUtil.mostrarAdvertencia("El DNI debe contener exactamente 8 dígitos.");
                return;
            }
            if (!validarCorreo(textCorreoHuesped.getText().trim())) {
                AlertaUtil.mostrarAdvertencia("El correo electrónico no tiene un formato válido.");
                return;
            }

            HuespedDAO huespedDAO = new HuespedDAO();
            Huesped huesped = huespedDAO.buscarPorDni(dni);
            if (huesped == null) {
                huesped = new Huesped(
                        dni,
                        sanitizarEntrada(textNombreCompletoHuesped.getText().trim()),
                        sanitizarEntrada(textTelefonoHuesped.getText().trim()),
                        sanitizarEntrada(textCorreoHuesped.getText().trim())
                );
                if (!huespedDAO.insertar(huesped)) {
                    AlertaUtil.mostrarError("No se pudo registrar el huésped.");
                    return;
                }
            }

            int numHabitacion = Integer.parseInt(textNumeroHabitacion.getText().trim());
            LocalDate fechaEntrada = DatePickerFechaEntrada.getValue();
            LocalDate fechaSalida = DatePickerFechaSalida.getValue();
            LocalTime horaEntrada = LocalTime.parse(textHoraEntrada.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime fechaHoraEntrada = LocalDateTime.of(fechaEntrada, horaEntrada);
            LocalDateTime fechaHoraSalida = LocalDateTime.of(fechaSalida, horaEntrada);
            double total = Double.parseDouble(textTotal.getText().trim());

            Reserva reserva = new Reserva(dni, numHabitacion, fechaHoraEntrada, fechaHoraSalida, total);
            ReservaDAO reservaDAO = new ReservaDAO();
            if (!reservaDAO.insertar(reserva)) {
                AlertaUtil.mostrarError("No se pudo registrar la reserva.");
                return;
            }

            int idReserva = reserva.getIdReserva();

            if (!detallesReserva.isEmpty()) {
                DetallesReservaDAO detalleDAO = new DetallesReservaDAO();
                for (DetalleReserva detalle : detallesReserva) {
                    detalle.setIdReserva(idReserva);
                    if (!detalleDAO.insertar(detalle)) {
                        AlertaUtil.mostrarAdvertencia("Error al registrar un detalle de la reserva.");
                    }
                }
            }

            HabitacionesDAO habitacionesDAO = new HabitacionesDAO();
            Habitacion habitacion = habitacionesDAO.buscarPorNumero(numHabitacion);
            if (habitacion != null) {
                habitacion.setEstado(EstadoHabitacion.OCUPADO);
                habitacionesDAO.actualizarHabitacion(habitacion);
            }

            AlertaUtil.mostrarExitoTemporal("Reserva registrada correctamente.", 2);
            limpiarCamposReserva();

        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error en la base de datos al registrar la reserva.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarError("El número de habitación o el total deben ser valores numéricos válidos.");
        } catch (Exception e) {
            AlertaUtil.mostrarError("Error inesperado al registrar la reserva.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelarReserva(ActionEvent event) {
        if (AlertaUtil.confirmar("¿Está seguro de que desea cancelar y limpiar los campos?")) {
            limpiarCamposReserva();
        }
    }

    @FXML
    private void agregarServicio(ActionEvent event) {
        abrirVentanaSeleccion("/Vistas/Servicios.fxml", "Seleccionar Servicios", (List<Servicio> seleccionados) -> {
            for (Servicio servicio : seleccionados) {
                DetalleReserva detalle = new DetalleReserva(TipoItem.SERVICIO, servicio.getIdServicio(),
                        servicio.getNombre(), 1, servicio.getCosto());
                actualizarDetallesReserva(detalle);
            }
            actualizarTabla();
        });
    }

    @FXML
    private void abrirVentanaSeleccionProductos(ActionEvent event) {
        abrirVentanaSeleccion("/Vistas/Productos.fxml", "Seleccionar Productos", (List<Producto> seleccionados) -> {
            for (Producto producto : seleccionados) {
                DetalleReserva detalle = new DetalleReserva(TipoItem.PRODUCTO, producto.getIdProducto(),
                        producto.getNombre(), 1, producto.getPrecio());
                actualizarDetallesReserva(detalle);
            }
            actualizarTabla();
        });
    }

    @FXML
    private void verTotaslasReservas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/VerReservas.fxml"));
            Parent vista = loader.load();

            VerReservasController controller = loader.getController();
            controller.setUsuario(usuarioLogueado);

            AnchorPaneReservas.getChildren().clear();
            AnchorPaneReservas.getChildren().add(vista);

            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

        } catch (IOException e) {
            AlertaUtil.mostrarError("No se pudo cargar la vista de todas las reservas.");
            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionarHabitacion(ActionEvent event) {
        String input = sanitizarEntrada(textNumeroHabitacion.getText().trim());
        if (input.isEmpty()) {
            AlertaUtil.mostrarAdvertencia("Por favor, ingrese el número de habitación.");
            return;
        }

        try {
            int numero = Integer.parseInt(input);
            HabitacionesDAO dao = new HabitacionesDAO();
            Habitacion habitacion = dao.buscarPorNumero(numero);

            if (habitacion == null) {
                AlertaUtil.mostrarInformacion("No se encontró una habitación con el número: " + numero);
                limpiarCamposHabitacion();
                return;
            }

            textTipoHabitacion.setText(habitacion.getTipo().name());
            TextEstadoHabitacion.setText(habitacion.getEstado().name());
            textPrecioHabitacion.setText(String.format("%.2f", habitacion.getPrecio()));
            textDetallesHabitacion.setText(habitacion.getDetalle());
            AlertaUtil.mostrarExitoTemporal("Habitación encontrada y cargada correctamente", 2);

        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error en la base de datos al buscar la habitación.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarError("El número de habitación debe ser un valor numérico entero.");
        }
    }

    @FXML
    private void calcularTotal(ActionEvent event) {
        try {
            if (!validarFechas()) {
                return;
            }

            LocalDate entrada = DatePickerFechaEntrada.getValue();
            LocalDate salida = DatePickerFechaSalida.getValue();
            long dias = java.time.temporal.ChronoUnit.DAYS.between(entrada, salida);
            dias = Math.max(1, dias); // Asegura al menos 1 día

            double precioHabitacion;
            try {
                precioHabitacion = Double.parseDouble(sanitizarEntrada(textPrecioHabitacion.getText().trim()));
            } catch (NumberFormatException e) {
                AlertaUtil.mostrarAdvertencia("El precio de la habitación debe ser un valor numérico válido.");
                return;
            }

            double total = precioHabitacion * dias;
            for (DetalleReserva detalle : detallesReserva) {
                total += detalle.calcularSubtotal();
            }

            textTotal.setText(String.format("%.2f", total));

        } catch (Exception e) {
            AlertaUtil.mostrarError("Error al calcular el total.");
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarHuespedPorDNI(ActionEvent event) {
        String dni = sanitizarEntrada(textDNIHuesped.getText().trim());
        if (dni.matches("\\d{8}")) {
            try {
                HuespedDAO dao = new HuespedDAO();
                Huesped huesped = dao.buscarPorDni(dni);
                if (huesped != null) {
                    textNombreCompletoHuesped.setText(huesped.getNombreCompleto());
                    textTelefonoHuesped.setText(huesped.getTelefono());
                    textCorreoHuesped.setText(huesped.getCorreo());
                } else {
                    limpiarCamposHuesped();
                }
            } catch (SQLException e) {
                AlertaUtil.mostrarError("Error en la base de datos al buscar el huésped.");
                e.printStackTrace();
            }
        } else {
            limpiarCamposHuesped();
        }
    }

    private <T> void abrirVentanaSeleccion(String fxmlPath, String titulo, Consumer<List<T>> procesarSeleccion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                AlertaUtil.mostrarError("No se encontró el archivo FXML: " + fxmlPath);
                return;
            }
            Parent root = loader.load();
            Initializable controlador = loader.getController();

            if (!(controlador instanceof Seleccionable)) {
                AlertaUtil.mostrarError("El controlador no soporta la selección de elementos.");
                return;
            }

            Seleccionable<T> seleccionable = (Seleccionable<T>) controlador;
            seleccionable.setModoSeleccionMultiple(true);
            seleccionable.configurarModoSeleccion();
            seleccionable.setUsuario(usuarioLogueado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnAgregarServicio.getScene().getWindow());
            stage.showAndWait();

            List<T> seleccionados = seleccionable.getSeleccionados();
            if (seleccionados != null && !seleccionados.isEmpty()) {
                procesarSeleccion.accept(seleccionados);
            }
        } catch (IOException e) {
            AlertaUtil.mostrarError("No se pudo abrir la ventana de selección.");
            e.printStackTrace();
        }
    }

    private void actualizarDetallesReserva(DetalleReserva detalle) {
        for (DetalleReserva existente : detallesReserva) {
            if (existente.getTipoItem() == detalle.getTipoItem() && existente.getIdItem() == detalle.getIdItem()) {
                existente.setCantidad(existente.getCantidad() + 1);
                return;
            }
        }
        detallesReserva.add(detalle);
    }

    private void actualizarTabla() {
        if (!detallesReserva.isEmpty()) {
            VistaTablaServicioYProducto.setVisible(true);
            TableProductoOServicio.refresh();
        }
    }

    private boolean validarCamposObligatorios() {
        return !textDNIHuesped.getText().isEmpty()
                && !textNombreCompletoHuesped.getText().isEmpty()
                && !textTelefonoHuesped.getText().isEmpty()
                && !textCorreoHuesped.getText().isEmpty()
                && !textNumeroHabitacion.getText().isEmpty()
                && !textPrecioHabitacion.getText().isEmpty()
                && DatePickerFechaEntrada.getValue() != null
                && DatePickerFechaSalida.getValue() != null
                && !textHoraEntrada.getText().isEmpty()
                && !textTotal.getText().isEmpty();
    }

    private boolean validarCorreo(String correo) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return correo != null && correo.matches(emailRegex);
    }

    private String sanitizarEntrada(String entrada) {
        if (entrada == null) {
            return "";
        }
        return entrada.replaceAll("[^a-zA-Z0-9@._-]", "");
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

    private void configurarValidacionCampos() {
        btnRegistrarReservas.disableProperty().bind(
                textDNIHuesped.textProperty().isEmpty()
                        .or(textNombreCompletoHuesped.textProperty().isEmpty())
                        .or(textTelefonoHuesped.textProperty().isEmpty())
                        .or(textCorreoHuesped.textProperty().isEmpty())
                        .or(textNumeroHabitacion.textProperty().isEmpty())
                        .or(textPrecioHabitacion.textProperty().isEmpty())
                        .or(DatePickerFechaEntrada.valueProperty().isNull())
                        .or(DatePickerFechaSalida.valueProperty().isNull())
                        .or(textHoraEntrada.textProperty().isEmpty())
        );
    }

    private void limpiarCamposHuesped() {
        textNombreCompletoHuesped.clear();
        textTelefonoHuesped.clear();
        textCorreoHuesped.clear();
    }

    private void limpiarCamposHabitacion() {
        textTipoHabitacion.clear();
        TextEstadoHabitacion.clear();
        textPrecioHabitacion.clear();
        textDetallesHabitacion.clear();
    }

    private void limpiarCamposReserva() {
        textDNIHuesped.clear();
        limpiarCamposHuesped();
        textNumeroHabitacion.clear();
        limpiarCamposHabitacion();
        textHoraEntrada.clear();
        DatePickerFechaEntrada.setValue(null);
        DatePickerFechaSalida.setValue(null);
        textTotal.clear();
        detallesReserva.clear();
        TableProductoOServicio.refresh();
        VistaTablaServicioYProducto.setVisible(false);
    }

    private void configurarTablaDetalle() {
        ColumnTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoItem().toString()));
        ColumnNombre.setCellValueFactory(data -> {
            String nombre = obtenerNombrePorTipoYId(data.getValue().getTipoItem(), data.getValue().getIdItem());
            return new SimpleStringProperty(nombre);
        });
        ColunmCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        ColunmCantidad.setCellFactory(column -> new CeldaCantidadConBotones());
        ColumnPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        ColumnSubTotal.setCellValueFactory(cell -> {
            double subtotal = cell.getValue().calcularSubtotal();
            return new SimpleDoubleProperty(subtotal).asObject();
        });
    }

    private String obtenerNombrePorTipoYId(TipoItem tipo, int idItem) {
        try {
            if (tipo == TipoItem.PRODUCTO) {
                ProductoDAO dao = new ProductoDAO();
                Producto producto = dao.buscarPorId(idItem);
                return producto != null ? producto.getNombre() : "Desconocido";
            } else {
                ServicioDAO dao = new ServicioDAO();
                Servicio servicio = dao.buscarPorId(idItem);
                return servicio != null ? servicio.getNombre() : "Desconocido";
            }
        } catch (SQLException e) {
            return "Error";
        }
    }

    public interface Seleccionable<T> {

        void setModoSeleccionMultiple(boolean modo);

        void configurarModoSeleccion();

        void setUsuario(Usuario usuario);

        List<T> getSeleccionados();
    }
}
