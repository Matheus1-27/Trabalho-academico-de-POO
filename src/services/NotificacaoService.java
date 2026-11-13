package services;

import java.time.*;
import java.util.*;
import models.*;
import services.SistemaService.RegraNegocioException;

public final class NotificacaoService {
    private final SistemaService sistema;
    NotificacaoService(SistemaService sistema) { this.sistema=sistema; }

    public void publicarComunicado(Usuario solicitante, String mensagem) throws RegraNegocioException {
        sistema.publicarComunicado(solicitante, mensagem);
    }
}
