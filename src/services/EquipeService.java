package services;

import java.time.*;
import java.util.*;
import models.*;
import services.SistemaService.RegraNegocioException;

public final class EquipeService {
    private final SistemaService sistema;
    EquipeService(SistemaService sistema) { this.sistema=sistema; }

    public void alterarStatusEquipe(Tecnico tecnico, String equipeId, boolean ativa) throws RegraNegocioException {
        sistema.alterarStatusEquipe(tecnico, equipeId, ativa);
    }

    public Equipe criarEquipe(Tecnico tecnico, String nome, Modalidade modalidade) throws RegraNegocioException {
        return sistema.criarEquipe(tecnico, nome, modalidade);
    }

    public void editarEquipe(Tecnico tecnico, String equipeId, String nome) throws RegraNegocioException {
        sistema.editarEquipe(tecnico, equipeId, nome);
    }

    public List<Equipe> listarEquipes() {
        return sistema.listarEquipes();
    }

    public List<Equipe> listarEquipesDoTecnico(Tecnico tecnico) {
        return sistema.listarEquipesDoTecnico(tecnico);
    }

    public List<SolicitacaoEquipe> listarSolicitacoesEquipe() {
        return sistema.listarSolicitacoesEquipe();
    }

    public String nomeEquipe(String id) {
        return sistema.nomeEquipe(id);
    }

    public void removerAtleta(Tecnico tecnico, String equipeId, String atletaId) throws RegraNegocioException {
        sistema.removerAtleta(tecnico, equipeId, atletaId);
    }

    public void responderSolicitacaoEquipe(Tecnico tecnico, String pedidoId, boolean aprovar) throws RegraNegocioException {
        sistema.responderSolicitacaoEquipe(tecnico, pedidoId, aprovar);
    }

    public void solicitarParticipacao(Atleta atleta, String equipeId) throws RegraNegocioException {
        sistema.solicitarParticipacao(atleta, equipeId);
    }
}
