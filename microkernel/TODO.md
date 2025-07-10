# TODO

## Dentro de cada plug-in, sugiro a seguinte arquitetura de pacotes, que é simples e eficaz

- br.edu.ifba.inf008.plugins.user.UserPlugin.java (A classe principal que implementa IPlugin)

- br.edu.ifba.inf008.plugins.user.ui (Classes do JavaFX: Controllers, FXML)

- br.edu.ifba.inf008.plugins.user.persistence (Classes de acesso a dados, como UserDAO)

- br.edu.ifba.inf008.plugins.user.models (A classe User)

---

## No Módulo app (Microkernel)

Neste módulo, você provavelmente não precisará criar novos controllers, mas sim entender e utilizar os que foram fornecidos pelo professor. A estrutura inicial já vem com:

- Core.java: O coração do seu microkernel. Ele orquestra a inicialização da aplicação, o carregamento dos plugins e a interface principal.

- PluginController.java: Responsável por encontrar e carregar dinamicamente os arquivos .jar dos seus plugins da pasta plugins/.

- UIController.java: Gerencia a interface gráfica principal do sistema. É aqui que você, por exemplo, adicionaria os botões ou menus que são fornecidos por cada plugin.

- AuthenticationController.java: Gerencia a autenticação, se houver.

- IOController.java: Lida com operações de entrada e saída.

O seu trabalho aqui é fazer com que o Core utilize esses controllers para montar a aplicação dinamicamente.

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

- **Função**: Isolar o acesso ao banco de dados para a tabela de usuários.
- **Métodos**: salvar(User u), atualizar(User u), excluir(int id), buscarPorId(int id), listarTodos().

#### Recurso - View (user-view.fxml)

- **Função**: Definir a estrutura visual da tela de gestão de usuários (campos de texto, labels, botões, tabela).

### 2. Plugin de Livros (book-plugin) 📚

#### Controller (BookViewController.java)

- **Função**: Semelhante ao de usuário, mas para a tela de livros. Gerencia o formulário e a listagem de livros.

#### Modelo (Book.java)

- **Função**: Representar um livro.
- **Atributos**: titulo, autor, isbn, anoDePublicacao, quantidadeDeCopiasDisponiveis.

#### DAO (BookDAO.java)

- **Função**: Lidar com as operações de CRUD (Create, Read, Update, Delete) para a tabela de livros.
- **Métodos**: salvar(Book b), atualizar(Book b), excluir(String isbn), listarTodos(), etc.

#### Recurso - View (book-view.fxml)

- **Função**: Definir a UI para o cadastro e listagem de livros.

### 3. Plugin de Empréstimos (loan-plugin) 📤

#### Controller (LoanViewController.java)

- **Função**: Orquestrar a lógica de empréstimos e devoluções.
- **Ações**: Preencher comboboxes (caixas de seleção) com usuários e livros disponíveis, registrar um novo empréstimo ao clicar em "Emprestar" (verificando a disponibilidade de cópias), e registrar a devolução.

#### Modelo (Loan.java)

- **Função**: Representar a associação entre um usuário, um livro e as datas.
- **Atributos**: id, um objeto User, um objeto Book, dataEmprestimo, dataDevolucao.

#### DAO (LoanDAO.java)

- **Função**: Gerenciar os registros na tabela de empréstimos.
- **Métodos**: realizarEmprestimo(Loan l) (este método deve ser transacional: cria o registro de empréstimo E diminui a quantidade de cópias do livro), registrarDevolucao(int loanId) (aumenta a quantidade de cópias do livro), listarEmprestimosAtivos().

#### Recurso - View (loan-view.fxml)

- **Função**: Definir a UI para registrar novos empréstimos e devoluções.

### 4. Plugin de Relatório (report-plugin) 📊

#### Controller (ReportViewController.java)

- **Função**: Exibir o relatório de livros emprestados.
- **Ações**: Chamar o DAO para buscar os dados e preencher uma tabela ou área de texto na tela com as informações.

#### DAO (ReportDAO.java)

- **Função**: Executar a consulta SQL específica para o relatório.
- **Método**: gerarRelatorioLivrosEmprestados(). Este método fará uma consulta JOIN entre as tabelas loans, books e users para buscar o título do livro, o nome do usuário e a data do empréstimo.

#### Recurso - View (report-view.fxml)

- **Função**: Definir a UI que exibirá o relatório. Pode ser uma simples tabela.
