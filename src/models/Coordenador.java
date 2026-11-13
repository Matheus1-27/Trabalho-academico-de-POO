package models;

import java.time.*;
import java.util.*;

public final class Coordenador extends Usuario {
	public Coordenador(String id, String nome, String email, String senha) {
		super(id, nome, email, senha);
	}
	@Override public PerfilUsuario getPerfil() { return PerfilUsuario.COORDENADOR; }
}
