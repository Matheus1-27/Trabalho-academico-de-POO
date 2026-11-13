package models;

import java.time.*;
import java.util.*;

public abstract class Usuario implements Identificavel {
	private final String id, nome, email, senha;

	protected Usuario(String id, String nome, String email, String senha) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
	}

	@Override public final String getId() { return id; }
	public String getNome() { return nome; }
	public String getEmail() { return email; }
	public final boolean conferirSenha(char[] informada) { return new String(informada).equals(senha); }
	public abstract PerfilUsuario getPerfil();
}
