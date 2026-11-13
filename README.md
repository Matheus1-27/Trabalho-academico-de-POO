# Sistema de Gerenciamento de Espaços Esportivos

Aplicação desktop em Java Swing para administrar usuários, equipes, espaços esportivos, reservas e campeonatos universitários. Os dados são mantidos em memória durante a execução.

## Estrutura

- src/app/Main.java: inicialização mínima do programa.
- src/models/: modelos separados, um tipo por arquivo.
- src/services/: serviços por área funcional, ligados ao SistemaService, que mantém dados e regras compartilhadas.
- src/views/Views.java: menus e telas Swing.

Os perfis são Atleta, Tecnico e Coordenador. O projeto demonstra encapsulamento, escopo e pacotes, constantes e métodos estáticos, herança, polimorfismo, classes abstratas, interfaces e tratamento de exceções.

## Executar

No Eclipse, importe a pasta como projeto Java e execute app.Main.

No PowerShell, na pasta do projeto, com javac e java no PATH:

javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp bin app.Main
