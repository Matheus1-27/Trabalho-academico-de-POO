package services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import models.*;

public final class SistemaService {
	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private final List<Usuario> usuarios = new ArrayList<>();
	private final List<Equipe> equipes = new ArrayList<>();
	private final List<EspacoEsportivo> espacos = new ArrayList<>();
	private final List<Campeonato> campeonatos = new ArrayList<>();
	private final List<Reserva> reservas = new ArrayList<>();
	private final List<InscricaoCampeonato> inscricoes = new ArrayList<>();
	private final List<SolicitacaoEquipe> solicitacoes = new ArrayList<>();
	private final List<Notificacao> notificacoes = new ArrayList<>();
	private final UsuarioService usuarioService = new UsuarioService(this);
	private final EquipeService equipeService = new EquipeService(this);
	private final EspacoService espacoService = new EspacoService(this);
	private final CampeonatoService campeonatoService = new CampeonatoService(this);
	private final ReservaService reservaService = new ReservaService(this);
	private final NotificacaoService notificacaoService = new NotificacaoService(this);
	private final RelatorioService relatorioService = new RelatorioService(this);
	private int proximoId = 1;

	public static final class RegraNegocioException extends Exception {
		private static final long serialVersionUID = 1L;

		public RegraNegocioException(String mensagem) {
			super(mensagem);
		}
	}


	public UsuarioService getUsuarioService() { return usuarioService; }
	public EquipeService getEquipeService() { return equipeService; }
	public EspacoService getEspacoService() { return espacoService; }
	public CampeonatoService getCampeonatoService() { return campeonatoService; }
	public ReservaService getReservaService() { return reservaService; }
	public NotificacaoService getNotificacaoService() { return notificacaoService; }
	public RelatorioService getRelatorioService() { return relatorioService; }

	private String id() {
		return String.valueOf(proximoId++);
	}

	private void coordenador(Usuario usuario) throws RegraNegocioException {
		if (!(usuario instanceof Coordenador)) {
			throw new RegraNegocioException("Somente coordenadores podem executar esta operacao.");
		}
	}

	private Usuario usuario(String id) throws RegraNegocioException {
		return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst()
				.orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado."));
	}

	private Atleta atleta(String id) throws RegraNegocioException {
		Usuario usuario = usuario(id);
		if (!(usuario instanceof Atleta)) {
			throw new RegraNegocioException("A operacao exige um atleta.");
		}
		return (Atleta) usuario;
	}

	private Tecnico tecnico(String id) throws RegraNegocioException {
		Usuario usuario = usuario(id);
		if (!(usuario instanceof Tecnico)) {
			throw new RegraNegocioException("A operacao exige um tecnico.");
		}
		return (Tecnico) usuario;
	}

	private Equipe equipe(String id) throws RegraNegocioException {
		return equipes.stream().filter(e -> e.getId().equals(id)).findFirst()
				.orElseThrow(() -> new RegraNegocioException("Equipe nao encontrada."));
	}

	private EspacoEsportivo espaco(String id) throws RegraNegocioException {
		return espacos.stream().filter(e -> e.getId().equals(id)).findFirst()
				.orElseThrow(() -> new RegraNegocioException("Espaco esportivo nao encontrado."));
	}

	private Campeonato campeonato(String id) throws RegraNegocioException {
		return campeonatos.stream().filter(c -> c.getId().equals(id)).findFirst()
				.orElseThrow(() -> new RegraNegocioException("Campeonato nao encontrado."));
	}

	private void responsavel(Usuario usuario, Equipe equipe) throws RegraNegocioException {
		tecnico(usuario.getId());
		if (!equipe.getTecnicoId().equals(usuario.getId())) {
			throw new RegraNegocioException("Tecnicos so podem gerenciar suas proprias equipes.");
		}
	}

	private void notificar(Usuario usuario, String mensagem) {
		notificacoes.add(new Notificacao(id(), usuario.getId(), mensagem, LocalDateTime.now()));
	}

	private void notificarEquipe(Equipe equipe, String mensagem) throws RegraNegocioException {
		notificar(tecnico(equipe.getTecnicoId()), mensagem);
		for (String atletaId : equipe.getAtletasIds()) {
			notificar(atleta(atletaId), mensagem);
		}
	}

	public Usuario cadastrarUsuario(PerfilUsuario perfil, String nome, String email, char[] senha,
			Modalidade modalidade) throws RegraNegocioException {
		if (perfil == null || nome == null || nome.trim().length() < 2 || email == null
				|| !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
				|| senha == null || senha.length < 6) {
			throw new RegraNegocioException("Informe nome, e-mail valido e senha com ao menos 6 caracteres.");
		}
		String normalizado = email.trim().toLowerCase(Locale.ROOT);
		if (usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(normalizado))) {
			throw new RegraNegocioException("Ja existe uma conta com esse e-mail.");
		}
		String textoSenha = new String(senha);
		String novoId = id();
		Usuario novo;
		switch (perfil) {
		case ATLETA:
			novo = new Atleta(novoId, nome.trim(), normalizado, textoSenha,
					modalidade == null ? Modalidade.OUTRA : modalidade);
			break;
		case TECNICO:
			novo = new Tecnico(novoId, nome.trim(), normalizado, textoSenha);
			break;
		case COORDENADOR:
			novo = new Coordenador(novoId, nome.trim(), normalizado, textoSenha);
			break;
		default:
			throw new RegraNegocioException("Perfil invalido.");
		}
		usuarios.add(novo);
		return novo;
	}

	public Usuario autenticar(String email, char[] senha) throws RegraNegocioException {
		if (email != null && senha != null) {
			for (Usuario usuario : usuarios) {
				if (usuario.getEmail().equalsIgnoreCase(email.trim()) && usuario.conferirSenha(senha)) {
					return usuario;
				}
			}
		}
		throw new RegraNegocioException("E-mail ou senha incorretos.");
	}

	public List<Usuario> listarUsuarios() {
		return new ArrayList<>(usuarios);
	}

	public List<Atleta> listarAtletas() {
		return usuarios.stream().filter(Atleta.class::isInstance).map(Atleta.class::cast)
				.collect(Collectors.toList());
	}

	public List<Equipe> listarEquipes() {
		return new ArrayList<>(equipes);
	}

	public List<Equipe> listarEquipesDoTecnico(Tecnico tecnico) {
		return equipes.stream().filter(e -> e.getTecnicoId().equals(tecnico.getId()))
				.collect(Collectors.toList());
	}

	public List<EspacoEsportivo> listarEspacos() {
		return new ArrayList<>(espacos);
	}

	public List<Campeonato> listarCampeonatos() {
		return new ArrayList<>(campeonatos);
	}

	public List<Reserva> listarReservas() {
		return new ArrayList<>(reservas);
	}

	public List<InscricaoCampeonato> listarInscricoesCampeonato() {
		return new ArrayList<>(inscricoes);
	}

	public List<SolicitacaoEquipe> listarSolicitacoesEquipe() {
		return new ArrayList<>(solicitacoes);
	}

	public List<Partida> listarPartidas() {
		List<Partida> partidas = new ArrayList<>();
		for (Campeonato campeonato : campeonatos) {
			partidas.addAll(campeonato.getPartidas());
		}
		return partidas;
	}

	public List<Notificacao> listarNotificacoes(Usuario usuario) {
		return notificacoes.stream().filter(n -> n.getUsuarioId().equals(usuario.getId()))
				.sorted(Comparator.comparing(Notificacao::getCriadaEm).reversed())
				.collect(Collectors.toList());
	}

	public String nomeUsuario(String id) {
		return usuarios.stream().filter(u -> u.getId().equals(id)).map(Usuario::getNome)
				.findFirst().orElse("Usuario removido");
	}

	public String nomeEquipe(String id) {
		return equipes.stream().filter(e -> e.getId().equals(id)).map(Equipe::getNome)
				.findFirst().orElse("Equipe removida");
	}

	public String nomeEspaco(String id) {
		return espacos.stream().filter(e -> e.getId().equals(id)).map(EspacoEsportivo::getNome)
				.findFirst().orElse("Espaco removido");
	}

	public String nomeCampeonato(String id) {
		return campeonatos.stream().filter(c -> c.getId().equals(id)).map(Campeonato::getNome)
				.findFirst().orElse("Campeonato removido");
	}

	public Equipe criarEquipe(Tecnico tecnico, String nome, Modalidade modalidade)
			throws RegraNegocioException {
		if (nome == null || nome.trim().isEmpty() || modalidade == null) {
			throw new RegraNegocioException("Informe nome e modalidade para criar a equipe.");
		}
		tecnico(tecnico.getId());
		Equipe equipe = new Equipe(id(), nome.trim(), modalidade, tecnico.getId());
		equipes.add(equipe);
		return equipe;
	}

	public void editarEquipe(Tecnico tecnico, String equipeId, String nome) throws RegraNegocioException {
		Equipe equipe = equipe(equipeId);
		responsavel(tecnico, equipe);
		if (nome == null || nome.trim().isEmpty()) {
			throw new RegraNegocioException("O nome da equipe nao pode ficar vazio.");
		}
		equipe.setNome(nome.trim());
	}

	public void alterarStatusEquipe(Tecnico tecnico, String equipeId, boolean ativa)
			throws RegraNegocioException {
		Equipe equipe = equipe(equipeId);
		responsavel(tecnico, equipe);
		equipe.setAtiva(ativa);
	}

	public void solicitarParticipacao(Atleta atleta, String equipeId) throws RegraNegocioException {
		Equipe equipe = equipe(equipeId);
		if (!equipe.isAtiva() || equipe.getModalidade() != atleta.getModalidadeFavorita()) {
			throw new RegraNegocioException("Escolha uma equipe ativa da sua modalidade favorita.");
		}
		if (equipe.getAtletasIds().contains(atleta.getId())
				|| equipes.stream().anyMatch(e -> e.getModalidade() == equipe.getModalidade()
						&& e.getAtletasIds().contains(atleta.getId()))) {
			throw new RegraNegocioException("Voce ja esta em uma equipe desta modalidade.");
		}
		if (solicitacoes.stream().anyMatch(s -> s.getAtletaId().equals(atleta.getId())
				&& s.getEquipeId().equals(equipeId) && s.getEstado() == EstadoSolicitacao.PENDENTE)) {
			throw new RegraNegocioException("Ja existe uma solicitacao pendente.");
		}
		solicitacoes.add(new SolicitacaoEquipe(id(), atleta.getId(), equipeId, LocalDateTime.now()));
		notificar(tecnico(equipe.getTecnicoId()), atleta.getNome() + " solicitou entrada na equipe "
				+ equipe.getNome() + ".");
	}

	public void responderSolicitacaoEquipe(Tecnico tecnico, String pedidoId, boolean aprovar)
			throws RegraNegocioException {
		SolicitacaoEquipe pedido = solicitacoes.stream().filter(s -> s.getId().equals(pedidoId)
				&& s.getEstado() == EstadoSolicitacao.PENDENTE).findFirst()
				.orElseThrow(() -> new RegraNegocioException("Solicitacao inexistente ou ja respondida."));
		Equipe equipe = equipe(pedido.getEquipeId());
		responsavel(tecnico, equipe);
		if (aprovar) {
			Atleta atleta = atleta(pedido.getAtletaId());
			if (equipes.stream().anyMatch(e -> e.getModalidade() == equipe.getModalidade()
					&& e.getAtletasIds().contains(atleta.getId()))) {
				throw new RegraNegocioException("O atleta ja esta em uma equipe desta modalidade.");
			}
			equipe.adicionarAtleta(atleta.getId());
			atleta.registrarHistorico("Entrou na equipe " + equipe.getNome() + ".");
		}
		pedido.setEstado(aprovar ? EstadoSolicitacao.APROVADA : EstadoSolicitacao.REJEITADA);
		notificar(atleta(pedido.getAtletaId()), "Solicitacao para " + equipe.getNome()
				+ " foi " + (aprovar ? "aprovada." : "rejeitada."));
	}

	public void removerAtleta(Tecnico tecnico, String equipeId, String atletaId) throws RegraNegocioException {
		Equipe equipe = equipe(equipeId);
		responsavel(tecnico, equipe);
		if (!equipe.getAtletasIds().contains(atletaId)) {
			throw new RegraNegocioException("O atleta nao pertence a esta equipe.");
		}
		equipe.removerAtleta(atletaId);
		atleta(atletaId).registrarHistorico("Saiu da equipe " + equipe.getNome() + ".");
	}

	public void alterarSuspensao(Usuario solicitante, String atletaId, boolean suspenso)
			throws RegraNegocioException {
		coordenador(solicitante);
		atleta(atletaId).setSuspenso(suspenso);
	}

	public EspacoEsportivo cadastrarEspaco(Usuario solicitante, String nome, TipoEspaco tipo,
			String localizacao, int capacidade, String horarios, StatusEspaco status)
			throws RegraNegocioException {
		coordenador(solicitante);
		if (nome == null || nome.trim().isEmpty() || tipo == null || localizacao == null
				|| localizacao.trim().isEmpty() || capacidade < 1 || horarios == null
				|| horarios.trim().isEmpty()) {
			throw new RegraNegocioException("Preencha todos os dados e informe uma capacidade positiva.");
		}
		EspacoEsportivo espaco = new EspacoEsportivo(id(), nome.trim(), tipo, localizacao.trim(),
				capacidade, horarios.trim(), status);
		espacos.add(espaco);
		return espaco;
	}

	public void editarEspaco(Usuario solicitante, String id, String nome, TipoEspaco tipo,
			String localizacao, int capacidade, String horarios, StatusEspaco status)
			throws RegraNegocioException {
		coordenador(solicitante);
		EspacoEsportivo espaco = espaco(id);
		if (nome == null || nome.trim().isEmpty() || tipo == null || localizacao == null
				|| localizacao.trim().isEmpty() || capacidade < 1 || horarios == null
				|| horarios.trim().isEmpty()) {
			throw new RegraNegocioException("Preencha todos os dados e informe uma capacidade positiva.");
		}
		espaco.setNome(nome.trim());
		espaco.setTipo(tipo);
		espaco.setLocalizacao(localizacao.trim());
		espaco.setCapacidade(capacidade);
		espaco.setHorarios(horarios.trim());
		espaco.setStatus(status);
	}

	public void removerEspaco(Usuario solicitante, String id) throws RegraNegocioException {
		coordenador(solicitante);
		if (reservas.stream().anyMatch(r -> r.getEspacoId().equals(id)
				&& r.getStatus() != StatusReserva.REJEITADA)) {
			throw new RegraNegocioException("Nao remova um espaco que possua reservas.");
		}
		if (!espacos.removeIf(e -> e.getId().equals(id))) {
			throw new RegraNegocioException("Espaco esportivo nao encontrado.");
		}
	}

	public List<EspacoEsportivo> buscarEspacos(String termo, TipoEspaco tipo, StatusEspaco status) {
		String busca = termo == null ? "" : termo.trim().toLowerCase(Locale.ROOT);
		return espacos.stream().filter(e -> busca.isEmpty()
				|| e.getNome().toLowerCase(Locale.ROOT).contains(busca)
				|| e.getLocalizacao().toLowerCase(Locale.ROOT).contains(busca))
				.filter(e -> tipo == null || e.getTipo() == tipo)
				.filter(e -> status == null || e.getStatus() == status)
				.collect(Collectors.toList());
	}

	public Campeonato cadastrarCampeonato(Usuario solicitante, String nome, String regulamento,
			Modalidade modalidade) throws RegraNegocioException {
		coordenador(solicitante);
		if (nome == null || nome.trim().isEmpty() || regulamento == null || regulamento.trim().isEmpty()
				|| modalidade == null) {
			throw new RegraNegocioException("Informe nome, modalidade e regulamento.");
		}
		Campeonato novo = new Campeonato(id(), nome.trim(), regulamento.trim(), modalidade);
		campeonatos.add(novo);
		for (Usuario usuario : usuarios) {
			if (!(usuario instanceof Coordenador)) {
				notificar(usuario, "Novo campeonato disponivel: " + novo.getNome() + ".");
			}
		}
		return novo;
	}

	public void editarCampeonato(Usuario solicitante, String id, String nome, String regulamento,
			Modalidade modalidade) throws RegraNegocioException {
		coordenador(solicitante);
		Campeonato campeonato = campeonato(id);
		if (nome == null || nome.trim().isEmpty() || regulamento == null || regulamento.trim().isEmpty()
				|| modalidade == null) {
			throw new RegraNegocioException("Informe nome, modalidade e regulamento.");
		}
		if (campeonato.getEquipesIds().stream()
				.anyMatch(equipeId -> equipes.stream().filter(e -> e.getId().equals(equipeId))
						.noneMatch(e -> e.getModalidade() == modalidade))) {
			throw new RegraNegocioException("A modalidade nao pode mudar enquanto houver equipes inscritas.");
		}
		campeonato.setNome(nome.trim());
		campeonato.setRegulamento(regulamento.trim());
		campeonato.setModalidade(modalidade);
	}

	public void removerCampeonato(Usuario solicitante, String id) throws RegraNegocioException {
		coordenador(solicitante);
		Campeonato campeonato = campeonato(id);
		if (!campeonato.getPartidas().isEmpty()) {
			throw new RegraNegocioException("Campeonatos com partidas nao podem ser removidos.");
		}
		inscricoes.removeIf(i -> i.getCampeonatoId().equals(id));
		campeonatos.remove(campeonato);
	}

	public void encerrarCampeonato(Usuario solicitante, String id) throws RegraNegocioException {
		coordenador(solicitante);
		campeonato(id).setStatus(StatusCampeonato.ENCERRADO);
	}

	public void solicitarInscricaoCampeonato(Tecnico solicitante, String equipeId, String campeonatoId)
			throws RegraNegocioException {
		Equipe equipe = equipe(equipeId);
		responsavel(solicitante, equipe);
		Campeonato campeonato = campeonato(campeonatoId);
		if (campeonato.getStatus() == StatusCampeonato.ENCERRADO) {
			throw new RegraNegocioException("Campeonatos encerrados nao recebem inscricoes.");
		}
		if (!equipe.isAtiva() || equipe.getModalidade() != campeonato.getModalidade()) {
			throw new RegraNegocioException("A equipe deve estar ativa e ser da modalidade do campeonato.");
		}
		if (inscricoes.stream().anyMatch(i -> i.getEquipeId().equals(equipeId)
				&& i.getCampeonatoId().equals(campeonatoId)
				&& i.getSituacao() != SituacaoInscricao.REJEITADA)) {
			throw new RegraNegocioException("A equipe ja possui inscricao para este campeonato.");
		}
		inscricoes.add(new InscricaoCampeonato(id(), equipeId, campeonatoId));
	}

	public void responderInscricaoCampeonato(Usuario solicitante, String inscricaoId, boolean aprovar)
			throws RegraNegocioException {
		coordenador(solicitante);
		InscricaoCampeonato inscricao = inscricoes.stream().filter(i -> i.getId().equals(inscricaoId)
				&& i.getSituacao() == SituacaoInscricao.PENDENTE).findFirst()
				.orElseThrow(() -> new RegraNegocioException("Inscricao inexistente ou ja respondida."));
		Campeonato campeonato = campeonato(inscricao.getCampeonatoId());
		Equipe equipe = equipe(inscricao.getEquipeId());
		if (campeonato.getStatus() == StatusCampeonato.ENCERRADO) {
			throw new RegraNegocioException("Campeonatos encerrados nao recebem inscricoes.");
		}
		inscricao.setSituacao(aprovar ? SituacaoInscricao.APROVADA : SituacaoInscricao.REJEITADA);
		if (aprovar) {
			campeonato.adicionarEquipe(equipe.getId());
			equipe.registrarParticipacao(campeonato.getNome());
		}
		notificarEquipe(equipe, "Inscricao " + (aprovar ? "aprovada" : "rejeitada")
				+ " no campeonato " + campeonato.getNome() + ".");
	}

	public void gerarTabelaJogos(Usuario solicitante, String campeonatoId, LocalDateTime data)
			throws RegraNegocioException {
		coordenador(solicitante);
		Campeonato campeonato = campeonato(campeonatoId);
		List<String> ids = new ArrayList<>(campeonato.getEquipesIds());
		if (ids.size() < 2) {
			throw new RegraNegocioException("Sao necessarias duas equipes inscritas.");
		}
		if (!campeonato.getPartidas().isEmpty()) {
			throw new RegraNegocioException("A tabela de jogos ja foi gerada.");
		}
		if (campeonato.getStatus() == StatusCampeonato.ENCERRADO) {
			throw new RegraNegocioException("O campeonato esta encerrado.");
		}
		LocalDateTime horario = data == null ? LocalDateTime.now().plusDays(1) : data;
		for (int i = 0; i < ids.size(); i++) {
			for (int j = i + 1; j < ids.size(); j++) {
				campeonato.adicionarPartida(new Partida(id(), ids.get(i), ids.get(j), horario));
				horario = horario.plusDays(1);
			}
		}
		campeonato.setStatus(StatusCampeonato.EM_ANDAMENTO);
		for (Usuario usuario : usuarios) {
			if (!(usuario instanceof Coordenador)) {
				notificar(usuario, "Tabela de jogos publicada: " + campeonato.getNome() + ".");
			}
		}
	}

	public void agendarPartida(Usuario solicitante, String campeonatoId, String partidaId,
			LocalDateTime data, String espacoId) throws RegraNegocioException {
		coordenador(solicitante);
		Campeonato campeonato = campeonato(campeonatoId);
		Partida partida = campeonato.getPartidas().stream().filter(p -> p.getId().equals(partidaId))
				.findFirst().orElseThrow(() -> new RegraNegocioException("Partida nao encontrada."));
		if (data == null || partida.isRealizada()) {
			throw new RegraNegocioException("Informe uma data valida para partida ainda nao realizada.");
		}
		partida.setDataHora(data);
		partida.setEspacoId(espacoId == null || espacoId.isEmpty() ? null : espaco(espacoId).getId());
		notificarEquipe(equipe(partida.getEquipeCasaId()), "Horario da partida alterado para " + formatarDataHora(data));
		notificarEquipe(equipe(partida.getEquipeVisitanteId()), "Horario da partida alterado para " + formatarDataHora(data));
	}

	public void registrarPlacar(Tecnico tecnico, String campeonatoId, String partidaId, int casa, int visitante)
			throws RegraNegocioException {
		Campeonato campeonato = campeonato(campeonatoId);
		Partida partida = campeonato.getPartidas().stream().filter(p -> p.getId().equals(partidaId))
				.findFirst().orElseThrow(() -> new RegraNegocioException("Partida nao encontrada."));
		tecnico(tecnico.getId());
		if (!equipe(partida.getEquipeCasaId()).getTecnicoId().equals(tecnico.getId())
				&& !equipe(partida.getEquipeVisitanteId()).getTecnicoId().equals(tecnico.getId())) {
			throw new RegraNegocioException("Somente tecnicos das equipes podem registrar o resultado.");
		}
		if (casa < 0 || visitante < 0) {
			throw new RegraNegocioException("O placar nao pode ser negativo.");
		}
		partida.registrarPlacar(casa, visitante);
		notificarEquipe(equipe(partida.getEquipeCasaId()), "Resultado registrado: " + casa + " x " + visitante + ".");
		notificarEquipe(equipe(partida.getEquipeVisitanteId()), "Resultado registrado: " + casa + " x " + visitante + ".");
	}

	public void definirEscalacao(Tecnico tecnico, String campeonatoId, String partidaId, String equipeId,
			List<String> atletasIds) throws RegraNegocioException {
		Campeonato campeonato = campeonato(campeonatoId);
		Partida partida = campeonato.getPartidas().stream().filter(p -> p.getId().equals(partidaId))
				.findFirst().orElseThrow(() -> new RegraNegocioException("Partida nao encontrada."));
		Equipe equipe = equipe(equipeId);
		responsavel(tecnico, equipe);
		boolean casa = partida.getEquipeCasaId().equals(equipeId);
		if (!casa && !partida.getEquipeVisitanteId().equals(equipeId)) {
			throw new RegraNegocioException("A equipe nao participa desta partida.");
		}
		if (atletasIds == null || atletasIds.isEmpty()
				|| new HashSet<>(atletasIds).size() != atletasIds.size()) {
			throw new RegraNegocioException("Selecione atletas diferentes para a escalacao.");
		}
		for (String atletaId : atletasIds) {
			Atleta atleta = atleta(atletaId);
			if (!equipe.getAtletasIds().contains(atletaId) || atleta.isSuspenso()) {
				throw new RegraNegocioException("Escalacao exige atletas da equipe que nao estejam suspensos.");
			}
		}
		if (casa) {
			partida.definirEscalacaoCasa(atletasIds);
		} else {
			partida.definirEscalacaoVisitante(atletasIds);
		}
		for (String atletaId : atletasIds) {
			notificar(atleta(atletaId), "Voce foi convocado para a partida de " + campeonato.getNome() + ".");
		}
	}

	public void solicitarReserva(Tecnico tecnico, String equipeId, String espacoId,
			LocalDateTime inicio, LocalDateTime fim) throws RegraNegocioException {
		Equipe equipe = equipe(equipeId);
		responsavel(tecnico, equipe);
		EspacoEsportivo espaco = espaco(espacoId);
		if (espaco.getStatus() == StatusEspaco.MANUTENCAO) {
			throw new RegraNegocioException("Espacos em manutencao nao recebem reservas.");
		}
		if (inicio == null || fim == null || !fim.isAfter(inicio) || inicio.isBefore(LocalDateTime.now())) {
			throw new RegraNegocioException("Informe um intervalo futuro valido.");
		}
		boolean conflito = reservas.stream().anyMatch(r -> r.getEspacoId().equals(espacoId)
				&& r.getStatus() != StatusReserva.REJEITADA
				&& inicio.isBefore(r.getFim()) && fim.isAfter(r.getInicio()));
		if (conflito) {
			throw new RegraNegocioException("Ja existe uma reserva ou solicitacao nesse horario.");
		}
		reservas.add(new Reserva(id(), equipeId, espacoId, inicio, fim));
		notificarCoordenadores("Nova solicitacao de reserva para " + espaco.getNome() + ".");
	}

	public void responderReserva(Usuario solicitante, String reservaId, boolean aprovar)
			throws RegraNegocioException {
		coordenador(solicitante);
		Reserva reserva = reservas.stream().filter(r -> r.getId().equals(reservaId)
				&& r.getStatus() == StatusReserva.PENDENTE).findFirst()
				.orElseThrow(() -> new RegraNegocioException("Reserva inexistente ou ja respondida."));
		if (aprovar) {
			EspacoEsportivo espaco = espaco(reserva.getEspacoId());
			if (espaco.getStatus() == StatusEspaco.MANUTENCAO) {
				throw new RegraNegocioException("Espacos em manutencao nao recebem reservas.");
			}
			boolean conflito = reservas.stream().anyMatch(r -> !r.getId().equals(reservaId)
					&& r.getEspacoId().equals(reserva.getEspacoId())
					&& r.getStatus() != StatusReserva.REJEITADA
					&& reserva.getInicio().isBefore(r.getFim()) && reserva.getFim().isAfter(r.getInicio()));
			if (conflito) {
				throw new RegraNegocioException("A reserva conflita com outra solicitacao.");
			}
		}
		reserva.setStatus(aprovar ? StatusReserva.APROVADA : StatusReserva.REJEITADA);
		notificarEquipe(equipe(reserva.getEquipeId()), "Reserva " + (aprovar ? "aprovada" : "rejeitada")
				+ " para " + nomeEspaco(reserva.getEspacoId()) + ".");
	}

	private void notificarCoordenadores(String mensagem) {
		for (Usuario usuario : usuarios) {
			if (usuario instanceof Coordenador) {
				notificar(usuario, mensagem);
			}
		}
	}

	public void publicarComunicado(Usuario solicitante, String mensagem) throws RegraNegocioException {
		coordenador(solicitante);
		if (mensagem == null || mensagem.trim().isEmpty()) {
			throw new RegraNegocioException("O comunicado nao pode ficar vazio.");
		}
		for (Usuario usuario : usuarios) {
			if (!(usuario instanceof Coordenador)) {
				notificar(usuario, "Comunicado: " + mensagem.trim());
			}
		}
	}

	public void marcarNotificacaoLida(Usuario usuario, String notificacaoId) throws RegraNegocioException {
		Notificacao notificacao = notificacoes.stream()
				.filter(n -> n.getId().equals(notificacaoId) && n.getUsuarioId().equals(usuario.getId()))
				.findFirst().orElseThrow(() -> new RegraNegocioException("Notificacao nao encontrada."));
		notificacao.marcarComoLida();
	}

	public List<String> historicoAtleta(Atleta atleta) {
		List<String> historico = atleta.getHistorico();
		for (Equipe equipe : equipes) {
			if (equipe.getAtletasIds().contains(atleta.getId())) {
				historico.add("Integrante da equipe " + equipe.getNome() + ".");
			}
		}
		for (Partida partida : partidasDoAtleta(atleta)) {
			historico.add("Partida " + nomeEquipe(partida.getEquipeCasaId()) + " x "
					+ nomeEquipe(partida.getEquipeVisitanteId()) + " - "
					+ (partida.isRealizada() ? partida.getGolsCasa() + " x " + partida.getGolsVisitante()
							: "Agendada"));
		}
		return historico;
	}

	public List<Partida> partidasDoAtleta(Atleta atleta) {
		return listarPartidas().stream().filter(p -> p.getEscalacaoCasa().contains(atleta.getId())
				|| p.getEscalacaoVisitante().contains(atleta.getId())).collect(Collectors.toList());
	}

	public List<String> classificacao(Campeonato campeonato) {
		Map<String, int[]> tabela = new LinkedHashMap<>();
		for (String equipeId : campeonato.getEquipesIds()) {
			tabela.put(equipeId, new int[3]);
		}
		for (Partida partida : campeonato.getPartidas()) {
			if (!partida.isRealizada() || !tabela.containsKey(partida.getEquipeCasaId())
					|| !tabela.containsKey(partida.getEquipeVisitanteId())) {
				continue;
			}
			int golsCasa = partida.getGolsCasa();
			int golsVisitante = partida.getGolsVisitante();
			int[] casa = tabela.get(partida.getEquipeCasaId());
			int[] visitante = tabela.get(partida.getEquipeVisitanteId());
			casa[1] += golsCasa - golsVisitante;
			visitante[1] += golsVisitante - golsCasa;
			casa[2] += golsCasa;
			visitante[2] += golsVisitante;
			if (golsCasa > golsVisitante) {
				casa[0] += 3;
			} else if (golsCasa < golsVisitante) {
				visitante[0] += 3;
			} else {
				casa[0]++;
				visitante[0]++;
			}
		}
		List<Map.Entry<String, int[]>> ordenada = new ArrayList<>(tabela.entrySet());
		ordenada.sort((a, b) -> {
			int pontos = Integer.compare(b.getValue()[0], a.getValue()[0]);
			if (pontos != 0) {
				return pontos;
			}
			int saldo = Integer.compare(b.getValue()[1], a.getValue()[1]);
			return saldo != 0 ? saldo : Integer.compare(b.getValue()[2], a.getValue()[2]);
		});
		List<String> linhas = new ArrayList<>();
		for (int i = 0; i < ordenada.size(); i++) {
			Map.Entry<String, int[]> item = ordenada.get(i);
			linhas.add((i + 1) + ". " + nomeEquipe(item.getKey()) + " - " + item.getValue()[0]
					+ " pts, saldo " + item.getValue()[1]);
		}
		return linhas;
	}

	public String relatorioGeral(Usuario solicitante) throws RegraNegocioException {
		coordenador(solicitante);
		StringBuilder relatorio = new StringBuilder("RELATORIO DO SISTEMA\n\n");
		relatorio.append("Atletas: ").append(listarAtletas().size()).append('\n');
		relatorio.append("Equipes ativas: ").append(equipes.stream().filter(Equipe::isAtiva).count()).append('\n');
		relatorio.append("Espacos: ").append(espacos.size()).append('\n');
		LocalDateTime inicio = LocalDateTime.now();
		LocalDateTime fim = inicio.plusDays(30);
		long horas = horasReservadas(null, inicio, fim);
		relatorio.append("Horas de reservas aprovadas: ").append(horas).append('\n');
		double capacidade = espacos.stream().filter(e -> e.getStatus() == StatusEspaco.DISPONIVEL).count()
				* 24.0 * 30.0;
		relatorio.append("Ocupacao estimada: ").append(String.format(Locale.ROOT, "%.1f%%",
				horas * 100.0 / Math.max(1.0, capacidade))).append(" nos proximos 30 dias\n");
		relatorio.append("\nUTILIZACAO POR ESPACO\n");
		for (EspacoEsportivo espaco : espacos) {
			relatorio.append(espaco.getNome()).append(": ")
					.append(horasReservadas(espaco.getId(), inicio, fim)).append(" hora(s)\n");
		}
		relatorio.append("PARTIDAS POR MODALIDADE\n");
		for (Modalidade modalidade : Modalidade.values()) {
			long total = campeonatos.stream().filter(c -> c.getModalidade() == modalidade)
					.flatMap(c -> c.getPartidas().stream()).filter(Partida::isRealizada).count();
			relatorio.append(modalidade).append(": ").append(total).append('\n');
		}
		relatorio.append("\nCLASSIFICACOES\n");
		for (Campeonato campeonato : campeonatos) {
			relatorio.append(campeonato.getNome()).append('\n');
			for (String linha : classificacao(campeonato)) {
				relatorio.append("  ").append(linha).append('\n');
			}
		}
		relatorio.append("\nRANKING DE ATLETAS\n");
		List<Atleta> atletas = listarAtletas();
		atletas.sort(Comparator.comparingInt((Atleta a) -> partidasDoAtleta(a).size()).reversed());
		for (Atleta atleta : atletas) {
			relatorio.append(atleta.getNome()).append(": ").append(partidasDoAtleta(atleta).size())
					.append(" convocatoria(oes)\n");
		}
		return relatorio.toString();
	}

	private long horasReservadas(String espacoId, LocalDateTime inicio, LocalDateTime fim) {
		return reservas.stream()
				.filter(r -> r.getStatus() == StatusReserva.APROVADA)
				.filter(r -> espacoId == null || r.getEspacoId().equals(espacoId))
				.filter(r -> r.getInicio().isBefore(fim) && r.getFim().isAfter(inicio))
				.mapToLong(r -> {
					LocalDateTime comeco = r.getInicio().isAfter(inicio) ? r.getInicio() : inicio;
					LocalDateTime termino = r.getFim().isBefore(fim) ? r.getFim() : fim;
					return Duration.between(comeco, termino).toHours();
				}).sum();
	}

	public static String formatarDataHora(LocalDateTime data) {
		return data == null ? "" : DATA.format(data);
	}
}
