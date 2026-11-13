package models;

import java.time.*;
import java.util.*;

public final class Partida implements Identificavel {
	private final String id, equipeCasaId, equipeVisitanteId;
	private final List<String> escalacaoCasa = new ArrayList<>(), escalacaoVisitante = new ArrayList<>();
	private LocalDateTime dataHora;
	private String espacoId;
	private Integer golsCasa, golsVisitante;

	public Partida(String id, String casa, String visitante, LocalDateTime dataHora) {
		this.id = id;
		equipeCasaId = casa;
		equipeVisitanteId = visitante;
		this.dataHora = dataHora;
	}

	@Override public String getId() { return id; }
	public String getEquipeCasaId() { return equipeCasaId; }
	public String getEquipeVisitanteId() { return equipeVisitanteId; }
	public LocalDateTime getDataHora() { return dataHora; }
	public void setDataHora(LocalDateTime valor) { dataHora = valor; }
	public String getEspacoId() { return espacoId; }
	public void setEspacoId(String valor) { espacoId = valor; }
	public Integer getGolsCasa() { return golsCasa; }
	public Integer getGolsVisitante() { return golsVisitante; }
	public void registrarPlacar(int casa, int visitante) { golsCasa = casa; golsVisitante = visitante; }
	public boolean isRealizada() { return golsCasa != null; }
	public List<String> getEscalacaoCasa() { return new ArrayList<>(escalacaoCasa); }
	public List<String> getEscalacaoVisitante() { return new ArrayList<>(escalacaoVisitante); }
	public void definirEscalacaoCasa(List<String> ids) { escalacaoCasa.clear(); escalacaoCasa.addAll(ids); }
	public void definirEscalacaoVisitante(List<String> ids) {
		escalacaoVisitante.clear(); escalacaoVisitante.addAll(ids);
	}
}
