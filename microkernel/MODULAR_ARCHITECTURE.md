# Arquitetura Modular com Referências Fracas - BookStore

## 🎯 Objetivo: Verdadeira Modularidade

Este documento define a arquitetura que permite **remover qualquer plugin sem quebrar o sistema**, mantendo a funcionalidade dos demais plugins intacta.

### **Requisito Crítico:**

- ✅ Remover `book-plugin` → Sistema continua funcionando
- ✅ Usuários podem ser gerenciados normalmente
- ✅ Empréstimos funcionam com **referências fracas** (apenas IDs)
- ✅ Compilação e execução **sem dependências quebradas**

## 🏗️ Estrutura Arquitetural

### **1. Models Específicos por Plugin**

Cada plugin mantém seus models para garantir independência total:

```bash
plugins/
├── user-plugin/
│   ├── model/User.java           # ✅ Específico do plugin
│   └── persistence/UserDAO.java  # ✅ Específico do plugin
│
├── book-plugin/                  # 🗑️ Pode ser removido
│   ├── model/Book.java           # ⚠️ Se removido, não afeta outros
│   └── persistence/BookDAO.java
│
└── loan-plugin/
    ├── model/Loan.java           # ✅ Referências fracas por ID
    └── persistence/LoanDAO.java
```

### **2. Interface IDAO Genérica (Core)**

**Localização:** `interfaces/src/main/java/br/edu/ifba/inf008/interfaces/IDAO.java`

```java
public interface IDAO<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
    void update(T entity);
    void delete(T entity);
}
```

### **3. BaseDAO Centralizado (Core)**

**Localização:** `app/src/main/java/br/edu/ifba/inf008/shell/persistence/BaseDAO.java`

```java
public abstract class BaseDAO<T, ID> implements IDAO<T, ID> {

    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    // Implementações genéricas de CRUD
    // Método abstrato para obter classe da entidade
    protected abstract Class<T> getEntityClass();
}
```

## 🔗 Padrão de Referências Fracas

### **Problema dos Relacionamentos Diretos:**

```java
// ❌ PROBLEMA: Acoplamento forte
@Entity
public class Loan {
    @ManyToOne
    private User user;    // Se user-plugin for removido, quebra

    @ManyToOne
    private Book book;    // Se book-plugin for removido, quebra
}
```

### **Solução: Referências Fracas por ID:**

```java
// ✅ SOLUÇÃO: Referências fracas
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Referências FRACAS - apenas IDs
    @Column(name = "user_id")
    private Long userId;     // Em vez de @ManyToOne User

    @Column(name = "book_id")
    private Long bookId;     // Em vez de @ManyToOne Book

    @Column(name = "loan_date")
    private LocalDate loanDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    // Construtores, getters, setters...
}
```

## 💡 Implementação de DAOs Modulares

### **DAO com Validações Opcionais:**

```java
public class LoanDAO extends BaseDAO<Loan, Long> {

    @Override
    protected Class<Loan> getEntityClass() {
        return Loan.class;
    }

    public void createLoan(Long userId, Long bookId) {
        // ✅ Validar se IDs existem (graceful degradation)
        if (!userExists(userId)) {
            throw new RuntimeException("Usuário não encontrado: " + userId);
        }

        if (!bookExists(bookId)) {
            throw new RuntimeException("Livro não encontrado: " + bookId);
        }

        // Criar empréstimo
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setBookId(bookId);
        loan.setLoanDate(LocalDate.now());

        save(loan);
    }

    // ✅ Validações com degradação graciosa
    private boolean userExists(Long userId) {
        try {
            return getEntityManager()
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.id = :id", Long.class)
                .setParameter("id", userId)
                .getSingleResult() > 0;
        } catch (Exception e) {
            // Plugin User não está carregado - assumir que existe
            return true;
        }
    }

    private boolean bookExists(Long bookId) {
        try {
            return getEntityManager()
                .createQuery("SELECT COUNT(b) FROM Book b WHERE b.id = :id", Long.class)
                .setParameter("id", bookId)
                .getSingleResult() > 0;
        } catch (Exception e) {
            // Plugin Book não está carregado - assumir que existe
            return true;
        }
    }
}
```

### **DAO Específico por Plugin:**

```java
public class UserDAO extends BaseDAO<User, Long> {

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    // Métodos específicos de User
    public List<User> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
```

## 📋 persistence.xml Flexível

```xml
<persistence-unit name="bookstore-pu">
    <!-- ✅ Entidades são descobertas automaticamente -->
    <!-- Se plugin não estiver presente, entidade é ignorada -->
    <properties>
        <property name="jakarta.persistence.jdbc.driver" value="org.mariadb.jdbc.Driver"/>
        <property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://127.0.0.1:3307/bookstore"/>
        <property name="jakarta.persistence.jdbc.user" value="bookstore_user"/>
        <property name="jakarta.persistence.jdbc.password" value="BookStore@777"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
        <property name="hibernate.show_sql" value="true"/>
    </properties>
</persistence-unit>
```

## 🧪 Teste de Modularidade

### **Cenário 1: Sistema Completo**

```bash
# Todos os plugins presentes
mvn clean install
mvn exec:java -pl app
# ✅ Sistema completo funcionando
```

### **Cenário 2: Remover book-plugin**

```bash
# 1. Remover <module>plugins/book-plugin</module> do pom.xml principal
# 2. Recompilar
mvn clean install
mvn exec:java -pl app

# ✅ Resultados esperados:
# - Sistema funciona sem erros de compilação
# - Usuários funcionam normalmente
# - Empréstimos podem ser criados (com IDs manuais)
# - Menu "Book" simplesmente não aparece
# - Validação bookExists() retorna true (graceful degradation)
```

### **Cenário 3: Remover user-plugin**

```bash
# Similar ao cenário anterior
# ✅ Livros e empréstimos continuam funcionando
```

## 🎯 Vantagens da Arquitetura

### ✅ **Modularidade Real:**

- Plugins completamente independentes
- Remoção não quebra compilação
- Cada plugin tem sua responsabilidade isolada

### ✅ **Flexibilidade de Deploy:**

- Sistema core permanece limpo
- Relacionamentos opcionais
- Validações degradam graciosamente

### ✅ **Escalabilidade:**

- Fácil adicionar novos plugins
- Padrão consistente entre todos os plugins
- Base reutilizável (BaseDAO, IDAO)

### ✅ **Manutenibilidade:**

- Contratos bem definidos
- Separation of concerns clara
- Testes independentes por plugin

## 🚀 Implementação Prática

### **Fluxo de Desenvolvimento:**

1. **Core:** Implementar `BaseDAO` e `IDAO` ✅
2. **Plugin específico:** Estender `BaseDAO`
3. **Models independentes:** Usar apenas IDs para referências
4. **Validações opcionais:** Implementar graceful degradation
5. **Testes:** Validar remoção de plugins

### **Comandos de Validação:**

```bash
# Teste completo
mvn clean install && mvn exec:java -pl app

# Teste sem book-plugin
# (editar pom.xml primeiro)
mvn clean install && mvn exec:java -pl app

# Verificar logs
# Não deve haver erros de ClassNotFoundException
```

## 📝 Resumo

Esta arquitetura garante que o sistema BookStore seja **verdadeiramente modular**, permitindo:

- ✅ **Remoção segura** de qualquer plugin
- ✅ **Compilação bem-sucedida** mesmo com plugins ausentes
- ✅ **Funcionalidade preservada** dos plugins restantes
- ✅ **Escalabilidade** para novos plugins
- ✅ **Manutenibilidade** através de padrões consistentes

O trade-off é que relacionamentos são menos "elegantes" (IDs em vez de objetos), mas o ganho em modularidade compensa amplamente esta limitação.
