package Util;

import java.util.Optional;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

/**
 *
 * @author Wilian Lopez
 */
public class AlertaUtil {

    public static void mostrarError(String mensaje) {
        mostrarAlerta("Error", mensaje, AlertType.ERROR);
    }

    public static void mostrarInformacion(String mensaje) {
        mostrarAlerta("Información", mensaje, AlertType.INFORMATION);
    }

    public static void mostrarAdvertencia(String mensaje) {
        mostrarAlerta("Advertencia", mensaje, AlertType.WARNING);
    }

    public static void mostrarExitoTemporal(String mensaje, int segundos) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(segundos));
        delay.setOnFinished(e -> alert.close());
        delay.play();
    }

    public static boolean confirmar(String mensaje) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private static void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
