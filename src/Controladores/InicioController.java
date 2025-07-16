package Controladores;

import CRUD.HabitacionesDAO;
import CRUD.ReservaDAO;
import Enums.EstadoHabitacion;
import Modelo.Habitacion;
import Modelo.Reserva;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.scene.chart.NumberAxis;

public class InicioController implements Initializable {

    @FXML
    private AnchorPane AnchorPaneInicio;
    @FXML
    private TextField textFechaHoy;
    @FXML
    private TextField textNumeroHabitacionesTotales;
    @FXML
    private TextField textNumeroHabitacionesLimpieza;
    @FXML
    private TextField textNumeroHabitacionesOcupadas;
    @FXML
    private TextField textNumeroHabitacionesDisponibles;
    @FXML
    private TextField textNumeroHabitacionesLibres;
    @FXML
    private TextField totalReservasHoy;
    @FXML
    private LineChart<String, Number> LineChart;
    @FXML
    private HBox contenedorGridPaneHabitaciones;
    @FXML
    private VBox contenedorLeyendaHabitaciones;

    private HabitacionesDAO habitacionesDAO;
    private ReservaDAO reservaDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        habitacionesDAO = new HabitacionesDAO();
        reservaDAO = new ReservaDAO();

        textFechaHoy.setText(formatearFecha(LocalDate.now()));
        cargarResumenHabitaciones();
        cargarReservasHoy();
        cargarLineaTiempoReservas();
        construirGridHabitaciones();
        construirLeyenda();
    }

    private void cargarResumenHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionesDAO.listarHabitaciones();
            long total = habitaciones.size();
            long ocupadas = habitaciones.stream().filter(h -> h.getEstado() == EstadoHabitacion.OCUPADO).count();
            long libres = habitaciones.stream().filter(h -> h.getEstado() == EstadoHabitacion.LIBRE).count();
            long limpieza = habitaciones.stream().filter(h -> h.getEstado() == EstadoHabitacion.EN_LIMPIEZA).count();
            long mantenimiento = habitaciones.stream().filter(h -> h.getEstado() == EstadoHabitacion.MANTENIMIENTO).count();

            textNumeroHabitacionesTotales.setText(String.valueOf(total));
            textNumeroHabitacionesOcupadas.setText(String.valueOf(ocupadas));
            textNumeroHabitacionesLibres.setText(String.valueOf(libres));
            textNumeroHabitacionesLimpieza.setText(String.valueOf(limpieza + mantenimiento));
            textNumeroHabitacionesDisponibles.setText(String.valueOf(libres));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarReservasHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            List<Reserva> reservas = reservaDAO.listarReservas();
            long reservasHoy = reservas.stream()
                    .filter(r -> r.getFechaHoraEntrada().toLocalDate().isEqual(hoy))
                    .count();
            totalReservasHoy.setText(String.valueOf(reservasHoy));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarLineaTiempoReservas() {
        try {
            
            List<Reserva> reservas = reservaDAO.listarReservas();
            Map<LocalDate, Long> reservasPorDia = new LinkedHashMap<>();

            LocalDate hoy = LocalDate.now();
            DateTimeFormatter diaFormatter = DateTimeFormatter.ofPattern("EEE", new Locale("es"));

            for (int i = 6; i >= 0; i--) {
                reservasPorDia.put(hoy.minusDays(i), 0L);
            }

            for (Reserva r : reservas) {
                LocalDate fecha = r.getFechaHoraEntrada().toLocalDate();
                reservasPorDia.computeIfPresent(fecha, (k, v) -> v + 1);
            }

            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Reservas");

            for (Map.Entry<LocalDate, Long> entry : reservasPorDia.entrySet()) {
                String dia = diaFormatter.format(entry.getKey()).substring(0, 1).toUpperCase()
                        + diaFormatter.format(entry.getKey()).substring(1);
                serie.getData().add(new XYChart.Data<>(dia, entry.getValue()));
            }

            LineChart.getData().clear();
            LineChart.getData().add(serie);
            LineChart.getStyleClass().add("line-chart-reservas");


            NumberAxis yAxis = (NumberAxis) LineChart.getYAxis();
            yAxis.setTickUnit(1);
            yAxis.setMinorTickVisible(false);
            yAxis.setForceZeroInRange(true);
            yAxis.setAutoRanging(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void construirGridHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionesDAO.listarHabitaciones();
            contenedorGridPaneHabitaciones.getChildren().clear();

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            VBox.setVgrow(grid, Priority.ALWAYS);
            grid.setMaxWidth(Double.MAX_VALUE);
            

            Label titulo = new Label("Estado de habitaciones");
            titulo.getStyleClass().add("titulo-grid-habitaciones");
            grid.add(titulo, 0, 0, 6, 1);

            int columnas = 6;

            for (int i = 0; i < habitaciones.size(); i++) {
                Habitacion h = habitaciones.get(i);
                Label label = crearEtiquetaHabitacion(h);
                int col = i % columnas;
                int row = (i / columnas) + 1;
                grid.add(label, col, row);
            }

            contenedorGridPaneHabitaciones.getChildren().add(grid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Label crearEtiquetaHabitacion(Habitacion habitacion) {
        Label label = new Label(" " + habitacion.getNumero() + " ");
        label.getStyleClass().add("etiqueta-habitacion");

        switch (habitacion.getEstado()) {
            case LIBRE ->
                label.getStyleClass().add("habitacion-libre");
            case OCUPADO ->
                label.getStyleClass().add("habitacion-ocupada");
            case EN_LIMPIEZA ->
                label.getStyleClass().add("habitacion-limpieza");
            case MANTENIMIENTO ->
                label.getStyleClass().add("habitacion-mantenimiento");
        }

        return label;
    }

    private void construirLeyenda() {
        contenedorLeyendaHabitaciones.getChildren().clear();

        Label titulo = new Label("Leyenda de estados");
        titulo.getStyleClass().add("titulo-grid-habitaciones");
        contenedorLeyendaHabitaciones.getChildren().add(titulo);

        agregarLeyenda("Libre", Color.web("#2ecc71"));
        agregarLeyenda("Ocupada", Color.web("#e74c3c"));
        agregarLeyenda("En Limpieza", Color.web("#f1c40f"));
        agregarLeyenda("Mantenimiento", Color.web("#7f8c8d"));
    }

    private void agregarLeyenda(String texto, Color color) {
        HBox box = new HBox(5);
        box.getStyleClass().add("leyenda-item");
        
        Rectangle rect = new Rectangle(15, 15);
        rect.setFill(color);
        rect.getStyleClass().add("rectangle-leyenda");

        Label label = new Label(texto);
        label.getStyleClass().add("leyenda-texto");

        box.getChildren().addAll(rect, label);
        contenedorLeyendaHabitaciones.getChildren().add(box);
    }

    private static String formatearFecha(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }
}
