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

``` java
private void setupComboBoxFiltering() {
        userComboBox.setEditable(true);
        bookComboBox.setEditable(true);

        FilteredList<User> filteredUsers = new FilteredList<>(allUsersMasterData, p -> true);
        userComboBox.setItems(filteredUsers);
        userComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
            if (userComboBox.getSelectionModel().getSelectedItem() == null ||
                    !userComboBox.getSelectionModel().getSelectedItem().getName().equals(newVal)) {
                filteredUsers.setPredicate(user -> user.getName().toLowerCase().contains(newVal.toLowerCase().trim()));
            }
        }));

        FilteredList<Book> filteredBooks = new FilteredList<>(allBooksMasterData, p -> true);
        bookComboBox.setItems(filteredBooks);
        bookComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
            if (bookComboBox.getSelectionModel().getSelectedItem() == null ||
                    !bookComboBox.getSelectionModel().getSelectedItem().getTitle().equals(newVal)) {
                filteredBooks.setPredicate(book -> book.getTitle().toLowerCase().contains(newVal.toLowerCase().trim()));
            }
        }));

        userComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user == null ? null : user.getName();
            }

            @Override
            public User fromString(String string) {
                return allUsersMasterData.stream().filter(u -> u.getName().equals(string)).findFirst().orElse(null);
            }
        });
        bookComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Book book) {
                return book == null ? null : book.getTitle();
            }

            @Override
            public Book fromString(String string) {
                return allBooksMasterData.stream().filter(b -> b.getTitle().equals(string)).findFirst().orElse(null);
            }
        });
    }
    ```
