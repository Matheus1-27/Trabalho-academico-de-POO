package models;

import java.time.*;
import java.util.*;

public final class EspacoEsportivo implements Identificavel {
	private final String id;
	private String nome, localizacao, horarios;
	private TipoEspaco tipo;
	private StatusEspaco status;
	private int capacidade;

	public EspacoEsportivo(String id, String nome, TipoEspaco tipo, String localizacao,
			int capacidade, String horarios, StatusEspaco status) {
		this.id = id;
		this.nome = nome;
		this.tipo = tipo;
		this.localizacao = localizacao;
		this.capacidade = capacidade;
		this.horarios = horarios;
		this.status = status;
	}

	@Override public String getId() { return id; }
	public String getNome() { return nome; }
	public void setNome(String valor) { nome = valor; }
	public TipoEspaco getTipo() { return tipo; }
	public void setTipo(TipoEspaco valor) { tipo = valor; }
	public String getLocalizacao() { return localizacao; }
	public void setLocalizacao(String valor) { localizacao = valor; }
	public int getCapacidade() { return capacidade; }
	public void setCapacidade(int valor) { capacidade = valor; }
	public String getHorarios() { return horarios; }
	public void setHorarios(String valor) { horarios = valor; }
	public StatusEspaco getStatus() { return status; }
	public void setStatus(StatusEspaco valor) { status = valor; }
}
