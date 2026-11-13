package models;

import java.time.*;
import java.util.*;

public final class Notificacao implements Identificavel {
	private final String id, usuarioId, mensagem;
	private final LocalDateTime criadaEm;
	private boolean lida;

	public Notificacao(String id, String usuarioId, String mensagem, LocalDateTime criadaEm) {
		this.id = id;
		this.usuarioId = usuarioId;
		this.mensagem = mensagem;
		this.criadaEm = criadaEm;
	}

	@Override public String getId() { return id; }
	public String getUsuarioId() { return usuarioId; }
	public String getMensagem() { return mensagem; }
	public LocalDateTime getCriadaEm() { return criadaEm; }
	public boolean isLida() { return lida; }
	public void marcarComoLida() { lida = true; }
}
