package Util;

import CRUD.ReservaDAO;
import CRUD.GananciaDAO;
import Modelo.Ganancia;
import Modelo.Reserva;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Wilian Lopez
 */
public class GeneradorGanancias {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final GananciaDAO gananciaDAO = new GananciaDAO();

    public void generarGananciasDesdeReservas() {
        List<Reserva> reservas = reservaDAO.obtenerReservasSinGanancia();

        for (Reserva r : reservas) {
            LocalDate fechaGanancia = r.getFechaHoraEntrada().toLocalDate();
            double total = r.getTotal();

            Ganancia g = new Ganancia(fechaGanancia, total, "Reserva #" + r.getIdReserva());
            g.setReservaId(r.getIdReserva());

            boolean insertado = gananciaDAO.insertarGanancia(g);

            if (insertado) {
                System.out.println("Ganancia generada para Reserva ID: " + r.getIdReserva());
            } else {
                System.out.println("Error al generar ganancia para Reserva ID: " + r.getIdReserva());
            }
        }
    }

}
