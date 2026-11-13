package services;

import java.time.*;
import java.util.*;
import models.*;
import services.SistemaService.RegraNegocioException;

public final class CampeonatoService {
    private final SistemaService sistema;
    CampeonatoService(SistemaService sistema) { this.sistema=sistema; }

    public void agendarPartida(Usuario solicitante, String campeonatoId, String partidaId, LocalDateTime data, String espacoId) throws RegraNegocioException {
        sistema.agendarPartida(solicitante, campeonatoId, partidaId, data, espacoId);
    }

    public Campeonato cadastrarCampeonato(Usuario solicitante, String nome, String regulamento, Modalidade modalidade) throws RegraNegocioException {
        return sistema.cadastrarCampeonato(solicitante, nome, regulamento, modalidade);
    }

    public List<String> classificacao(Campeonato campeonato) {
        return sistema.classificacao(campeonato);
    }

    public void definirEscalacao(Tecnico tecnico, String campeonatoId, String partidaId, String equipeId, List<String> atletasIds) throws RegraNegocioException {
        sistema.definirEscalacao(tecnico, campeonatoId, partidaId, equipeId, atletasIds);
    }

    public void editarCampeonato(Usuario solicitante, String id, String nome, String regulamento, Modalidade modalidade) throws RegraNegocioException {
        sistema.editarCampeonato(solicitante, id, nome, regulamento, modalidade);
    }

    public void encerrarCampeonato(Usuario solicitante, String id) throws RegraNegocioException {
        sistema.encerrarCampeonato(solicitante, id);
    }

    public void gerarTabelaJogos(Usuario solicitante, String campeonatoId, LocalDateTime data) throws RegraNegocioException {
        sistema.gerarTabelaJogos(solicitante, campeonatoId, data);
    }

    public List<Campeonato> listarCampeonatos() {
        return sistema.listarCampeonatos();
    }

    public List<InscricaoCampeonato> listarInscricoesCampeonato() {
        return sistema.listarInscricoesCampeonato();
    }

    public List<Partida> listarPartidas() {
        return sistema.listarPartidas();
    }

    public String nomeCampeonato(String id) {
        return sistema.nomeCampeonato(id);
    }

    public void registrarPlacar(Tecnico tecnico, String campeonatoId, String partidaId, int casa, int visitante) throws RegraNegocioException {
        sistema.registrarPlacar(tecnico, campeonatoId, partidaId, casa, visitante);
    }

    public void removerCampeonato(Usuario solicitante, String id) throws RegraNegocioException {
        sistema.removerCampeonato(solicitante, id);
    }

    public void responderInscricaoCampeonato(Usuario solicitante, String inscricaoId, boolean aprovar) throws RegraNegocioException {
        sistema.responderInscricaoCampeonato(solicitante, inscricaoId, aprovar);
    }

    public void solicitarInscricaoCampeonato(Tecnico solicitante, String equipeId, String campeonatoId) throws RegraNegocioException {
        sistema.solicitarInscricaoCampeonato(solicitante, equipeId, campeonatoId);
    }
}
