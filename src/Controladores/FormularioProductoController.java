package Controladores;

import CRUD.ProductoDAO;
import Modelo.Producto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Wilian Lopez
 */
public class FormularioProductoController {

    @FXML
    private TextField txtNombreProducto;
    @FXML
    private TextField txtTipoProducto;
    @FXML
    private TextArea textDescripcion;
    @FXML
    private TextField txtPrecio;

    private boolean esEdicion = false;
    private Producto productoExistente;
    private boolean guardado = false;
    @FXML
    private Button btnGuardar;

    /**
     * Initializes the controller class.
     */
    public void inicializarFormulario(Producto producto, boolean edicion) {
        this.esEdicion = edicion;
        this.productoExistente = producto;

        if (edicion && producto != null) {
            txtNombreProducto.setText(producto.getNombre());
            txtTipoProducto.setText(producto.getTipo());
            textDescripcion.setText(producto.getDescripcion());
            txtPrecio.setText(String.valueOf(producto.getPrecio()));
        }
    }

    @FXML
    private void btnGuardar(ActionEvent event) {
        guardarProducto();
    }

    private void guardarProducto() {
        try {
            String nombre = txtNombreProducto.getText().trim();
            String tipo = txtTipoProducto.getText().trim();
            String descripcion = textDescripcion.getText().trim();
            String precioTexto = txtPrecio.getText().trim();

            if (nombre.isEmpty() || tipo.isEmpty() || descripcion.isEmpty() || precioTexto.isEmpty()) {
                AlertaUtil.mostrarAdvertencia("Complete todos los campos.");
                return;
            }

            double precio = Double.parseDouble(precioTexto);
            if (precio < 0) {
                AlertaUtil.mostrarAdvertencia("El precio no puede ser negativo.");
                return;
            }

            Producto nuevoProducto = new Producto(
                    esEdicion ? productoExistente.getIdProducto() : 0,
                    nombre,
                    tipo,
                    precio,
                    descripcion,
                    true 
            );

            ProductoDAO dao = new ProductoDAO();
            boolean exito = esEdicion ? dao.actualizar(nuevoProducto) : dao.insertar(nuevoProducto);

            if (exito) {
                guardado = true;
                AlertaUtil.mostrarExitoTemporal(esEdicion ? "Producto actualizado." : "Producto registrado.", 2);
                cerrarVentana();
            } else {
                AlertaUtil.mostrarError("No se pudo guardar el producto.");
            }

        } catch (NumberFormatException e) {
            AlertaUtil.mostrarError("El precio debe ser un número válido.");
        } catch (Exception e) {
            AlertaUtil.mostrarError("Error al guardar el producto: " + e.getMessage());
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombreProducto.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public String getNombre() {
        return txtNombreProducto.getText().trim();
    }

}
