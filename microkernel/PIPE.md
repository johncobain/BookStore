# Pipeline de Desenvolvimento - BookStore

**Cronograma:** 1 mês (até 05/08/2025)  
**Meta:** Sistema completo de livraria com arquitetura de microkernel e plugins

## Visão Geral

O projeto utiliza uma abordagem focada em entregas semanais, priorizando configuração robusta na primeira semana para acelerar o desenvolvimento nas semanas seguintes. O uso do Hibernate como ORM reduz significativamente a complexidade do acesso a dados.

---

## Semana 1 (até 13/07): Configuração e Base

**Meta:** Ambiente 100% funcional e projeto base compilando

### Tarefas Essenciais

1. **Configuração do Ambiente**

   - [x] Instalar SDKMAN e Java 24
   - [x] Instalar Docker e subir MariaDB com `docker-compose up -d`
   - [x] Testar conexão com banco (DBeaver ou cliente CLI)
   - [x] Configurar Java 24 no projeto (`sdk use ...`)

2. **Configuração do Projeto**

   - [x] Executar `mvn install` no diretório raiz (`microkernel/`)
   - [x] Criar primeiro plugin vazio (`user-plugin`)
   - [x] Adicionar plugin ao POM principal
   - [x] Verificar que `mvn install` ainda funciona

3. **Configuração do Hibernate** ⚠️ **CRÍTICO**
   - [x] Adicionar dependências (`hibernate-core`, `mariadb-java-client`) nos POMs dos plugins
   - [x] Criar arquivo `persistence.xml` com configuração do banco
   - [x] Implementar classe utilitária `JPAUtil` para gerenciar EntityManagerFactory
   - [x] Configurar `hibernate.hbm2ddl.auto=update`

**⚠️ Nota:** Esta é a semana mais crítica - maior investimento de tempo e estudo, mas essencial para o sucesso do projeto.

---

## Semana 2 (até 20/07): Plugin de Usuários (CRUD Completo)

**Meta:** Funcionalidade completa de gerenciamento de usuários como modelo para outros plugins

### Tarefas de Desenvolvimento

1. **Modelo de Dados**

   - [x] Criar classe `User.java` com anotações JPA (`@Entity`, `@Table`, `@Id`, `@Column`)
   - [x] Definir atributos: nome, email, dataDeRegistro

2. **Camada de Persistência**

   - [x] Implementar `UserDAO.java` usando EntityManager
   - [x] Métodos: `save()`, `update()`, `delete()`, `findById()`, `findAll()`
   - [x] Gerenciar transações adequadamente

3. **Interface Gráfica**

   - [x] Criar tela JavaFX para listagem de usuários
   - [x] Formulários de cadastro e edição
   - [x] Conectar UI com DAO

4. **Integração com Microkernel**

   - [x] Integrar plugin ao sistema principal
   - [x] Criar menu/botão para acessar gestão de usuários

5. **Testes Funcionais** (Recomendado)
   - [x] Criar `UserDAOTest.java`
   - [x] Testar cada operação CRUD
   - [x] Validar regras de negócio

**🚀 Resultado:** Desenvolvimento mais rápido que JDBC puro, menos propenso a erros.

---

## Semana 3 (até 27/07): Replicação do Padrão

**Meta:** Implementar plugins de Livros e Empréstimos

### Plugin de Livros

1. **Estrutura Base**

   - [x] Usar `user-plugin` como template para `book-plugin`
   - [x] Criar `Book.java` com atributos: titulo, autor, isbn, anoPublicacao, quantidadeCopiasDisponiveis
   - [x] Implementar `BookDAO.java` (cópia adaptada do UserDAO)

2. **Interface e Integração**
   - [x] UI para gerenciamento de livros
   - [x] Integração com microkernel

### Plugin de Empréstimos

1. **Modelo Complexo**

   - [x] Criar `Loan.java` com relacionamentos JPA (`@ManyToOne`)
   - [x] Mapear relações com User e Book
   - [x] Atributos: id, user, book, dataEmprestimo, dataDevolucao

2. **Lógica de Negócio**

   - [x] Implementar `LoanDAO.java` com transações
   - [x] Método `realizarEmprestimo()`: criar empréstimo + diminuir estoque
   - [x] Método `registrarDevolucao()`: finalizar empréstimo + aumentar estoque
   - [x] Validações de disponibilidade

3. **Interface Avançada**

   - [ ] Formulário com ComboBox de usuários e livros
   - [ ] Validação de cópias disponíveis
   - [ ] Listagem de empréstimos ativos

4. **Testes Funcionais**
   - [x] `BookDAOTest.java`
   - [x] `LoanDAOTest.java` (especialmente importante para validar regras de negócio)

**⚡ Resultado:** Aceleração massiva - Hibernate gerencia relacionamentos e transações automaticamente.

---

## Semana 4 (até 05/08): Finalização e Entrega

**Meta:** Relatórios, refinamento e entrega final

### Plugin de Relatórios

1. **Implementação**
   - [ ] Criar `report-plugin`
   - [ ] Implementar `ReportDAO.java` com consultas JPQL
   - [ ] Query exemplo: `SELECT b.titulo, u.name FROM Loan l JOIN l.book b JOIN l.user u WHERE ...`
   - [ ] UI para exibição de relatórios

### Refinamento e Qualidade

1. **Revisão Geral**

   - [ ] Avaliar UI/UX - sistema fácil de usar?
   - [ ] Testar todos os casos de uso principais
   - [ ] Verificar integração entre plugins

2. **Documentação**

   - [ ] Escrever `README.md` final com:
     - Instruções de compilação (`mvn install`)
     - Instruções de execução (`mvn exec:java -pl app`)
     - Documentação da arquitetura
     - Guia de uso do sistema

3. **Preparação para Entrega**
   - [ ] Executar `mvn clean` para limpar arquivos temporários
   - [ ] Testar compilação e execução em ambiente limpo
   - [ ] Compactar código-fonte (`.zip` ou `.tar.gz`)

### Entrega Final

- [ ] **Email com assunto exato:** `INF008 T2 <seu-nome-completo>`
- [ ] Anexar arquivo compactado do projeto
- [ ] Incluir instruções de execução

---
