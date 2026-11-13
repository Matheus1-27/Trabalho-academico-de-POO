package models;

import java.time.*;
import java.util.*;

public final class InscricaoCampeonato implements Identificavel {
	private final String id, equipeId, campeonatoId;
	private SituacaoInscricao situacao = SituacaoInscricao.PENDENTE;

	public InscricaoCampeonato(String id, String equipeId, String campeonatoId) {
		this.id = id;
		this.equipeId = equipeId;
		this.campeonatoId = campeonatoId;
	}

	@Override public String getId() { return id; }
	public String getEquipeId() { return equipeId; }
	public String getCampeonatoId() { return campeonatoId; }
	public SituacaoInscricao getSituacao() { return situacao; }
	public void setSituacao(SituacaoInscricao valor) { situacao = valor; }
}
