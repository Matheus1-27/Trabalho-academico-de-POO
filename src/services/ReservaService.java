package services;

import java.time.*;
import java.util.*;
import models.*;
import services.SistemaService.RegraNegocioException;

public final class ReservaService {
    private final SistemaService sistema;
    ReservaService(SistemaService sistema) { this.sistema=sistema; }

    public List<Reserva> listarReservas() {
        return sistema.listarReservas();
    }

    public void responderReserva(Usuario solicitante, String reservaId, boolean aprovar) throws RegraNegocioException {
        sistema.responderReserva(solicitante, reservaId, aprovar);
    }

    public void solicitarReserva(Tecnico tecnico, String equipeId, String espacoId, LocalDateTime inicio, LocalDateTime fim) throws RegraNegocioException {
        sistema.solicitarReserva(tecnico, equipeId, espacoId, inicio, fim);
    }
}
