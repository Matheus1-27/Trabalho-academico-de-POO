package views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import models.*;
import services.SistemaService;
import services.SistemaService.RegraNegocioException;

public final class Views {
	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static SistemaService sistema;
	private static Usuario usuario;

	private Views() {
	}

	public static void iniciar(SistemaService servico) {
		sistema = servico;
		SwingUtilities.invokeLater(Views::login);
	}

	public static void TelaLogin() {
		login();
	}

	public static void TeladeCadastro() {
		cadastrar();
	}

	private static void login() {
		while (true) {
			String[] opcoes = { "Entrar", "Cadastrar", "Sair" };
			int opcao = JOptionPane.showOptionDialog(null, "Sistema de gerenciamento esportivo",
					"Login", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
			if (opcao == 2 || opcao == JOptionPane.CLOSED_OPTION) {
				return;
			}
			if (opcao == 1) {
				cadastrar();
			} else if (opcao == 0) {
				entrar();
				if (usuario != null) {
					menuPrincipal();
					usuario = null;
				}
			}
		}
	}

	private static void cadastrar() {
		JTextField nome = new JTextField();
		JTextField email = new JTextField();
		JPasswordField senha = new JPasswordField();
		JComboBox<PerfilUsuario> perfil = new JComboBox<>(PerfilUsuario.values());
		JComboBox<Modalidade> modalidade = new JComboBox<>(Modalidade.values());
		Object[] campos = { "Nome", nome, "E-mail", email, "Senha (min. 6 caracteres)", senha,
				"Perfil", perfil, "Modalidade favorita (atleta)", modalidade };
		if (JOptionPane.showConfirmDialog(null, campos, "Criar conta", JOptionPane.OK_CANCEL_OPTION)
				!= JOptionPane.OK_OPTION) {
			return;
		}
		char[] senhaDigitada = senha.getPassword();
		try {
			sistema.getUsuarioService().cadastrarUsuario((PerfilUsuario) perfil.getSelectedItem(), nome.getText(), email.getText(),
					senhaDigitada, (Modalidade) modalidade.getSelectedItem());
			mensagem("Conta criada. Agora voce pode entrar.");
		} catch (RegraNegocioException erro) {
			erro(erro);
		} finally {
			Arrays.fill(senhaDigitada, '\0');
			senha.setText("");
		}
	}

	private static void entrar() {
		JTextField email = new JTextField();
		JPasswordField senha = new JPasswordField();
		Object[] campos = { "E-mail", email, "Senha", senha };
		if (JOptionPane.showConfirmDialog(null, campos, "Entrar", JOptionPane.OK_CANCEL_OPTION)
				!= JOptionPane.OK_OPTION) {
			return;
		}
		char[] senhaDigitada = senha.getPassword();
		try {
			usuario = sistema.getUsuarioService().autenticar(email.getText(), senhaDigitada);
		} catch (RegraNegocioException erro) {
			erro(erro);
		} finally {
			Arrays.fill(senhaDigitada, '\0');
			senha.setText("");
		}
	}

	private static void menuPrincipal() {
		while (usuario != null) {
			String[] opcoes = opcoesDoPerfil();
			int escolha = JOptionPane.showOptionDialog(null, "Ola, " + usuario.getNome()
					+ " (" + usuario.getPerfil() + ")", "Menu principal",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
			if (escolha < 0 || escolha == opcoes.length - 1) {
				usuario = null;
				return;
			}
			executarOpcao(escolha);
		}
	}

	private static String[] opcoesDoPerfil() {
		if (usuario instanceof Atleta) {
			return new String[] { "Ver equipes", "Pedir para entrar em equipe", "Ver campeonatos",
					"Ver jogos e historico", "Ver notificacoes", "Sair" };
		}
		if (usuario instanceof Tecnico) {
			return new String[] { "Minhas equipes", "Criar equipe", "Gerenciar elenco",
					"Ver campeonatos e inscrever equipe", "Solicitar reserva", "Ver jogos",
					"Convocar atletas", "Registrar resultado", "Ver notificacoes", "Sair" };
		}
		return new String[] { "Gerenciar espacos", "Gerenciar campeonatos", "Aprovar inscricoes",
				"Aprovar reservas", "Suspender atleta", "Enviar comunicado", "Relatorios",
				"Ver notificacoes", "Sair" };
	}

	private static void executarOpcao(int escolha) {
		try {
			if (usuario instanceof Atleta) {
				switch (escolha) {
				case 0: verEquipesAtleta(); break;
				case 1: pedirEquipe(); break;
				case 2: verCampeonatos(); break;
				case 3: verJogosAtleta(); break;
				case 4: notificacoes(); break;
				default: break;
				}
			} else if (usuario instanceof Tecnico) {
				switch (escolha) {
				case 0: verEquipesTecnico(); break;
				case 1: criarEquipe(); break;
				case 2: gerenciarElenco(); break;
				case 3: inscreverEquipe(); break;
				case 4: reservarEspaco(); break;
				case 5: verJogosTecnico(); break;
				case 6: convocarAtletas(); break;
				case 7: registrarResultado(); break;
				case 8: notificacoes(); break;
				default: break;
				}
			} else {
				switch (escolha) {
				case 0: gerenciarEspacos(); break;
				case 1: gerenciarCampeonatos(); break;
				case 2: aprovarInscricao(); break;
				case 3: aprovarReserva(); break;
				case 4: suspenderAtleta(); break;
				case 5: comunicado(); break;
				case 6: mensagem(sistema.getRelatorioService().relatorioGeral(usuario)); break;
				case 7: notificacoes(); break;
				default: break;
				}
			}
		} catch (RegraNegocioException | NumberFormatException | DateTimeParseException erro) {
			erro(erro);
		}
	}

	private static void verEquipesAtleta() {
		Atleta atleta = (Atleta) usuario;
		String lista = sistema.getEquipeService().listarEquipes().stream().filter(Equipe::isAtiva)
				.filter(e -> e.getModalidade() == atleta.getModalidadeFavorita())
				.map(e -> e.getNome() + " - tecnico " + sistema.getUsuarioService().nomeUsuario(e.getTecnicoId())
						+ " - " + e.getAtletasIds().size() + " atleta(s)")
				.collect(Collectors.joining("\n"));
		mensagem(lista.isEmpty() ? "Nenhuma equipe disponivel para sua modalidade." : lista);
	}

	private static void pedirEquipe() throws RegraNegocioException {
		Atleta atleta = (Atleta) usuario;
		List<Equipe> equipes = sistema.getEquipeService().listarEquipes().stream().filter(Equipe::isAtiva)
				.filter(e -> e.getModalidade() == atleta.getModalidadeFavorita()).collect(Collectors.toList());
		Equipe equipe = escolher("Equipe", equipes, Equipe::getNome);
		if (equipe != null) {
			sistema.getEquipeService().solicitarParticipacao(atleta, equipe.getId());
			mensagem("Pedido enviado ao tecnico.");
		}
	}

	private static void verCampeonatos() {
		String lista = sistema.getCampeonatoService().listarCampeonatos().stream().map(c -> c.getNome() + " - "
				+ c.getModalidade() + " - " + c.getStatus() + "\n" + c.getRegulamento())
				.collect(Collectors.joining("\n\n"));
		mensagem(lista.isEmpty() ? "Nenhum campeonato cadastrado." : lista);
	}

	private static void verJogosAtleta() {
		Atleta atleta = (Atleta) usuario;
		String jogos = sistema.getUsuarioService().partidasDoAtleta(atleta).stream().map(p -> sistema.getEquipeService().nomeEquipe(p.getEquipeCasaId())
				+ " " + placar(p) + " " + sistema.getEquipeService().nomeEquipe(p.getEquipeVisitanteId())
				+ " - " + SistemaService.formatarDataHora(p.getDataHora())).collect(Collectors.joining("\n"));
		String historico = String.join("\n", sistema.getUsuarioService().historicoAtleta(atleta));
		mensagem("JOGOS\n" + (jogos.isEmpty() ? "Nenhum jogo." : jogos)
				+ "\n\nHISTORICO\n" + (historico.isEmpty() ? "Sem registros." : historico));
	}

	private static void verEquipesTecnico() {
		Tecnico tecnico = (Tecnico) usuario;
		String lista = sistema.getEquipeService().listarEquipesDoTecnico(tecnico).stream()
				.map(e -> e.getNome() + " - " + e.getModalidade() + " - "
						+ (e.isAtiva() ? "ativa" : "inativa") + " - "
						+ e.getAtletasIds().size() + " atleta(s)")
				.collect(Collectors.joining("\n"));
		mensagem(lista.isEmpty() ? "Voce ainda nao criou equipes." : lista);
	}

	private static void criarEquipe() throws RegraNegocioException {
		JTextField nome = new JTextField();
		JComboBox<Modalidade> modalidade = new JComboBox<>(Modalidade.values());
		Object[] campos = { "Nome da equipe", nome, "Modalidade", modalidade };
		if (JOptionPane.showConfirmDialog(null, campos, "Nova equipe", JOptionPane.OK_CANCEL_OPTION)
				== JOptionPane.OK_OPTION) {
			sistema.getEquipeService().criarEquipe((Tecnico) usuario, nome.getText(), (Modalidade) modalidade.getSelectedItem());
		}
	}

	private static void gerenciarElenco() throws RegraNegocioException {
		Tecnico tecnico = (Tecnico) usuario;
		Equipe equipe = escolher("Equipe", sistema.getEquipeService().listarEquipesDoTecnico(tecnico), Equipe::getNome);
		if (equipe == null) {
			return;
		}
		String[] opcoes = { "Renomear", "Ativar/desativar", "Remover atleta", "Aceitar pedido", "Cancelar" };
		int escolha = JOptionPane.showOptionDialog(null, "Equipe: " + equipe.getNome(), "Gerenciar equipe",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
		switch (escolha) {
		case 0:
			String nome = JOptionPane.showInputDialog("Novo nome", equipe.getNome());
			if (nome != null) sistema.getEquipeService().editarEquipe(tecnico, equipe.getId(), nome);
			break;
		case 1:
			sistema.getEquipeService().alterarStatusEquipe(tecnico, equipe.getId(), !equipe.isAtiva());
			break;
		case 2:
			List<Atleta> elenco = sistema.getUsuarioService().listarAtletas().stream()
					.filter(a -> equipe.getAtletasIds().contains(a.getId())).collect(Collectors.toList());
			Atleta atleta = escolher("Atleta", elenco, Atleta::getNome);
			if (atleta != null) sistema.getEquipeService().removerAtleta(tecnico, equipe.getId(), atleta.getId());
			break;
		case 3:
			List<SolicitacaoEquipe> pedidos = sistema.getEquipeService().listarSolicitacoesEquipe().stream()
					.filter(s -> s.getEquipeId().equals(equipe.getId())
							&& s.getEstado() == EstadoSolicitacao.PENDENTE).collect(Collectors.toList());
			SolicitacaoEquipe pedido = escolher("Pedido", pedidos,
					s -> sistema.getUsuarioService().nomeUsuario(s.getAtletaId()));
			if (pedido != null) {
				int aceitar = JOptionPane.showConfirmDialog(null, "Aceitar pedido?",
						"Solicitacao", JOptionPane.YES_NO_CANCEL_OPTION);
				if (aceitar == JOptionPane.YES_OPTION || aceitar == JOptionPane.NO_OPTION) {
					sistema.getEquipeService().responderSolicitacaoEquipe(tecnico, pedido.getId(), aceitar == JOptionPane.YES_OPTION);
				}
			}
			break;
		default:
			break;
		}
	}

	private static void inscreverEquipe() throws RegraNegocioException {
		Tecnico tecnico = (Tecnico) usuario;
		Equipe equipe = escolher("Equipe", sistema.getEquipeService().listarEquipesDoTecnico(tecnico), Equipe::getNome);
		Campeonato campeonato = escolher("Campeonato", sistema.getCampeonatoService().listarCampeonatos(), Campeonato::getNome);
		if (equipe != null && campeonato != null) {
			sistema.getCampeonatoService().solicitarInscricaoCampeonato(tecnico, equipe.getId(), campeonato.getId());
			mensagem("Inscricao enviada para aprovacao.");
		}
	}

	private static void reservarEspaco() throws RegraNegocioException {
		Tecnico tecnico = (Tecnico) usuario;
		Equipe equipe = escolher("Equipe", sistema.getEquipeService().listarEquipesDoTecnico(tecnico), Equipe::getNome);
		EspacoEsportivo espaco = escolher("Espaco", sistema.getEspacoService().listarEspacos(),
				e -> e.getNome() + " - " + e.getStatus());
		if (equipe == null || espaco == null) return;
		LocalDateTime inicio = pedirData("Inicio da reserva");
		LocalDateTime fim = pedirData("Fim da reserva");
		if (inicio != null && fim != null) {
			sistema.getReservaService().solicitarReserva(tecnico, equipe.getId(), espaco.getId(), inicio, fim);
		}
	}

	private static void verJogosTecnico() {
		Tecnico tecnico = (Tecnico) usuario;
		String lista = sistema.getCampeonatoService().listarPartidas().stream()
				.filter(p -> sistema.getEquipeService().listarEquipesDoTecnico(tecnico).stream()
						.anyMatch(e -> e.getId().equals(p.getEquipeCasaId())
								|| e.getId().equals(p.getEquipeVisitanteId())))
				.map(p -> sistema.getEquipeService().nomeEquipe(p.getEquipeCasaId()) + " " + placar(p) + " "
						+ sistema.getEquipeService().nomeEquipe(p.getEquipeVisitanteId()) + " - "
						+ SistemaService.formatarDataHora(p.getDataHora()))
				.collect(Collectors.joining("\n"));
		mensagem(lista.isEmpty() ? "Nenhum jogo cadastrado." : lista);
	}

	private static void convocarAtletas() throws RegraNegocioException {
		Tecnico tecnico = (Tecnico) usuario;
		Partida partida = escolher("Partida", partidasTecnico(tecnico), p ->
				sistema.getEquipeService().nomeEquipe(p.getEquipeCasaId()) + " x " + sistema.getEquipeService().nomeEquipe(p.getEquipeVisitanteId()));
		if (partida == null) return;
		Campeonato campeonato = campeonatoDa(partida);
		Equipe equipe = equipeDa(tecnico, partida);
		List<Atleta> atletas = sistema.getUsuarioService().listarAtletas().stream()
				.filter(a -> equipe.getAtletasIds().contains(a.getId()) && !a.isSuspenso())
				.collect(Collectors.toList());
		List<Atleta> selecionados = escolherVarios("Selecione atletas", atletas, Atleta::getNome);
		if (selecionados != null && !selecionados.isEmpty()) {
			sistema.getCampeonatoService().definirEscalacao(tecnico, campeonato.getId(), partida.getId(), equipe.getId(),
					selecionados.stream().map(Atleta::getId).collect(Collectors.toList()));
		}
	}

	private static void registrarResultado() throws RegraNegocioException {
		Tecnico tecnico = (Tecnico) usuario;
		Partida partida = escolher("Partida", partidasTecnico(tecnico), p ->
				sistema.getEquipeService().nomeEquipe(p.getEquipeCasaId()) + " x " + sistema.getEquipeService().nomeEquipe(p.getEquipeVisitanteId()));
		if (partida == null) return;
		String valor = JOptionPane.showInputDialog("Placar no formato gols-casa,gols-visitante (ex.: 2,1)");
		if (valor == null) return;
		String[] gols = valor.split(",");
		if (gols.length != 2) throw new RegraNegocioException("Informe o placar no formato 2,1.");
		sistema.getCampeonatoService().registrarPlacar(tecnico, campeonatoDa(partida).getId(), partida.getId(),
				Integer.parseInt(gols[0].trim()), Integer.parseInt(gols[1].trim()));
	}

	private static List<Partida> partidasTecnico(Tecnico tecnico) {
		return sistema.getCampeonatoService().listarPartidas().stream()
				.filter(p -> sistema.getEquipeService().listarEquipesDoTecnico(tecnico).stream()
						.anyMatch(e -> e.getId().equals(p.getEquipeCasaId())
								|| e.getId().equals(p.getEquipeVisitanteId())))
				.collect(Collectors.toList());
	}

	private static Equipe equipeDa(Tecnico tecnico, Partida partida) {
		return sistema.getEquipeService().listarEquipesDoTecnico(tecnico).stream()
				.filter(e -> e.getId().equals(partida.getEquipeCasaId())
						|| e.getId().equals(partida.getEquipeVisitanteId())).findFirst().orElse(null);
	}

	private static Campeonato campeonatoDa(Partida partida) {
		return sistema.getCampeonatoService().listarCampeonatos().stream()
				.filter(c -> c.getPartidas().stream().anyMatch(p -> p.getId().equals(partida.getId())))
				.findFirst().orElse(null);
	}

	private static void gerenciarEspacos() throws RegraNegocioException {
		String[] opcoes = { "Listar", "Cadastrar", "Editar", "Alterar funcionamento", "Remover",
				"Buscar e filtrar", "Voltar" };
		int escolha = JOptionPane.showOptionDialog(null, "Gerenciamento de espacos", "Espacos",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
		switch (escolha) {
		case 0: verEspacos(); break;
		case 1: editarEspaco(null); break;
		case 2:
			EspacoEsportivo editar = escolher("Espaco", sistema.getEspacoService().listarEspacos(), EspacoEsportivo::getNome);
			if (editar != null) editarEspaco(editar);
			break;
		case 3:
			EspacoEsportivo status = escolher("Espaco", sistema.getEspacoService().listarEspacos(), EspacoEsportivo::getNome);
			if (status != null) sistema.getEspacoService().editarEspaco(usuario, status.getId(), status.getNome(), status.getTipo(),
					status.getLocalizacao(), status.getCapacidade(), status.getHorarios(),
					status.getStatus() == StatusEspaco.MANUTENCAO
							? StatusEspaco.DISPONIVEL : StatusEspaco.MANUTENCAO);
			break;
		case 4:
			EspacoEsportivo remover = escolher("Espaco", sistema.getEspacoService().listarEspacos(), EspacoEsportivo::getNome);
			if (remover != null && confirmar("Remover " + remover.getNome() + "?"))
				sistema.getEspacoService().removerEspaco(usuario, remover.getId());
			break;
		case 5: buscarEspacos(); break;
		default: break;
		}
	}

	private static void verEspacos() {
		String lista = sistema.getEspacoService().listarEspacos().stream().map(Views::textoEspaco).collect(Collectors.joining("\n"));
		mensagem(lista.isEmpty() ? "Nenhum espaco cadastrado." : lista);
	}

	private static String textoEspaco(EspacoEsportivo e) {
		return e.getNome() + " - " + e.getTipo() + " - " + e.getLocalizacao()
				+ " - capacidade " + e.getCapacidade() + " - " + e.getStatus();
	}

	private static void editarEspaco(EspacoEsportivo e) throws RegraNegocioException {
		JTextField nome = new JTextField(e == null ? "" : e.getNome());
		JComboBox<TipoEspaco> tipo = new JComboBox<>(TipoEspaco.values());
		JTextField local = new JTextField(e == null ? "" : e.getLocalizacao());
		JTextField capacidade = new JTextField(e == null ? "" : String.valueOf(e.getCapacidade()));
		JTextField horarios = new JTextField(e == null ? "" : e.getHorarios());
		JComboBox<StatusEspaco> status = new JComboBox<>(StatusEspaco.values());
		if (e != null) {
			tipo.setSelectedItem(e.getTipo());
			status.setSelectedItem(e.getStatus());
		}
		Object[] campos = { "Nome", nome, "Tipo", tipo, "Localizacao", local, "Capacidade", capacidade,
				"Horarios", horarios, "Status", status };
		if (JOptionPane.showConfirmDialog(null, campos, "Espaco esportivo", JOptionPane.OK_CANCEL_OPTION)
				!= JOptionPane.OK_OPTION) return;
		int quantidade = Integer.parseInt(capacidade.getText().trim());
		if (e == null) {
			sistema.getEspacoService().cadastrarEspaco(usuario, nome.getText(), (TipoEspaco) tipo.getSelectedItem(),
					local.getText(), quantidade, horarios.getText(), (StatusEspaco) status.getSelectedItem());
		} else {
			sistema.getEspacoService().editarEspaco(usuario, e.getId(), nome.getText(), (TipoEspaco) tipo.getSelectedItem(),
					local.getText(), quantidade, horarios.getText(), (StatusEspaco) status.getSelectedItem());
		}
	}

	private static void buscarEspacos() {
		String termo = JOptionPane.showInputDialog("Parte do nome ou localizacao (vazio = todos)", "");
		if (termo == null) return;
		Object[] tipos = { "Todos", "QUADRA", "CAMPO", "GINASIO", "PISCINA" };
		String tipoTexto = (String) JOptionPane.showInputDialog(null, "Tipo", "Filtro",
				JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
		if (tipoTexto == null) return;
		Object[] statusOpcoes = { "Todos", "DISPONIVEL", "MANUTENCAO" };
		String statusTexto = (String) JOptionPane.showInputDialog(null, "Status", "Filtro",
				JOptionPane.QUESTION_MESSAGE, null, statusOpcoes, statusOpcoes[0]);
		if (statusTexto == null) return;
		TipoEspaco tipo = tipoTexto.equals("Todos") ? null : TipoEspaco.valueOf(tipoTexto);
		StatusEspaco status = statusTexto.equals("Todos") ? null : StatusEspaco.valueOf(statusTexto);
		String lista = sistema.getEspacoService().buscarEspacos(termo, tipo, status).stream()
				.map(Views::textoEspaco).collect(Collectors.joining("\n"));
		mensagem(lista.isEmpty() ? "Nenhum espaco encontrado." : lista);
	}

	private static void gerenciarCampeonatos() throws RegraNegocioException {
		String[] opcoes = { "Listar", "Cadastrar", "Editar", "Gerar tabela de jogos", "Agendar partida",
				"Encerrar", "Remover", "Ver classificacao", "Voltar" };
		int escolha = JOptionPane.showOptionDialog(null, "Gerenciamento de campeonatos", "Campeonatos",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
		switch (escolha) {
		case 0: verCampeonatos(); break;
		case 1: editarCampeonato(null); break;
		case 2:
			Campeonato editar = escolher("Campeonato", sistema.getCampeonatoService().listarCampeonatos(), Campeonato::getNome);
			if (editar != null) editarCampeonato(editar);
			break;
		case 3: gerarTabela(); break;
		case 4: agendarPartida(); break;
		case 5:
			Campeonato encerrar = escolher("Campeonato", sistema.getCampeonatoService().listarCampeonatos(), Campeonato::getNome);
			if (encerrar != null) sistema.getCampeonatoService().encerrarCampeonato(usuario, encerrar.getId());
			break;
		case 6:
			Campeonato remover = escolher("Campeonato", sistema.getCampeonatoService().listarCampeonatos(), Campeonato::getNome);
			if (remover != null) sistema.getCampeonatoService().removerCampeonato(usuario, remover.getId());
			break;
		case 7: verClassificacao(); break;
		default: break;
		}
	}

	private static void editarCampeonato(Campeonato c) throws RegraNegocioException {
		JTextField nome = new JTextField(c == null ? "" : c.getNome());
		JTextField regra = new JTextField(c == null ? "" : c.getRegulamento());
		JComboBox<Modalidade> modalidade = new JComboBox<>(Modalidade.values());
		if (c != null) modalidade.setSelectedItem(c.getModalidade());
		Object[] campos = { "Nome", nome, "Regulamento", regra, "Modalidade", modalidade };
		if (JOptionPane.showConfirmDialog(null, campos, "Campeonato", JOptionPane.OK_CANCEL_OPTION)
				!= JOptionPane.OK_OPTION) return;
		if (c == null) {
			sistema.getCampeonatoService().cadastrarCampeonato(usuario, nome.getText(), regra.getText(),
					(Modalidade) modalidade.getSelectedItem());
		} else {
			sistema.getCampeonatoService().editarCampeonato(usuario, c.getId(), nome.getText(), regra.getText(),
					(Modalidade) modalidade.getSelectedItem());
		}
	}

	private static void gerarTabela() throws RegraNegocioException {
		Campeonato c = escolher("Campeonato", sistema.getCampeonatoService().listarCampeonatos(), Campeonato::getNome);
		if (c != null) sistema.getCampeonatoService().gerarTabelaJogos(usuario, c.getId(), LocalDateTime.now().plusDays(1));
	}

	private static void agendarPartida() throws RegraNegocioException {
		Campeonato c = escolher("Campeonato", sistema.getCampeonatoService().listarCampeonatos(), Campeonato::getNome);
		if (c == null) return;
		Partida p = escolher("Partida", c.getPartidas(),
				j -> sistema.getEquipeService().nomeEquipe(j.getEquipeCasaId()) + " x " + sistema.getEquipeService().nomeEquipe(j.getEquipeVisitanteId()));
		if (p == null) return;
		LocalDateTime data = pedirData("Novo horario");
		EspacoEsportivo espaco = escolher("Espaco", sistema.getEspacoService().listarEspacos(), EspacoEsportivo::getNome);
		if (data != null) sistema.getCampeonatoService().agendarPartida(usuario, c.getId(), p.getId(), data,
				espaco == null ? null : espaco.getId());
	}

	private static void verClassificacao() {
		Campeonato c = escolher("Campeonato", sistema.getCampeonatoService().listarCampeonatos(), Campeonato::getNome);
		if (c != null) mensagem(String.join("\n", sistema.getCampeonatoService().classificacao(c)));
	}

	private static void aprovarInscricao() throws RegraNegocioException {
		List<InscricaoCampeonato> pendentes = sistema.getCampeonatoService().listarInscricoesCampeonato().stream()
				.filter(i -> i.getSituacao() == SituacaoInscricao.PENDENTE).collect(Collectors.toList());
		InscricaoCampeonato i = escolher("Inscricao", pendentes,
				in -> sistema.getEquipeService().nomeEquipe(in.getEquipeId()) + " - " + sistema.getCampeonatoService().nomeCampeonato(in.getCampeonatoId()));
		if (i != null) {
			int resultado = JOptionPane.showConfirmDialog(null, "Aprovar inscricao?",
					"Inscricao", JOptionPane.YES_NO_CANCEL_OPTION);
			if (resultado == JOptionPane.YES_OPTION || resultado == JOptionPane.NO_OPTION)
				sistema.getCampeonatoService().responderInscricaoCampeonato(usuario, i.getId(), resultado == JOptionPane.YES_OPTION);
		}
	}

	private static void aprovarReserva() throws RegraNegocioException {
		List<Reserva> pendentes = sistema.getReservaService().listarReservas().stream()
				.filter(r -> r.getStatus() == StatusReserva.PENDENTE).collect(Collectors.toList());
		Reserva r = escolher("Reserva", pendentes,
				reserva -> sistema.getEspacoService().nomeEspaco(reserva.getEspacoId()) + " - "
						+ SistemaService.formatarDataHora(reserva.getInicio()));
		if (r != null) {
			int resultado = JOptionPane.showConfirmDialog(null, "Aprovar reserva?",
					"Reserva", JOptionPane.YES_NO_CANCEL_OPTION);
			if (resultado == JOptionPane.YES_OPTION || resultado == JOptionPane.NO_OPTION)
				sistema.getReservaService().responderReserva(usuario, r.getId(), resultado == JOptionPane.YES_OPTION);
		}
	}

	private static void suspenderAtleta() throws RegraNegocioException {
		Atleta atleta = escolher("Atleta", sistema.getUsuarioService().listarAtletas(),
				a -> a.getNome() + (a.isSuspenso() ? " (suspenso)" : ""));
		if (atleta != null) sistema.getUsuarioService().alterarSuspensao(usuario, atleta.getId(), !atleta.isSuspenso());
	}

	private static void comunicado() throws RegraNegocioException {
		String texto = JOptionPane.showInputDialog("Comunicado");
		if (texto != null) sistema.getNotificacaoService().publicarComunicado(usuario, texto);
	}

	private static void notificacoes() throws RegraNegocioException {
		List<Notificacao> lista = sistema.getUsuarioService().listarNotificacoes(usuario);
		Notificacao n = escolher("Notificacoes", lista,
				item -> (item.isLida() ? "Lida: " : "Nova: ") + item.getMensagem());
		if (n != null && !n.isLida() && confirmar("Marcar como lida?"))
			sistema.getUsuarioService().marcarNotificacaoLida(usuario, n.getId());
	}

	private static LocalDateTime pedirData(String titulo) {
		String valor = JOptionPane.showInputDialog(titulo + " (dd/MM/yyyy HH:mm)",
				LocalDateTime.now().plusDays(1).format(DATA));
		if (valor == null) return null;
		return LocalDateTime.parse(valor.trim(), DATA);
	}

	private static String placar(Partida p) {
		return p.isRealizada() ? p.getGolsCasa() + " x " + p.getGolsVisitante() : "x";
	}

	private static boolean confirmar(String mensagem) {
		return JOptionPane.showConfirmDialog(null, mensagem, "Confirmacao", JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION;
	}

	private static void mensagem(String texto) {
		JOptionPane.showMessageDialog(null, texto);
	}

	private static void erro(Exception erro) {
		JOptionPane.showMessageDialog(null, erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
	}

	private static <T> T escolher(String titulo, List<T> itens, Function<T, String> nome) {
		if (itens.isEmpty()) {
			mensagem("Nao ha opcoes disponiveis.");
			return null;
		}
		Object selecionado = JOptionPane.showInputDialog(null, titulo, titulo, JOptionPane.QUESTION_MESSAGE,
				null, itens.stream().map(nome).toArray(), nome.apply(itens.get(0)));
		if (selecionado == null) return null;
		for (T item : itens) {
			if (nome.apply(item).equals(selecionado.toString())) return item;
		}
		return null;
	}

	private static <T> List<T> escolherVarios(String titulo, List<T> itens, Function<T, String> nome) {
		if (itens.isEmpty()) {
			mensagem("Nao ha opcoes disponiveis.");
			return null;
		}
		String[] opcoes = itens.stream().map(nome).toArray(String[]::new);
		JList<String> lista = new JList<>(opcoes);
		lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		if (JOptionPane.showConfirmDialog(null, new JScrollPane(lista), titulo,
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
			return null;
		}
		List<String> selecionados = lista.getSelectedValuesList();
		return itens.stream().filter(item -> selecionados.contains(nome.apply(item))).collect(Collectors.toList());
	}
}
