package Util;

import Modelo.DetalleReserva;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author Wilian Lopez
 */
public class CeldaCantidadConBotones extends TableCell<DetalleReserva, Integer> {

    private final Button btnMenos = new Button("-");
    private final Button btnMas = new Button("+");
    private final Label lblCantidad = new Label();
    private final HBox contenedor = new HBox(10);

    public CeldaCantidadConBotones() {
        btnMenos.setStyle(
                "-fx-background-color: #FFCDD2;"
                + // rojo pálido
                "-fx-text-fill: #b71c1c;"
                + "-fx-font-size: 10px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 2 6;"
                + "-fx-background-radius: 10;"
        );

        btnMas.setStyle(
                "-fx-background-color: #C8E6C9;"
                + // verde pálido
                "-fx-text-fill: #1b5e20;"
                + "-fx-font-size: 10px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 2 6;"
                + "-fx-background-radius: 10;"
        );
        lblCantidad.setFont(new Font("Arial", 12));
        lblCantidad.setTextFill(Color.BLACK);

        contenedor.getChildren().addAll(btnMenos, lblCantidad, btnMas);
        contenedor.setAlignment(Pos.CENTER);

        // Eventos
        btnMas.setOnAction(e -> {
            DetalleReserva detalle = getTableView().getItems().get(getIndex());
            detalle.setCantidad(detalle.getCantidad() + 1);
            actualizarVista(detalle);
        });

        btnMenos.setOnAction(e -> {
            DetalleReserva detalle = getTableView().getItems().get(getIndex());
            if (detalle.getCantidad() > 0) {
                detalle.setCantidad(detalle.getCantidad() - 1);

                if (detalle.getCantidad() == 0) {
                    getTableView().getItems().remove(detalle);
                }

                getTableView().refresh();
            }
        });
    }

    private void actualizarVista(DetalleReserva detalle) {
        lblCantidad.setText(String.valueOf(detalle.getCantidad()));
        getTableView().refresh(); // Actualiza el subtotal en su columna
    }

    @Override
    protected void updateItem(Integer cantidad, boolean empty) {
        super.updateItem(cantidad, empty);
        if (empty || getIndex() >= getTableView().getItems().size()) {
            setGraphic(null);
        } else {
            DetalleReserva detalle = getTableView().getItems().get(getIndex());
            lblCantidad.setText(String.valueOf(detalle.getCantidad()));
            setGraphic(contenedor);
        }
    }
}
