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

   - [ ] Criar classe `User.java` com anotações JPA (`@Entity`, `@Table`, `@Id`, `@Column`)
   - [ ] Definir atributos: nome, email, dataDeRegistro

2. **Camada de Persistência**

   - [ ] Implementar `UserDAO.java` usando EntityManager
   - [ ] Métodos: `save()`, `update()`, `delete()`, `findById()`, `findAll()`
   - [ ] Gerenciar transações adequadamente

3. **Interface Gráfica**

   - [ ] Criar tela JavaFX para listagem de usuários
   - [ ] Formulários de cadastro e edição
   - [ ] Conectar UI com DAO

4. **Integração com Microkernel**

   - [ ] Integrar plugin ao sistema principal
   - [ ] Criar menu/botão para acessar gestão de usuários

5. **Testes Funcionais** (Recomendado)
   - [ ] Criar `UserDAOTest.java`
   - [ ] Testar cada operação CRUD
   - [ ] Validar regras de negócio

**🚀 Resultado:** Desenvolvimento mais rápido que JDBC puro, menos propenso a erros.

---

## Semana 3 (até 27/07): Replicação do Padrão

**Meta:** Implementar plugins de Livros e Empréstimos

### Plugin de Livros

1. **Estrutura Base**

   - [ ] Usar `user-plugin` como template para `book-plugin`
   - [ ] Criar `Book.java` com atributos: titulo, autor, isbn, anoPublicacao, quantidadeCopiasDisponiveis
   - [ ] Implementar `BookDAO.java` (cópia adaptada do UserDAO)

2. **Interface e Integração**
   - [ ] UI para gerenciamento de livros
   - [ ] Integração com microkernel

### Plugin de Empréstimos

1. **Modelo Complexo**

   - [ ] Criar `Loan.java` com relacionamentos JPA (`@ManyToOne`)
   - [ ] Mapear relações com User e Book
   - [ ] Atributos: id, user, book, dataEmprestimo, dataDevolucao

2. **Lógica de Negócio**

   - [ ] Implementar `LoanDAO.java` com transações
   - [ ] Método `realizarEmprestimo()`: criar empréstimo + diminuir estoque
   - [ ] Método `registrarDevolucao()`: finalizar empréstimo + aumentar estoque
   - [ ] Validações de disponibilidade

3. **Interface Avançada**

   - [ ] Formulário com ComboBox de usuários e livros
   - [ ] Validação de cópias disponíveis
   - [ ] Listagem de empréstimos ativos

4. **Testes Funcionais**
   - [ ] `BookDAOTest.java`
   - [ ] `LoanDAOTest.java` (especialmente importante para validar regras de negócio)

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

## Detalhes de Implementação com Hibernate

### Impacto por Semana

#### Semana 1: Maior Investimento em Configuração

- **Dependências:** Adicionar `hibernate-core` e `mariadb-java-client` nos POMs
- **Configuração:** Criar `persistence.xml` com conexão e dialeto MariaDB
- **Utilitários:** Implementar `JPAUtil` para gerenciar EntityManagerFactory
- **Resultado:** 🧠 Maior esforço inicial, mas base sólida para desenvolvimento

#### Semana 2: Foco em Mapeamento de Objetos

- **Anotações JPA:** `@Entity`, `@Table`, `@Id`, `@Column` na classe User
- **DAO Simplificado:** Usar `entityManager.persist()` em vez de SQL manual
- **Resultado:** 🚀 Desenvolvimento mais rápido e menos propenso a erros

#### Semana 3: Aceleração Massiva

- **Reaproveitamento:** BookDAO baseado no UserDAO
- **Relacionamentos:** Usar `@ManyToOne` para mapear Loan → User/Book
- **Transações:** Hibernate gerencia automaticamente
- **Resultado:** ⚡ Produtividade máxima na semana

#### Semana 4: JPQL para Relatórios

- **Consultas:** Usar JPQL em vez de SQL nativo
- **Exemplo:** `SELECT b.titulo, u.name FROM Loan l JOIN l.book b JOIN l.user u`
- **Resultado:** 📖 Pequena curva de aprendizado, resto igual

---

## Como Incluir Testes Unitários (Recomendado)

Para garantir os 0,5 pontos de testes funcionais e maior qualidade:

### Abordagem Incremental

#### Semana 2 (Plugin de Usuários)

- Criar `UserDAOTest.java` após implementar UserDAO
- Testar cada operação CRUD individualmente
- **Vantagem:** Validar camada de dados antes da UI

#### Semana 3 (Plugins de Livros e Empréstimos)

- Repetir processo: `BookDAOTest.java` e `LoanDAOTest.java`
- **Foco especial:** Testar regras de negócio no LoanDAO
- Validar transações (empréstimo + diminuir estoque)

### Exemplo de Estrutura de Teste

```java
@Test
public void testSalvarUsuario() {
    User user = new User("João", "joao@email.com");
    userDAO.save(user);
    assertNotNull(user.getId());
}

@Test
public void testRealizarEmprestimo() {
    // Testar se diminui estoque corretamente
    int estoqueBefore = book.getQuantidadeCopiasDisponiveis();
    loanDAO.realizarEmprestimo(loan);
    int estoqueAfter = book.getQuantidadeCopiasDisponiveis();
    assertEquals(estoqueBefore - 1, estoqueAfter);
}
```
