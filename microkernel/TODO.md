# 📋 Plano de Desenvolvimento - BookStore

> Roadmap detalhado e tarefas pendentes para o sistema BookStore

## 🚀 Status Atual (Semana 2 - 14/07/2025)

### ✅ Implementado

- **Core Microkernel**: Sistema base funcionando
- **Plugin System**: Carregamento dinâmico de plugins
- **User Management**: CRUD completo com interface JavaFX
- **Book Management**: CRUD completo com interface JavaFX
- **Persistence Layer**: Hibernate + BaseDAO genérico
- **UI Framework**: Interface modular com cards dinâmicos
- **Documentation**: Arquitetura e guias de desenvolvimento

### 🔄 Em Progresso

- **Loan Plugin**: Interface básica criada, precisa de funcionalidades
- **Report Plugin**: Interface básica criada, precisa de implementação

## 📅 Cronograma Detalhado

### Geral

- [ ] Relacionamento com Loan (OneToMany) // ver se vale a pena
- [ ] Ao deletar um Usuário que tiver um empréstimo, deletar o empréstimo e incrementar o contador de copias disponíveis no livro

### Semana 4 (22-28/07): Finalização e Polimento

#### Report Plugin - Prioridade MÉDIA

- [ ] **Relatórios Básicos**:
  - [ ] Usuários mais ativos
  - [ ] Livros mais emprestados
  - [ ] Estatísticas gerais
- [ ] **Exportação**: PDF ou CSV
- [ ] **Filtros**: Por período, usuário, livro

#### Melhorias Gerais - Prioridade BAIXA

- [ ] **Validações**: Input validation em todos os formulários
- [ ] **Logs**: Sistema de logging estruturado
- [ ] **Performance**: Otimizações de query e UI

## 🔧 Tarefas Técnicas Específicas

### Infraestrutura

- [ ] **Build Automation**: Scripts para build e deploy
- [ ] **Environment Configs**: Profiles para dev/test/prod
- [ ] **CI/CD**: Pipeline básico (GitHub Actions?)

### Funcionalidades (até 05/08)

- [x] ✅ User CRUD (100%)
- [x] ✅ Book CRUD (100%)
- [x] ✅ Loan CRUD (10%)
- [ ] 🔄 Basic Reports (0%)

> 🎯 **Meta Principal**: Sistema funcional completo até 05/08/2025 com todas as funcionalidades básicas implementadas e testadas.
