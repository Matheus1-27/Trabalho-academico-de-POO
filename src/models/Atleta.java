package models;

import java.time.*;
import java.util.*;

public final class Atleta extends Usuario {
	private final Modalidade modalidadeFavorita;
	private final List<String> historico = new ArrayList<>();
	private boolean suspenso;

	public Atleta(String id, String nome, String email, String senha, Modalidade modalidade) {
		super(id, nome, email, senha);
		modalidadeFavorita = modalidade;
	}

	@Override public PerfilUsuario getPerfil() { return PerfilUsuario.ATLETA; }
	public Modalidade getModalidadeFavorita() { return modalidadeFavorita; }
	public int getNivelDesempenho() { return 1; }
	public boolean isSuspenso() { return suspenso; }
	public void setSuspenso(boolean valor) { suspenso = valor; }
	public List<String> getHistorico() { return new ArrayList<>(historico); }
	public void registrarHistorico(String evento) { historico.add(evento); }
}
