package services;

import java.time.*;
import java.util.*;
import models.*;
import services.SistemaService.RegraNegocioException;

public final class EspacoService {
    private final SistemaService sistema;
    EspacoService(SistemaService sistema) { this.sistema=sistema; }

    public List<EspacoEsportivo> buscarEspacos(String termo, TipoEspaco tipo, StatusEspaco status) {
        return sistema.buscarEspacos(termo, tipo, status);
    }

    public EspacoEsportivo cadastrarEspaco(Usuario solicitante, String nome, TipoEspaco tipo, String localizacao, int capacidade, String horarios, StatusEspaco status) throws RegraNegocioException {
        return sistema.cadastrarEspaco(solicitante, nome, tipo, localizacao, capacidade, horarios, status);
    }

    public void editarEspaco(Usuario solicitante, String id, String nome, TipoEspaco tipo, String localizacao, int capacidade, String horarios, StatusEspaco status) throws RegraNegocioException {
        sistema.editarEspaco(solicitante, id, nome, tipo, localizacao, capacidade, horarios, status);
    }

    public List<EspacoEsportivo> listarEspacos() {
        return sistema.listarEspacos();
    }

    public String nomeEspaco(String id) {
        return sistema.nomeEspaco(id);
    }

    public void removerEspaco(Usuario solicitante, String id) throws RegraNegocioException {
        sistema.removerEspaco(solicitante, id);
    }
}
