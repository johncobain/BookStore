# Estrutura do Projeto BookStore com Microkernel e Plugins

```bash
BookStore/
├── docker/
│   ├── docker-compose.yml
│   ├── init.sql
│   └── README.md
│
├── microkernel/
│   ├── pom.xml         # POM PAI, que gerencia todos os módulos
│   │
│   ├── app/            # MÓDULO DO MICROKERNEL (Aplicação Principal)
│   │   ├── pom.xml
│   │   └── src/main/java/br/edu/ifba/inf008/
│   │       ├── App.java
│   │       └── shell/
│   │           ├── Core.java
│   │           └── ... (outros controladores do núcleo)
│   │
│   ├── interfaces/     # MÓDULO DAS INTERFACES COMUNS
│   │   ├── pom.xml
│   │   └── src/main/java/br/edu/ifba/inf008/interfaces/
│   │       ├── IPlugin.java
│   │       └── ... (outras interfaces do núcleo)
│   │
│   └── plugins/        # DIRETÓRIO PARA TODOS OS PLUGINS
│       │
│       ├── user-plugin/ # PLUG-IN DE EXEMPLO: GESTÃO DE USUÁRIOS
│       │   ├── pom.xml
│       │   └── src/
│       │       ├── main/
│       │       │   ├── java/br/edu/ifba/inf008/plugins/user/
│       │       │   │   ├── UserPlugin.java  # Ponto de entrada do plugin
│       │       │   │   │
│       │       │   │   ├── model/
│       │       │   │   │   └── User.java       # Classe de modelo (entidade)
│       │       │   │   │
│       │       │   │   ├── persistence/
│       │       │   │   │   └── UserDAO.java    # Data Access Object (lógica de BD)
│       │       │   │   │
│       │       │   │   └── ui/
│       │       │   │       └── UserViewController.java # Controller do JavaFX
│       │       │   │
│       │       │   └── resources/br/edu/ifba/inf008/plugins/user/ui/
│       │       │       └── user-view.fxml     # Arquivo FXML da interface
│       │       │
│       │       └── test/  # Pasta para testes unitários (opcional, mas boa prática)
│       │
│       ├── book-plugin/ # PLUG-IN PARA GESTÃO DE LIVROS (mesma estrutura)
│       │   ├── pom.xml
│       │   └── src/main/
│       │       ├── java/br/edu/ifba/inf008/plugins/book/
│       │       │   ├── BookPlugin.java
│       │       │   ├── model/
│       │       │   │   └── Book.java
│       │       │   ├── persistence/
│       │       │   │   └── BookDAO.java
│       │       │   └── ui/
│       │       │       └── BookViewController.java
│       │       └── resources/br/edu/ifba/inf008/plugins/book/ui/
│       │           └── book-view.fxml
│       │
│       ├── loan-plugin/ # PLUG-IN PARA GESTÃO DE EMPRÉSTIMOS
│       │   ├── pom.xml
│       │   └── src/main/
│       │       ├── java/br/edu/ifba/inf008/plugins/loan/
│       │       │   ├── LoanPlugin.java
│       │       │   ├── model/
│       │       │   │   └── Loan.java
│       │       │   ├── persistence/
│       │       │   │   └── LoanDAO.java
│       │       │   └── ui/
│       │       │       └── LoanViewController.java
│       │       └── resources/br/edu/ifba/inf008/plugins/loan/ui/
│       │           └── loan-view.fxml
│       │
│       └── report-plugin/ # PLUG-IN PARA RELATÓRIOS
│           ├── pom.xml
│           └── src/main/
│               ├── java/br/edu/ifba/inf008/plugins/report/
│               │   ├── ReportPlugin.java
│               │   ├── persistence/
│               │   │   └── ReportDAO.java # Lógica para buscar os dados do relatório
│               │   └── ui/
│               │       └── ReportViewController.java
│               └── resources/br/edu/ifba/inf008/plugins/report/ui/
│                   └── report-view.fxml
│
└── INF008-2025.1-T2.pdf
```

## Explicação da Estrutura Proposta

- **`microkernel/pom.xml`**: Este é o coração do seu build. Ele deve listar todos os módulos (app, interfaces, user-plugin, book-plugin, etc.) para que o Maven saiba como construí-los em ordem.

- **`plugins/`**: É o diretório que agrupa todos os seus módulos de funcionalidade. Cada subdiretório é um projeto Maven independente.

- **Estrutura Interna de um Plugin (ex: user-plugin):**

  - **`UserPlugin.java`**: A classe principal que implementa a interface IPlugin do seu módulo interfaces. O microkernel irá descobrir e carregar esta classe.

  - **`model/User.java`**: Representa a entidade User (um POJO - Plain Old Java Object) com seus atributos (nome, e-mail, etc.).

  - **`persistence/UserDAO.java`**: A classe DAO (Data Access Object) é responsável por toda a interação com o banco de dados para a entidade User. Terá métodos como salvar(User u), listarTodos(), excluir(User u), etc. Isso isola a lógica de SQL/JDBC do resto da aplicação.

  - **`ui/UserViewController.java`**: É o Controller do padrão MVC (Model-View-Controller) para o JavaFX. Ele contém a lógica da interface gráfica: o que acontece quando um botão é clicado, como preencher a tabela de usuários, etc.

  - **`resources/br/edu/ifba/inf008/plugins/user/ui/user-view.fxml`**: O arquivo FXML define a aparência da sua tela de gerenciamento de usuários (os botões, campos de texto, tabelas). Manter a UI declarativa em FXML separada da lógica no Controller é uma ótima prática.
