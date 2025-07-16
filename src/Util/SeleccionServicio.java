package Util;


import Modelo.Servicio;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class SeleccionServicio {

    private static List<Servicio> serviciosSeleccionados;

    public static List<Servicio> getServiciosSeleccionados() {
        return serviciosSeleccionados;
    }

    public static void setServiciosSeleccionados(List<Servicio> servicios) {
        serviciosSeleccionados = servicios;
    }

    public static void limpiarSeleccion() {
        serviciosSeleccionados = null;
    }
}
