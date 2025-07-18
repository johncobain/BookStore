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

- [ ] Relacionamento com Loan (OneToMany) // ver se vale a pena

#### Book Plugin - Prioridade ALTA

- [x] **Modelo Book**: Entidade JPA com relacionamentos
  - [x] Campos: title, author, isbn, publicationYear, availableCopies
  - [x] Validações JPA
- [x] **BookDAO**: Implementar BaseDAO<Book, Long>
  - [x] findByTitle(String title)
  - [x] findByAuthor(String author)
  - [x] findByIsbn(String isbn)
  - [ ] findAvailableBooks()
- [x] **Interface FXML**: Tela completa de gerenciamento
  - [x] Formulário de cadastro/edição
  - [x] Lista com busca por título/autor
  - [ ] Controle de exemplares disponíveis
- [x] **Testes**: BookDAOTest completo

#### Loan Plugin - Prioridade ALTA

- [x] **Modelo Loan**: Entidade JPA
  - [x] Relacionamentos: User (ManyToOne), Book (ManyToOne)
  - [x] Campos: loanDate, dueDate, returnDate
- [ ] **LoanDAO**: Operações específicas
  - [ ] findByUser(User user)
  - [ ] findByBook(Book book)
  - [ ] findOverdueLoans()
  - [ ] findActiveLoans()
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
- [ ] **Testes**: Cobertura > 80%

## 🔧 Tarefas Técnicas Específicas

### Banco de Dados

- [ ] **Schema Evolution**: Scripts de migração para novas entidades
- [ ] **Test Database**: Configurar banco separado para testes
- [ ] **Data Seeding**: Popular banco com dados de exemplo
- [ ] **Backup Strategy**: Implementar backup automático

### Infraestrutura

- [ ] **Docker Compose**: Incluir aplicação além do MariaDB
- [ ] **Build Automation**: Scripts para build e deploy
- [ ] **Environment Configs**: Profiles para dev/test/prod
- [ ] **CI/CD**: Pipeline básico (GitHub Actions?)

### Code Quality

- [ ] **Checkstyle**: Configurar regras de código
- [ ] **SpotBugs**: Análise estática de código
- [ ] **JaCoCo**: Cobertura de testes
- [ ] **Documentation**: JavaDoc em todas as classes públicas

## 🐛 Bugs Conhecidos

### Críticos

- [ ] **Memory Leak**: EntityManager pode não estar fechando corretamente
- [ ] **Transaction Rollback**: Verificar se rollback funciona em todos os casos

### Menores

- [ ] **UI Responsiveness**: Interface trava durante operações longas
- [ ] **Search Performance**: Busca por texto pode ser lenta com muitos dados
- [ ] **Error Messages**: Mensagens genéricas demais para o usuário

## 💡 Melhorias Futuras (Pós-v1.0)

### Features Avançadas

- [ ] **Multi-tenancy**: Suporte a múltiplas bibliotecas
- [ ] **REST API**: Exposição via API REST
- [ ] **Mobile App**: Aplicativo móvel para consultas
- [ ] **Notifications**: Sistema de notificações (email/SMS)
- [ ] **Advanced Search**: Busca com filtros complexos
- [ ] **Book Recommendations**: Sistema de recomendações

### Plugins Adicionais

- [ ] **Fine Management**: Gestão de multas
- [ ] **Reservation System**: Sistema de reservas
- [ ] **Inventory Management**: Controle de estoque
- [ ] **Digital Library**: Livros digitais/PDFs
- [ ] **Library Statistics**: Analytics avançados

## 📊 Métricas de Sucesso

### Funcionalidades (até 05/08)

- [x] ✅ User CRUD (100%)
- [x] ✅ Book CRUD (100%)
- [ ] 🔄 Loan CRUD (10%)
- [ ] 🔄 Basic Reports (0%)

### Code Quality

- [ ] 📝 JavaDoc coverage > 80%
- [ ] 🧪 Test coverage > 70%
- [ ] 🔍 Zero critical bugs
- [ ] 📋 All TODO items categorized

### Performance

- [ ] ⚡ UI response < 200ms
- [ ] 🗄️ Database queries optimized
- [ ] 💾 Memory usage < 512MB
- [ ] 🚀 Startup time < 10s

## 📞 Suporte e Dúvidas

### Recursos de Aprendizado

- **JavaFX**: [OpenJFX Documentation](https://openjfx.io/)
- **Hibernate**: [Hibernate ORM Guide](https://hibernate.org/orm/documentation/)
- **Maven**: [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- **JUnit**: [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

### Contatos Técnicos

- **Architecture Questions**: Consultar ARCHITECTURE.md
- **Development Issues**: Consultar DEVELOPMENT.md
- **UI Problems**: Consultar UI_GUIDE.md
- **Setup Issues**: Consultar SETUP.md

---

> 🎯 **Meta Principal**: Sistema funcional completo até 05/08/2025 com todas as funcionalidades básicas implementadas e testadas.

> ⚠️ **Atenção**: Priorizar funcionalidades core antes de features avançadas. Qualidade > Quantidade.
