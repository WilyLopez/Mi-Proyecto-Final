package Controladores;

import Util.AlertaUtil;
import CRUD.ProductoDAO;
import Enums.RolUsuario;
import Enums.TipoAccionUsuario;
import Estructuras.ArregloProductos;
import Estructuras.PilaAccionesProducto;
import Modelo.AccionUsuario;
import Modelo.Producto;
import Modelo.Usuario;
import Util.SeleccionProducto;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class for managing products in the hotel management system.
 * Allows adding, editing, deleting, and selecting products for reservations.
 *
 * @author Wilian Lopez
 */
public class ProductosController implements Initializable, ReservasController.Seleccionable<Producto> {

    @FXML
    private ComboBox<String> CBoxOrdenar;
    @FXML
    private TextField textBuscarPorNombre;
    @FXML
    private TableView<Producto> tableProducto;
    @FXML
    private TableColumn<Producto, Integer> ColumnNumero;
    @FXML
    private TableColumn<Producto, String> ColumnNombreProducto;
    @FXML
    private TableColumn<Producto, String> ColumnTipoProducto;
    @FXML
    private TableColumn<Producto, String> ColumnDescripcion;
    @FXML
    private TableColumn<Producto, Double> ColumnPrecioProducto;
    @FXML
    private Button btnDeshacer;
    @FXML
    private Button btnRehacer;
    @FXML
    private Button btnEditarProducto;
    @FXML
    private Button btnEliminarProducto;
    @FXML
    private Button btnAgregarProducto;
    @FXML
    private Button btnAgregarAReserva;

    private ArregloProductos arregloProductos = new ArregloProductos();
    private PilaAccionesProducto pilaDeshacer = new PilaAccionesProducto();
    private PilaAccionesProducto pilaRehacer = new PilaAccionesProducto();
    private ObservableList<Producto> listaObservable = FXCollections.observableArrayList();
    
    private Usuario usuarioLogueado;
    private boolean modoSeleccionMultiple = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarComboOrdenar();
        try {
            cargarDatosDesdeBD();
        } catch (SQLException ex) {
            AlertaUtil.mostrarError("Error al cargar productos: " + ex.getMessage());
            ex.printStackTrace();
        }
        configurarEventos();
        configurarModoSeleccion();
    }


    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }


    @Override
    public void setModoSeleccionMultiple(boolean modo) {
        this.modoSeleccionMultiple = modo;
        configurarModoSeleccion();
    }

    @Override
    public void configurarModoSeleccion() {
        if (modoSeleccionMultiple) {
            btnAgregarAReserva.setVisible(true);
            btnAgregarProducto.setVisible(false);
            btnDeshacer.setVisible(false);
            btnEditarProducto.setVisible(false);
            btnEliminarProducto.setVisible(false);
            btnRehacer.setVisible(false);
            tableProducto.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } else {
            btnAgregarAReserva.setVisible(false);
            btnAgregarProducto.setVisible(true);
            btnDeshacer.setVisible(true);
            btnEditarProducto.setVisible(true);
            btnEliminarProducto.setVisible(true);
            btnRehacer.setVisible(true);
            tableProducto.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
    }

    @Override
    public List<Producto> getSeleccionados() {
        return new ArrayList<>(tableProducto.getSelectionModel().getSelectedItems());
    }


    @FXML
    private void deshacerAccion(ActionEvent event) {
        if (pilaDeshacer.estaVacia()) {
            return;
        }

        AccionUsuario accion = pilaDeshacer.pop();
        pilaRehacer.push(accion);

        switch (accion.getTipoAccion()) {
            case INSERTAR -> {
                arregloProductos.eliminarPorId(accion.getProductoNuevo().getIdProducto());
                listaObservable.remove(accion.getProductoNuevo());
            }
            case ELIMINAR -> {
                arregloProductos.insertar(accion.getProductoAnterior());
                listaObservable.add(accion.getProductoAnterior());
            }
            case EDITAR -> {
                Producto pAnterior = accion.getProductoAnterior();
                for (int i = 0; i < listaObservable.size(); i++) {
                    if (listaObservable.get(i).getIdProducto() == pAnterior.getIdProducto()) {
                        listaObservable.set(i, pAnterior);
                        break;
                    }
                }
            }
        }
        tableProducto.refresh();
    }

    @FXML
    private void rehacerAccion(ActionEvent event) {
        if (pilaRehacer.estaVacia()) {
            return;
        }

        AccionUsuario accion = pilaRehacer.pop();
        pilaDeshacer.push(accion);

        switch (accion.getTipoAccion()) {
            case INSERTAR -> {
                arregloProductos.insertar(accion.getProductoNuevo());
                listaObservable.add(accion.getProductoNuevo());
            }
            case ELIMINAR -> {
                arregloProductos.eliminarPorId(accion.getProductoAnterior().getIdProducto());
                listaObservable.remove(accion.getProductoAnterior());
            }
            case EDITAR -> {
                Producto pNuevo = accion.getProductoNuevo();
                for (int i = 0; i < listaObservable.size(); i++) {
                    if (listaObservable.get(i).getIdProducto() == pNuevo.getIdProducto()) {
                        listaObservable.set(i, pNuevo);
                        break;
                    }
                }
            }
        }
        tableProducto.refresh();
    }

    @FXML
    private void editarProducto(ActionEvent event) {
        if (usuarioLogueado == null || usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden editar productos.");
            return;
        }

        Producto seleccionado = tableProducto.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaUtil.mostrarAdvertencia("Seleccione un producto para editar.");
            return;
        }

        abrirFormulario(new Producto(
                seleccionado.getIdProducto(),
                seleccionado.getNombre(),
                seleccionado.getTipo(),
                seleccionado.getPrecio(),
                seleccionado.getDescripcion(),
                true
        ), true);
    }


    @FXML
    private void eliminarProducto(ActionEvent event) {
        if (usuarioLogueado == null || usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden eliminar productos.");
            return;
        }

        Producto seleccionado = tableProducto.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaUtil.mostrarAdvertencia("Seleccione un producto para eliminar.");
            return;
        }

        if (!AlertaUtil.confirmar("¿Está seguro de eliminar el producto?")) {
            return;
        }

        try {
            ProductoDAO dao = new ProductoDAO();
            if (dao.eliminar(seleccionado.getIdProducto())) {
                arregloProductos.eliminarPorId(seleccionado.getIdProducto());
                pilaDeshacer.push(new AccionUsuario(TipoAccionUsuario.ELIMINAR, null, seleccionado));
                pilaRehacer = new PilaAccionesProducto();
                listaObservable.remove(seleccionado);
                tableProducto.refresh();
                AlertaUtil.mostrarInformacion("Producto eliminado correctamente.");
            } else {
                AlertaUtil.mostrarError("No se pudo eliminar el producto.");
            }
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarProducto(ActionEvent event) {
        if (usuarioLogueado == null || usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR) {
            AlertaUtil.mostrarAdvertencia("Solo los administradores pueden insertar productos.");
            return;
        }

        abrirFormulario(null, false);
    }

    @FXML
    private void agregarProductoAReserva(ActionEvent event) {
        List<Producto> seleccionados = new ArrayList<>(tableProducto.getSelectionModel().getSelectedItems());
        if (!seleccionados.isEmpty()) {
            SeleccionProducto.setProductosSeleccionados(seleccionados);
            cerrarVentana();
        } else {
            AlertaUtil.mostrarAdvertencia("Debe seleccionar al menos un producto.");
        }
    }


    private void configurarTabla() {
        ColumnNumero.setCellValueFactory(cellData -> {
            int index = listaObservable.indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });
        ColumnNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ColumnTipoProducto.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        ColumnDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        ColumnPrecioProducto.setCellValueFactory(new PropertyValueFactory<>("precio"));
        tableProducto.setItems(listaObservable);
    }

  
    private void cargarComboOrdenar() {
        CBoxOrdenar.getItems().addAll("Por ID", "Por Nombre", "Por Precio");
        CBoxOrdenar.getSelectionModel().selectFirst();
    }

    private void configurarEventos() {
        textBuscarPorNombre.textProperty().addListener((obs, oldVal, newVal) -> filtrarPorNombre(newVal));
        CBoxOrdenar.setOnAction(e -> ordenarLista(CBoxOrdenar.getValue()));
    }

    private void cargarDatosDesdeBD() throws SQLException {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.listar();
            listaObservable.clear();
            arregloProductos = new ArregloProductos();
            for (Producto p : productos) {
                arregloProductos.insertar(p);
                listaObservable.add(p);
            }
            tableProducto.refresh();
        } catch (SQLException e) {
            AlertaUtil.mostrarError("Error al cargar productos: " + e.getMessage());
            throw e;
        }
    }

    private void filtrarPorNombre(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tableProducto.setItems(listaObservable);
            return;
        }
        String filtroLower = filtro.toLowerCase();
        List<Producto> filtrados = listaObservable.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(filtroLower))
                .collect(Collectors.toList());
        tableProducto.setItems(FXCollections.observableArrayList(filtrados));
    }

  
    private void ordenarLista(String criterio) {
        Comparator<Producto> comparador = switch (criterio) {
            case "Por Nombre" ->
                Comparator.comparing(Producto::getNombre);
            case "Por Precio" ->
                Comparator.comparingDouble(Producto::getPrecio);
            default ->
                Comparator.comparingInt(Producto::getIdProducto);
        };
        FXCollections.sort(listaObservable, comparador);
        tableProducto.refresh();
    }

    private void abrirFormulario(Producto producto, boolean esEdicion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Formularios/FormularioProducto.fxml"));
            Parent root = loader.load();
            FormularioProductoController controlador = loader.getController();
            controlador.inicializarFormulario(producto, esEdicion);

            Stage stage = new Stage();
            stage.setTitle(esEdicion ? "Editar Producto" : "Nuevo Producto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableProducto.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

            if (controlador.isGuardado()) {
                ProductoDAO dao = new ProductoDAO();
                Producto actualizado;

                if (esEdicion) {
                    actualizado = dao.buscarPorId(producto.getIdProducto());
                    for (int i = 0; i < listaObservable.size(); i++) {
                        if (listaObservable.get(i).getIdProducto() == actualizado.getIdProducto()) {
                            pilaDeshacer.push(new AccionUsuario(TipoAccionUsuario.EDITAR, producto, actualizado));
                            listaObservable.set(i, actualizado);
                            break;
                        }
                    }
                } else {
                    List<Producto> listaBD = dao.listar();
                    actualizado = listaBD.get(listaBD.size() - 1); // Get the last inserted product
                    if (actualizado != null) {
                        arregloProductos.insertar(actualizado);
                        listaObservable.add(actualizado);
                        pilaDeshacer.push(new AccionUsuario(TipoAccionUsuario.INSERTAR, null, actualizado));
                        pilaRehacer = new PilaAccionesProducto();
                        tableProducto.refresh();
                    } else {
                        AlertaUtil.mostrarAdvertencia("No se pudo obtener el producto recién insertado.");
                    }
                }
            }
        } catch (Exception e) {
            AlertaUtil.mostrarError("No se pudo abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

 
    private void cerrarVentana() {
        Stage stage = (Stage) tableProducto.getScene().getWindow();
        stage.close();
    }
}
