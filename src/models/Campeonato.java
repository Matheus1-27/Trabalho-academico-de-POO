package models;

import java.time.*;
import java.util.*;

public final class Campeonato implements Identificavel {
	private final String id;
	private final Set<String> equipes = new LinkedHashSet<>();
	private final List<Partida> partidas = new ArrayList<>();
	private String nome, regulamento;
	private Modalidade modalidade;
	private StatusCampeonato status = StatusCampeonato.ABERTO;

	public Campeonato(String id, String nome, String regulamento, Modalidade modalidade) {
		this.id = id;
		this.nome = nome;
		this.regulamento = regulamento;
		this.modalidade = modalidade;
	}

	@Override public String getId() { return id; }
	public String getNome() { return nome; }
	public void setNome(String valor) { nome = valor; }
	public String getRegulamento() { return regulamento; }
	public void setRegulamento(String valor) { regulamento = valor; }
	public Modalidade getModalidade() { return modalidade; }
	public void setModalidade(Modalidade valor) { modalidade = valor; }
	public StatusCampeonato getStatus() { return status; }
	public void setStatus(StatusCampeonato valor) { status = valor; }
	public Set<String> getEquipesIds() { return new LinkedHashSet<>(equipes); }
	public void adicionarEquipe(String id) { equipes.add(id); }
	public List<Partida> getPartidas() { return new ArrayList<>(partidas); }
	public void adicionarPartida(Partida partida) { partidas.add(partida); }
}
