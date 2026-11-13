package models;

import java.time.*;
import java.util.*;

public final class Tecnico extends Usuario {
	public Tecnico(String id, String nome, String email, String senha) {
		super(id, nome, email, senha);
	}
	@Override public PerfilUsuario getPerfil() { return PerfilUsuario.TECNICO; }
}
