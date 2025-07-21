# 📋 Plano de Desenvolvimento - BookStore

> Roadmap detalhado e tarefas pendentes para o sistema BookStore

## 🚀 Status Atual (Semana 2 - 14/07/2025)

### ✅ Implementado

- **Core Microkernel**: Sistema base funcionando
- **Plugin System**: Carregamento dinâmico de plugins
- **User Management**: CRUD completo com interface JavaFX
- **Persistence Layer**: Hibernate + BaseDAO genérico
- **UI Framework**: Interface modular com cards dinâmicos
- **Documentation**: Arquitetura e guias de desenvolvimento

### 🔄 Em Progresso

- **Book Plugin**: Interface básica criada, precisa de CRUD completo
- **Loan Plugin**: Interface básica criada, precisa de funcionalidades
- **Report Plugin**: Interface básica criada, precisa de implementação

## 📅 Cronograma Detalhado

### Semana 3 (15-21/07): Plugins de Livros e Empréstimos

#### Geral

- [ ] Formatar datas no formato brasileiro
- [ ] Relacionamento com Loan (OneToMany) // ver se vale a pena
- [ ] Ao deletar um Usuário que tiver um empréstimo, deletar o empréstimo e incrementar o contador de copias disponíveis no livro

#### Book Plugin - Prioridade ALTA

- [x] **Modelo Book**: Entidade JPA com relacionamentos
  - [x] Campos: title, author, isbn, publicationYear, availableCopies
  - [x] Validações JPA
- [x] **BookDAO**: Implementar BaseDAO<Book, Long>
  - [x] findByTitle(String title)
  - [x] findByAuthor(String author)
  - [x] findByIsbn(String isbn)
  - [x] findAvailableBooks()
- [x] **Interface FXML**: Tela completa de gerenciamento
  - [x] Formulário de cadastro/edição
  - [x] Lista com busca por título/autor
  - [x] Controle de exemplares disponíveis
- [x] **Testes**: BookDAOTest completo

#### Loan Plugin - Prioridade ALTA

- [x] **Modelo Loan**: Entidade JPA
  - [x] Relacionamentos: User (ManyToOne), Book (ManyToOne)
  - [x] Campos: loanDate, dueDate, returnDate
- [x] **LoanDAO**: Operações específicas
  - [x] listUser()
  - [x] listBooks()
  - [x] findActiveLoans()
- [ ] **Interface**: Gestão de empréstimos
  - [ ] Formulário de novo empréstimo
  - [ ] Lista de empréstimos ativos
  - [ ] Processo de devolução
  - [ ] Indicadores de atraso

### Semana 4 (22-28/07): Finalização e Polimento

#### Report Plugin - Prioridade MÉDIA

- [ ] **Relatórios Básicos**:
  - [ ] Usuários mais ativos
  - [ ] Livros mais emprestados
  - [ ] Empréstimos em atraso
  - [ ] Estatísticas gerais
- [ ] **Exportação**: PDF ou CSV
- [ ] **Filtros**: Por período, usuário, livro

#### Melhorias Gerais - Prioridade BAIXA

- [ ] **Autenticação**: Sistema completo de login
- [ ] **Validações**: Input validation em todos os formulários
- [ ] **Logs**: Sistema de logging estruturado
- [ ] **Performance**: Otimizações de query e UI

## 🔧 Tarefas Técnicas Específicas

### Banco de Dados

- [x] **Database**: Configurar banco de dados (MariaDB)
- [x] **Test Database**: Configurar banco separado para testes

### Infraestrutura

- [ ] **Build Automation**: Scripts para build e deploy
- [ ] **Environment Configs**: Profiles para dev/test/prod
- [ ] **CI/CD**: Pipeline básico (GitHub Actions?)

### Funcionalidades (até 05/08)

- [x] ✅ User CRUD (100%)
- [x] ✅ Book CRUD (100%)
- [ ] 🔄 Loan CRUD (10%)
- [ ] 🔄 Basic Reports (0%)

> 🎯 **Meta Principal**: Sistema funcional completo até 05/08/2025 com todas as funcionalidades básicas implementadas e testadas.
