package Controladores;

import CRUD.HuespedDAO;
import Modelo.Huesped;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FormularioHuespedController {

    @FXML
    private TextField textNombreCompleto;
    @FXML
    private TextField textDni;
    @FXML
    private TextField TextTelefono;
    @FXML
    private TextField textCorreo;
    @FXML
    private Button btnGuardar;

    private Huesped huesped;
    private boolean edicion = false;
    private boolean guardado = false;

    /**
     * Initializes the controller class.
     */
    public void inicializarFormulario(Huesped h, boolean esEdicion) {
        this.huesped = h;
        this.edicion = esEdicion;

        if (esEdicion && h != null) {
            textDni.setText(h.getDni());
            textDni.setDisable(true); // No permitir cambiar DNI
            textNombreCompleto.setText(h.getNombreCompleto());
            TextTelefono.setText(h.getTelefono());
            textCorreo.setText(h.getCorreo());
        }
    }

    @FXML
    private void guardarHuesped(ActionEvent event) {
        try {
            String dni = textDni.getText().trim();
            String nombre = textNombreCompleto.getText().trim();
            String telefono = TextTelefono.getText().trim();
            String correo = textCorreo.getText().trim();

            Huesped nuevo = new Huesped(dni, nombre, telefono, correo);
            HuespedDAO dao = new HuespedDAO();

            boolean exito;
            if (edicion) {
                exito = dao.actualizar(nuevo);
            } else {
                exito = dao.insertar(nuevo);
            }

            if (exito) {
                AlertaUtil.mostrarInformacion(edicion ? "Huésped actualizado correctamente." : "Huésped registrado correctamente.");
                guardado = true;
                cerrarVentana();
            } else {
                AlertaUtil.mostrarError("No se pudo guardar el huésped.");
            }

        } catch (IllegalArgumentException ex) {
            AlertaUtil.mostrarError("Validación: " + ex.getMessage());
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error de base de datos: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertaUtil.mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setHuespedExistente(Huesped h) {
        inicializarFormulario(h, true);
    }
}
