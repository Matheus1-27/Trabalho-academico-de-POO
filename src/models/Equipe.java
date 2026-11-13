package models;

import java.time.*;
import java.util.*;

public final class Equipe implements Identificavel {
	private final String id, tecnicoId;
	private final Modalidade modalidade;
	private final Set<String> atletas = new LinkedHashSet<>();
	private final List<String> historico = new ArrayList<>();
	private String nome;
	private boolean ativa = true;

	public Equipe(String id, String nome, Modalidade modalidade, String tecnicoId) {
		this.id = id;
		this.nome = nome;
		this.modalidade = modalidade;
		this.tecnicoId = tecnicoId;
	}

	@Override public String getId() { return id; }
	public String getNome() { return nome; }
	public void setNome(String valor) { nome = valor; }
	public Modalidade getModalidade() { return modalidade; }
	public String getTecnicoId() { return tecnicoId; }
	public Set<String> getAtletasIds() { return new LinkedHashSet<>(atletas); }
	public void adicionarAtleta(String id) { atletas.add(id); }
	public void removerAtleta(String id) { atletas.remove(id); }
	public void registrarParticipacao(String evento) { historico.add(evento); }
	public List<String> getHistoricoParticipacao() { return new ArrayList<>(historico); }
	public boolean isAtiva() { return ativa; }
	public void setAtiva(boolean valor) { ativa = valor; }
}
