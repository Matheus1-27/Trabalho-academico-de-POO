package models;

import java.time.*;
import java.util.*;

public final class Reserva implements Identificavel {
	private final String id, equipeId, espacoId;
	private final LocalDateTime inicio, fim;
	private StatusReserva status = StatusReserva.PENDENTE;

	public Reserva(String id, String equipeId, String espacoId, LocalDateTime inicio, LocalDateTime fim) {
		this.id = id;
		this.equipeId = equipeId;
		this.espacoId = espacoId;
		this.inicio = inicio;
		this.fim = fim;
	}

	@Override public String getId() { return id; }
	public String getEquipeId() { return equipeId; }
	public String getEspacoId() { return espacoId; }
	public LocalDateTime getInicio() { return inicio; }
	public LocalDateTime getFim() { return fim; }
	public StatusReserva getStatus() { return status; }
	public void setStatus(StatusReserva valor) { status = valor; }
}
