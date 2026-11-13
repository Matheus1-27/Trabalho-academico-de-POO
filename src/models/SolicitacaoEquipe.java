package models;

import java.time.*;
import java.util.*;

public final class SolicitacaoEquipe implements Identificavel {
	private final String id, atletaId, equipeId;
	private final LocalDateTime criadaEm;
	private EstadoSolicitacao estado = EstadoSolicitacao.PENDENTE;

	public SolicitacaoEquipe(String id, String atletaId, String equipeId, LocalDateTime criadaEm) {
		this.id = id;
		this.atletaId = atletaId;
		this.equipeId = equipeId;
		this.criadaEm = criadaEm;
	}

	@Override public String getId() { return id; }
	public String getAtletaId() { return atletaId; }
	public String getEquipeId() { return equipeId; }
	public LocalDateTime getCriadaEm() { return criadaEm; }
	public EstadoSolicitacao getEstado() { return estado; }
	public void setEstado(EstadoSolicitacao valor) { estado = valor; }
}
