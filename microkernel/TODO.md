# TODO

## Dentro de cada plug-in, sugiro a seguinte arquitetura de pacotes, que é simples e eficaz

- br.edu.ifba.inf008.plugins.user.UserPlugin.java (A classe principal que implementa IPlugin)

- br.edu.ifba.inf008.plugins.user.ui (Classes do JavaFX: Controllers, FXML)

- br.edu.ifba.inf008.plugins.user.persistence (Classes de acesso a dados, como UserDAO que estende BaseDAO)

- br.edu.ifba.inf008.plugins.user.model (A classe User com **referências fracas** para outros plugins)

**🎯 IMPORTANTE:** Para garantir **modularidade real**, use apenas **IDs** para referenciar entidades de outros plugins, não objetos JPA diretos.

---

## No Módulo app (Microkernel)

Neste módulo, você provavelmente não precisará criar novos controllers, mas sim entender e utilizar os que foram fornecidos pelo professor. A estrutura inicial já vem com:

- Core.java: O coração do seu microkernel. Ele orquestra a inicialização da aplicação, o carregamento dos plugins e a interface principal.

- PluginController.java: Responsável por encontrar e carregar dinamicamente os arquivos .jar dos seus plugins da pasta plugins/.

- UIController.java: Gerencia a interface gráfica principal do sistema. É aqui que você, por exemplo, adicionaria os botões ou menus que são fornecidos por cada plugin.

- AuthenticationController.java: Gerencia a autenticação, se houver.

- IOController.java: Lida com operações de entrada e saída.

- **BaseDAO.java**: Classe base genérica que implementa operações CRUD comuns. Todos os DAOs dos plugins devem estender esta classe.

- **JPAUtil.java**: Utilitário centralizado para gerenciar EntityManagerFactory e fornecer EntityManager para os plugins.

O seu trabalho aqui é fazer com que o Core utilize esses controllers para montar a aplicação dinamicamente, além de implementar a infraestrutura de persistência (BaseDAO, JPAUtil).

## Nos Módulos de Plugin

Para cada funcionalidade, você criará um conjunto completo de recursos no padrão MVC (Model-View-Controller), isolado dentro do respectivo módulo.

### 1. Plugin de Usuários (user-plugin) 🧑‍💻

#### Controller (UserViewController.java)

- **Função**: Gerenciar a lógica da tela de usuários.
- **Ações**: Lidar com cliques nos botões "Salvar", "Excluir", "Novo"; preencher a tabela de usuários com dados do banco; e ler os dados dos campos de texto do formulário.

#### Modelo (User.java)

- **Função**: Representar um usuário no sistema.
- **Atributos**: nome, email, dataDeRegistro, etc.

#### DAO (UserDAO.java)

- **Função**: Estender BaseDAO<User, Long> e implementar operações específicas de User.
- **Métodos**: Herda save(), update(), delete(), findById(), findAll() do BaseDAO. Implementa métodos específicos como findByEmail().
- **Implementação**: `public class UserDAO extends BaseDAO<User, Long> { ... }`

#### Recurso - View (user-view.fxml)

- **Função**: Definir a estrutura visual da tela de gestão de usuários (campos de texto, labels, botões, tabela).

### 2. Plugin de Livros (book-plugin) 📚

#### Controller (BookViewController.java)

- **Função**: Semelhante ao de usuário, mas para a tela de livros. Gerencia o formulário e a listagem de livros.

#### Modelo (Book.java)

- **Função**: Representar um livro.
- **Atributos**: titulo, autor, isbn, anoDePublicacao, quantidadeDeCopiasDisponiveis.

#### DAO (BookDAO.java)

- **Função**: Estender BaseDAO<Book, Long> para operações CRUD de livros.
- **Métodos**: Herda operações básicas do BaseDAO. Implementa métodos específicos como findByIsbn(), findByTitle().
- **Implementação**: `public class BookDAO extends BaseDAO<Book, Long> { ... }`

#### Recurso - View (book-view.fxml)

- **Função**: Definir a UI para o cadastro e listagem de livros.

### 3. Plugin de Empréstimos (loan-plugin) 📤

#### Controller (LoanViewController.java)

- **Função**: Orquestrar a lógica de empréstimos e devoluções.
- **Ações**: Preencher comboboxes (caixas de seleção) com usuários e livros disponíveis, registrar um novo empréstimo ao clicar em "Emprestar" (verificando a disponibilidade de cópias), e registrar a devolução.

#### Modelo (Loan.java)

- **Função**: Representar a associação entre um usuário, um livro e as datas.
- **Atributos**: id, **userId** (Long), **bookId** (Long), dataEmprestimo, dataDevolucao.
- **⚠️ IMPORTANTE**: Usar **referências fracas** (IDs) em vez de objetos User/Book para manter independência dos plugins.

#### DAO (LoanDAO.java)

- **Função**: Estender BaseDAO<Loan, Long> e gerenciar empréstimos.
- **Métodos**: Herda operações básicas. Implementa createLoan(Long userId, Long bookId), findActiveLoans(), returnBook(Long loanId).
- **Validações**: Implementar verificações opcionais de existência de User/Book com graceful degradation.

#### Recurso - View (loan-view.fxml)

- **Função**: Definir a UI para registrar novos empréstimos e devoluções.

### 4. Plugin de Relatório (report-plugin) 📊

#### Controller (ReportViewController.java)

- **Função**: Exibir o relatório de livros emprestados.
- **Ações**: Chamar o DAO para buscar os dados e preencher uma tabela ou área de texto na tela com as informações.

#### DAO (ReportDAO.java)

- **Função**: Estender BaseDAO para executar consultas específicas de relatório.
- **Método**: gerarRelatorioLivrosEmprestados() usando JPQL com referências por ID.
- **Exemplo**: `SELECT l.id, l.userId, l.bookId, l.loanDate FROM Loan l WHERE l.returnDate IS NULL`
- **Resolução**: Buscar nomes de User/Book por ID quando necessário (com validação de plugin presente).

#### Recurso - View (report-view.fxml)

- **Função**: Definir a UI que exibirá o relatório. Pode ser uma simples tabela.
