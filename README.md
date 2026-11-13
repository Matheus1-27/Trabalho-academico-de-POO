# Sistema de Gerenciamento de Espaços Esportivos

Projeto desenvolvido em Java com interface desktop em Swing para gestão de atividades esportivas em um ambiente institucional ou universitário. O sistema centraliza o cadastro de usuários, equipes, espaços esportivos, reservas, campeonatos e a lógica de negócio envolvida em cada processo.

A aplicação foi pensada como um exercício de Programação Orientada a Objetos, demonstrando estruturas como encapsulamento, herança, polimorfismo, classes abstratas, interfaces, enums, coleções, composição e regras de negócio em camada de serviço.

## Visão geral

O sistema permite que diferentes perfis interajam com o ambiente esportivo:

- Atleta: participa de equipes, solicita entrada, acompanha campeonatos e jogos, recebe notificações e histórico.
- Técnico: gerencia suas equipes, aceita ou rejeita pedidos, define escalações, solicita reservas e registra resultados.
- Coordenador: cadastra e administra espaços, aprova inscrições e reservas, envia comunicados e gera relatórios.

Todos os dados são mantidos em memória durante a execução do programa. Não há persistência em banco de dados ou arquivo local.

## Funcionalidades principais

### 1. Autenticação e cadastro

- Cadastro de usuários com perfil Atleta, Técnico ou Coordenador.
- Validação de e-mail e senha mínima de 6 caracteres.
- Login com autenticação local por e-mail e senha.
- Registro de perfil e modalidade favorita para atletas.

### 2. Gestão de usuários

- Listagem de usuários e atletas.
- Suspensão de atletas por coordenadores.
- Histórico do atleta com eventos e participações em equipes/partidas.
- Notificações personalizadas por usuário.

### 3. Gestão de equipes

- Criação de equipes por técnico.
- Associação de modalidade à equipe.
- Ativação/inativação da equipe.
- Solicitação de entrada por atletas.
- Aprovação ou rejeição das solicitações pelo técnico responsável.
- Remoção de atletas da equipe.

### 4. Gestão de espaços esportivos

- Cadastro de espaços com nome, tipo, localização, capacidade, horários e status.
- Busca por termo, tipo e status do espaço.
- Edição e remoção de espaços, com validação de reservas ativas.
- Controle de manutenção e disponibilidade.

### 5. Gestão de campeonatos

- Cadastro de campeonatos por coordenador.
- Inscrição de equipes em campeonatos compatíveis com a modalidade.
- Aprovação ou rejeição das inscrições.
- Geração da tabela de jogos.
- Agendamento das partidas em data e espaço específico.
- Definição de escalações das equipes.
- Registro de placar e classificação.
- Encerramento do campeonato.

### 6. Reservas e uso de espaços

- Técnicos podem solicitar reserva de espaço para determinada equipe.
- Validação de conflito de horários.
- Aprovação ou rejeição da reserva por coordenador.
- Controle de status: pendente, aprovada ou rejeitada.

### 7. Notificações e comunicação

- Notificações automáticas para usuários em eventos de interesse.
- Marcação de notificações como lidas.
- Comunicado institucional enviado por coordenadores para todos os usuários não coordenadores.

### 8. Relatórios

- Relatório geral com estatísticas do sistema, como:
  - quantidade de atletas;
  - equipes ativas;
  - espaços cadastrados;
  - horas reservadas aprovadas;
  - ocupação estimada;
  - partidas por modalidade;
  - classificação dos campeonatos;
  - ranking de atletas por convocações.

## Arquitetura do projeto

O projeto está organizado em pacotes e camadas:

- src/app/Main.java
  - ponto de entrada da aplicação.

- src/models/
  - entidades do domínio e enums.

- src/services/
  - regras de negócio e operações do sistema.

- src/views/Views.java
  - interface gráfica em Swing com menus e diálogos.

### Estrutura de classes principais

#### Modelos

- Usuario (abstrata): base para todos os usuários.
- Atleta: atleta com modalidade favorita e histórico.
- Tecnico: responsável por equipes.
- Coordenador: perfil administrativo.
- Equipe: representa uma equipe esportiva.
- Campeonato: campeonato com partidas, equipes participantes e status.
- EspacoEsportivo: espaço físico disponível para reservas.
- Reserva: solicitação de uso do espaço.
- Partida: confronto entre duas equipes, com data, local, placar e escalações.
- Notificacao: mensagem dirigida ao usuário.
- InscricaoCampeonato: inscrição de equipe em campeonato.
- SolicitacaoEquipe: pedido de adesão de atleta à equipe.

#### Enums

- PerfilUsuario: ATLETA, TECNICO, COORDENADOR
- Modalidade: FUTEBOL, FUTSAL, VOLEI, BASQUETE, HANDEBOL, NATACAO, TENIS, OUTRA
- StatusCampeonato: ABERTO, EM_ANDAMENTO, ENCERRADO
- StatusEspaco: DISPONIVEL, MANUTENCAO
- StatusReserva: PENDENTE, APROVADA, REJEITADA
- SituacaoInscricao: PENDENTE, APROVADA, REJEITADA
- EstadoSolicitacao: PENDENTE, APROVADA, REJEITADA
- TipoEspaco: QUADRA, CAMPO, GINASIO, PISCINA

### Camada de serviços

A base funcional do sistema é centralizada em SistemaService, que mantém as listas em memória e valida as regras de negócio. Os serviços específicos agrupam as operações por área:

- UsuarioService
- EquipeService
- EspacoService
- CampeonatoService
- ReservaService
- NotificacaoService
- RelatorioService

Essa organização facilita a manutenção e deixa coerentes as responsabilidades de cada funcionalidade.

## Regras de negócio relevantes

O projeto inclui diversas validações importantes:

- Nome, e-mail e senha obrigatórios no cadastro.
- E-mail deve ter formato válido.
- Atleta não pode estar em duas equipes da mesma modalidade ao mesmo tempo.
- Equipes precisam estar ativas para receber novos atletas.
- Técnicos só podem gerenciar suas próprias equipes.
- Reservas devem respeitar o intervalo futuro e conferência de conflitos.
- Espaços em manutenção não podem receber reservas.
- Campeonatos encerrados não aceitam novas inscrições.
- Partidas só podem ter placar registrado por técnicos das equipes envolvidas.
- A tabela de jogos de um campeonato só pode ser gerada uma vez.

## Fluxo principal de uso

1. O usuário abre o programa.
2. Escolhe entre Entrar, Cadastrar ou Sair.
3. Após autenticar, entra no menu específico do perfil.
4. Realiza ações como criar equipe, pedir inscrição, solicitar reserva, aprovar pedido ou publicar comunicado.
5. O sistema exibe mensagens e notificações em JDialogs e listas do Swing.

## Estrutura de diretórios

```text
Trabalho_final_POO_Refatorado/
├── bin/                  # classes compiladas
├── src/                  # código-fonte Java
│   ├── app/
│   ├── models/
│   ├── services/
│   └── views/
├── staticFiles/          # imagens e arquivos estáticos do projeto
├── README.md             # documentação do projeto
├── .gitignore
└── cola                 # arquivo auxiliar do ambiente
```

## Como executar

### Opção 1: Eclipse

1. Abra o projeto no Eclipse.
2. Importe a pasta raiz como projeto Java.
3. Localize a classe `app.Main`.
4. Execute como aplicação Java.

### Opção 2: linha de comando

No PowerShell, na raiz do projeto:

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp bin app.Main
```

## Observações

- O projeto é uma aplicação desktop local e não persiste dados entre execuções.
- O estado é carregado em memória durante a execução da JVM.
- A interface é baseada em diálogos JOptionPane e componentes Swing simples, adequada para fins acadêmicos e de demonstração do uso de POO em Java.

## Tecnologias utilizadas

- Java
- Swing
- Estrutura de pacotes Java
- Coleções da biblioteca padrão (`List`, `Set`, `Map`)
- Java Time (`LocalDateTime`, `Duration`)

## Conclusão

Este projeto simula um sistema completo de gestão esportiva com foco em regras de negócio, relacionamento entre entidades e organização em camadas. Ele serve como exemplo prático de desenvolvimento orientado a objetos em Java, com forte uso de classes modeladoras, serviços, controle de fluxos e interface gráfica simplificada.
