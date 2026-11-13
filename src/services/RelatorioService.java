package services;

import java.time.*;
import java.util.*;
import models.*;
import services.SistemaService.RegraNegocioException;

public final class RelatorioService {
    private final SistemaService sistema;
    RelatorioService(SistemaService sistema) { this.sistema=sistema; }

    public String relatorioGeral(Usuario solicitante) throws RegraNegocioException {
        return sistema.relatorioGeral(solicitante);
    }
}
