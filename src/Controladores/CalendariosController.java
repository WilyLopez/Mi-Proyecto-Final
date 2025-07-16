package Controladores;

import CRUD.ReservaDAO;
import Estructuras.AVLReservas;
import Modelo.Reserva;
import Util.AlertaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CalendariosController implements Initializable {

    @FXML
    private Button btnMesAnterior;
    @FXML
    private TextField textMes;
    @FXML
    private Button btnMesSiguiente;
    @FXML
    private HBox HBoxContenedorGrigPane;

    private GridPane gridPane;
    private YearMonth mesActual;
    private final DateTimeFormatter mesFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private AVLReservas arbolReservas;
    private ReservaDAO reservaDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mesActual = YearMonth.now();
        arbolReservas = new AVLReservas();
        try {
            reservaDAO = new ReservaDAO();
            for (Reserva r : reservaDAO.listarReservas()) {
                arbolReservas.insertar(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarError("Error cargando reservas");
        }

        gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));
        gridPane.setPrefWidth(700);
        gridPane.setPrefHeight(500);
        gridPane.getStyleClass().add("grid-pane");

        HBox.setHgrow(gridPane, Priority.ALWAYS);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        HBoxContenedorGrigPane.getChildren().add(gridPane);

        textMes.getStyleClass().add("text-field");

        mostrarCalendario();
    }

    @FXML
    private void mostrarMesAnterior(ActionEvent event) {
        mesActual = mesActual.minusMonths(1);
        mostrarCalendario();
    }

    @FXML
    private void mostraMesSiguiente(ActionEvent event) {
        mesActual = mesActual.plusMonths(1);
        mostrarCalendario();
    }

    private void mostrarCalendario() {
        gridPane.getChildren().clear();

        textMes.setText(capitalizar(mesActual.format(mesFormatter)));

        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (int i = 0; i < diasSemana.length; i++) {
            Label dia = new Label(diasSemana[i]);
            dia.getStyleClass().add("dias-semana");
            gridPane.add(dia, i, 0);
        }

        LocalDate primerDiaMes = mesActual.atDay(1);
        int inicioColumna = primerDiaMes.getDayOfWeek().getValue() % 7; // Lunes = 1
        int diasMes = mesActual.lengthOfMonth();

        int fila = 1;
        int columna = inicioColumna;

        for (int dia = 1; dia <= diasMes; dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            LocalDateTime desde = fecha.atStartOfDay();
            LocalDateTime hasta = fecha.plusDays(1).atStartOfDay();

            List<Reserva> reservasDelDia = reservaDAO.listarReservas().stream()
                    .filter(r -> !r.getFechaHoraEntrada().isBefore(desde) && r.getFechaHoraEntrada().isBefore(hasta))
                    .toList();

            VBox celda = new VBox();
            celda.setSpacing(4);
            celda.setPadding(new Insets(5));
            celda.setPrefSize(90, 70);
            celda.getStyleClass().add("calendario-celda");

            if (fecha.equals(LocalDate.now())) {
                celda.getStyleClass().add("hoy");
            }

            Label lblDia = new Label(String.valueOf(dia));
            lblDia.getStyleClass().add("label-dia");
            celda.getChildren().add(lblDia);

            if (!reservasDelDia.isEmpty()) {
                Label lblReservas = new Label(reservasDelDia.size() + " reservas");
                lblReservas.getStyleClass().add("label-reservas");
                celda.getStyleClass().add("con-reservas");
                celda.getChildren().add(lblReservas);
            }

            celda.setOnMouseClicked(e -> {
                if (!reservasDelDia.isEmpty()) {
                    mostrarReservasDelDia(fecha, reservasDelDia);
                }
            });

            GridPane.setHgrow(celda, Priority.ALWAYS);
            GridPane.setVgrow(celda, Priority.ALWAYS);

            gridPane.add(celda, columna, fila);
            columna++;

            if (columna > 6) {
                columna = 0;
                fila++;
            }
        }
    }

    private void mostrarReservasDelDia(LocalDate fecha, List<Reserva> reservas) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reservas para el ").append(fecha).append(":\n");
        for (Reserva r : reservas) {
            sb.append("\u2022 Habitación ").append(r.getNumeroHabitacion())
                    .append(" - DNI: ").append(r.getDniHuesped())
                    .append("\n");
        }
        AlertaUtil.mostrarInformacion(sb.toString());
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}
