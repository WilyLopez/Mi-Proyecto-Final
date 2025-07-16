package Controladores;

import Util.AlertaUtil;
import CRUD.ServicioDAO;
import Modelo.Servicio;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FormularioServicioController {

    @FXML
    private TextField textNombre;
    @FXML
    private TextArea textDescripcion;
    @FXML
    private TextField textPrecio;
    @FXML
    private Button btnAgregarAlFinal;
    @FXML
    private Button btnAgregarAlInicio;

    private boolean esEdicion = false;
    private Servicio servicioExistente;
    private boolean guardado = false;
    private boolean agregarAlInicio = false;

    /**
     * Initializes the controller class.
     */
    public void inicializarFormulario(Servicio servicio, boolean edicion) {
        this.esEdicion = edicion;
        this.servicioExistente = servicio;

        if (edicion && servicio != null) {
            textNombre.setText(servicio.getNombre());
            textDescripcion.setText(servicio.getDescripcion());
            textPrecio.setText(String.valueOf(servicio.getCosto()));

        }
    }

    @FXML
    private void agregarAlfinal(ActionEvent event) {
        agregarAlInicio = false;
        try {
            guardarServicio();
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void agregarAlInicio(ActionEvent event) {
        agregarAlInicio = true;
        try {
            guardarServicio();
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void guardarServicio() throws SQLException {
        try {
            String nombre = textNombre.getText().trim();
            String descripcion = textDescripcion.getText().trim();
            String precioTexto = textPrecio.getText().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || precioTexto.isEmpty()) {
                AlertaUtil.mostrarAdvertencia("Complete todos los campos.");
                return;
            }

            double precio = Double.parseDouble(precioTexto);

            Servicio nuevoServicio = new Servicio(
                    esEdicion ? servicioExistente.getIdServicio() : 0,
                    nombre,
                    descripcion,
                    precio
            );

            ServicioDAO dao = new ServicioDAO();
            boolean exito;

            if (esEdicion) {
                exito = dao.actualizar(nuevoServicio);
            } else {
                exito = dao.insertar(nuevoServicio);
            }

            if (exito) {
                guardado = true;
                AlertaUtil.mostrarInformacion(esEdicion ? "Servicio actualizado." : "Servicio registrado.");
                cerrarVentana();
            } else {
                AlertaUtil.mostrarError("No se pudo guardar el servicio.");
            }

        } catch (NumberFormatException e) {
            AlertaUtil.mostrarError("El precio debe ser un número válido.");
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) textNombre.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public boolean isAgregarAlInicio() {
        return agregarAlInicio;
    }

    public String getNombre() {
        return textNombre.getText().trim();
    }

}
