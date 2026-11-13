package services;

import java.time.*;
import java.util.*;
import models.*;
import services.SistemaService.RegraNegocioException;

public final class UsuarioService {
    private final SistemaService sistema;
    UsuarioService(SistemaService sistema) { this.sistema=sistema; }

    public void alterarSuspensao(Usuario solicitante, String atletaId, boolean suspenso) throws RegraNegocioException {
        sistema.alterarSuspensao(solicitante, atletaId, suspenso);
    }

    public Usuario autenticar(String email, char[] senha) throws RegraNegocioException {
        return sistema.autenticar(email, senha);
    }

    public Usuario cadastrarUsuario(PerfilUsuario perfil, String nome, String email, char[] senha, Modalidade modalidade) throws RegraNegocioException {
        return sistema.cadastrarUsuario(perfil, nome, email, senha, modalidade);
    }

    public List<String> historicoAtleta(Atleta atleta) {
        return sistema.historicoAtleta(atleta);
    }

    public List<Atleta> listarAtletas() {
        return sistema.listarAtletas();
    }

    public List<Notificacao> listarNotificacoes(Usuario usuario) {
        return sistema.listarNotificacoes(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return sistema.listarUsuarios();
    }

    public void marcarNotificacaoLida(Usuario usuario, String notificacaoId) throws RegraNegocioException {
        sistema.marcarNotificacaoLida(usuario, notificacaoId);
    }

    public String nomeUsuario(String id) {
        return sistema.nomeUsuario(id);
    }

    public List<Partida> partidasDoAtleta(Atleta atleta) {
        return sistema.partidasDoAtleta(atleta);
    }
}
