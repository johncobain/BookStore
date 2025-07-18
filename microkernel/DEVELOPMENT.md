# Guia de Desenvolvimento - BookStore

## Criando um Novo Plugin

### 1. Estrutura Básica

Para criar um novo plugin (exemplo: `test-plugin`):

```bash
# Criar estrutura de diretórios
mkdir -p microkernel/plugins/test-plugin/src/main/java/br/edu/ifba/inf008/plugins
```

### 2. Configurar POM do Plugin

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>br.edu.ifba.inf008</groupId>
        <artifactId>parent-project</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../..</relativePath>
    </parent>

    <artifactId>test-plugin</artifactId>

    <dependencies>
        <!-- Interface para comunicação com o core -->
        <dependency>
            <groupId>br.edu.ifba.inf008</groupId>
            <artifactId>interfaces</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <outputDirectory>${project.basedir}/../</outputDirectory>
                    <finalName>TestPlugin</finalName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 3. Implementar a Classe Principal

```java
package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;

import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class TestPlugin implements IPlugin {

    @Override
    public boolean init() {
        System.out.println(">>> Test Plugin carregado com sucesso!");

        // Obter controlador de UI
        IUIController uiController = ICore.getInstance().getUIController();

        // Criar item de menu
        MenuItem menuItem = uiController.createMenuItem("Test", "Execute Test");

        // Definir ação do menu
        menuItem.setOnAction(event -> {
            // Criar conteúdo da aba
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.getChildren().addAll(
                new Label("Test Plugin Funcionando!"),
                new Label("Este plugin foi carregado pelo microkernel."),
                new Label("Data/Hora: " + java.time.LocalDateTime.now())
            );

            // Criar nova aba
            uiController.createTab("Test Plugin", content);
            System.out.println("Test Plugin executado - nova aba criada!");
        });

        return true; // Inicialização bem-sucedida
    }
}
```

### 4. Atualizar POM Principal

Adicionar o novo plugin ao `microkernel/pom.xml`:

```xml
<modules>
    <module>interfaces</module>
    <module>app</module>
    <module>plugins/user-plugin</module>
    <module>plugins/book-plugin</module>
    <module>plugins/loan-plugin</module>
    <module>plugins/report-plugin</module>
    <module>plugins/test-plugin</module> <!-- NOVO -->
</modules>
```

### 5. Build e Teste

```bash
# Compilar o projeto
mvn clean install

# Executar aplicação
mvn exec:java -pl app
```

## Padrão DAO Implementado

### Interface Base

```java
public interface IDAO<T, ID> {
    void save(T entity);
    T update(T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
}
```

### Implementação Base

```java
public abstract class BaseDAO<T, ID> implements IDAO<T, ID> {

    protected abstract Class<T> getEntityClass();

    protected EntityManager getEntityManager() {
        Object emObject = JPAUtil.getEntityManager();
        return (EntityManager) emObject;
    }

    @Override
    public void save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T update(T entity) {
        EntityManager em = getEntityManager();
        T updatedEntity = null;
        try {
            em.getTransaction().begin();
            updatedEntity = em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return updatedEntity;
    }

    @Override
    public void delete(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(getEntityClass(), id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(getEntityClass(), id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e";
            return em.createQuery(jpql, getEntityClass()).getResultList();
        } finally {
            em.close();
        }
    }
}
```

### DAO Específico de Plugin

```java
// Em cada plugin: UserDAO, BookDAO, LoanDAO, etc.
public class UserDAO extends BaseDAO<User, Integer> {

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    // Métodos específicos do User
    public User findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.email = :email";
            return em.createQuery(jpql, User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<User> findByNameContaining(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.name LIKE :name";
            return em.createQuery(jpql, User.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
```

## Configuração do Hibernate nos Plugins

### JPAUtil do Plugin (Delegação)

```java
package br.edu.ifba.inf008.plugins.user.persistence;

public class JPAUtil {
    public static Object getEntityManager() {
        // Delega para o JPAUtil centralizado no core
        return br.edu.ifba.inf008.shell.persistence.JPAUtil.getEntityManagerAsObject();
    }
}
```

### Uso no Plugin

```java
// Em qualquer DAO do plugin
Object emObject = JPAUtil.getEntityManager();
EntityManager em = (EntityManager) emObject;

// Usar normalmente
em.getTransaction().begin();
// ... operações JPA ...
em.getTransaction().commit();
em.close();
```

## Modelos de Dados

### Estado Atual

#### User (Implementado)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId; // Sendo padronizado para Long

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    // Construtores, getters e setters...
}
```

#### Book (Planejado)

```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "available_copies")
    private Integer availableCopies;

    @Column(name = "total_copies")
    private Integer totalCopies;

    // Construtores, getters e setters...
}
```

#### Loan (Planejado)

```java
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    // Relacionamentos JPA (strong references)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LoanStatus status; // ACTIVE, RETURNED, OVERDUE

    // Construtores, getters e setters...
}
```

### Relacionamentos JPA

Com os modelos centralizados, é possível usar relacionamentos diretos:

```java
// Navegação direta entre objetos
Loan loan = loanDAO.findById(1L);
User user = loan.getUser();           // Lazy loading
Book book = loan.getBook();           // Lazy loading
String userName = user.getName();     // Acesso direto

// Queries com relacionamentos
String jpql = "SELECT l FROM Loan l JOIN l.user u WHERE u.email = :email";
List<Loan> userLoans = em.createQuery(jpql, Loan.class)
                        .setParameter("email", "user@email.com")
                        .getResultList();
```

## Desenvolvimento de Interface Gráfica

### Padrão de Criação de UI

```java
public class UserPlugin implements IPlugin {

    @Override
    public boolean init() {
        IUIController uiController = ICore.getInstance().getUIController();

        // Criar menu principal
        MenuItem usersMenu = uiController.createMenuItem("Users", "Manage Users");

        usersMenu.setOnAction(event -> {
            VBox userInterface = createUserManagementInterface();
            uiController.createTab("User Management", userInterface);
        });

        return true;
    }

    private VBox createUserManagementInterface() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Título
        Label title = new Label("User Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Formulário
        HBox formBox = createUserForm();

        // Lista de usuários
        TableView<User> userTable = createUserTable();

        layout.getChildren().addAll(title, formBox, userTable);
        return layout;
    }

    private HBox createUserForm() {
        HBox form = new HBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button saveButton = new Button("Save User");
        saveButton.setOnAction(e -> saveUser(nameField.getText(), emailField.getText()));

        form.getChildren().addAll(
            new Label("Name:"), nameField,
            new Label("Email:"), emailField,
            saveButton
        );

        return form;
    }

    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(idCol, nameCol, emailCol);

        // Carregar dados
        refreshUserTable(table);

        return table;
    }

    private void saveUser(String name, String email) {
        UserDAO userDAO = new UserDAO();
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRegistrationDate(LocalDate.now());

        userDAO.save(user);

        // Mostrar mensagem de sucesso
        // Limpar formulário
        // Atualizar tabela
    }
}
```

### Boas Práticas para UI

1. **Separação de responsabilidades**: UI separada da lógica de negócio
2. **Reutilização**: Criar componentes reutilizáveis
3. **Responsividade**: UI adaptável a diferentes tamanhos
4. **Feedback ao usuário**: Mensagens de sucesso/erro
5. **Validação**: Validar dados antes de persistir

## Desenvolvimento de Testes

### Teste de DAO (Exemplo)

```java
public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void testSaveUser() {
        // Arrange
        User user = new User();
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setRegistrationDate(LocalDate.now());

        // Act
        userDAO.save(user);

        // Assert
        assertNotNull(user.getUserId());

        // Verificar se foi salvo
        User savedUser = userDAO.findById(user.getUserId());
        assertNotNull(savedUser);
        assertEquals("João Silva", savedUser.getName());
        assertEquals("joao@email.com", savedUser.getEmail());
    }

    @Test
    void testFindByEmail() {
        // Arrange
        User user = new User();
        user.setName("Maria Santos");
        user.setEmail("maria@email.com");
        user.setRegistrationDate(LocalDate.now());
        userDAO.save(user);

        // Act
        User foundUser = userDAO.findByEmail("maria@email.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals("Maria Santos", foundUser.getName());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        User user = new User();
        user.setName("Pedro Costa");
        user.setEmail("pedro@email.com");
        user.setRegistrationDate(LocalDate.now());
        userDAO.save(user);

        // Act
        user.setName("Pedro Silva Costa");
        User updatedUser = userDAO.update(user);

        // Assert
        assertEquals("Pedro Silva Costa", updatedUser.getName());

        // Verificar no banco
        User dbUser = userDAO.findById(user.getUserId());
        assertEquals("Pedro Silva Costa", dbUser.getName());
    }

    @Test
    void testDeleteUser() {
        // Arrange
        User user = new User();
        user.setName("Ana Oliveira");
        user.setEmail("ana@email.com");
        user.setRegistrationDate(LocalDate.now());
        userDAO.save(user);
        Integer userId = user.getUserId();

        // Act
        userDAO.delete(userId);

        // Assert
        User deletedUser = userDAO.findById(userId);
        assertNull(deletedUser);
    }
}
```

### Estratégias de Teste

#### 1. Isolamento de Testes

```java
@TestMethodOrder(OrderAnnotation.class)
class UserDAOTest {

    @AfterEach
    void cleanup() {
        // Limpar dados de teste
        UserDAO userDAO = new UserDAO();
        List<User> allUsers = userDAO.findAll();
        for (User user : allUsers) {
            if (user.getEmail().endsWith("@test.com")) {
                userDAO.delete(user.getUserId());
            }
        }
    }
}
```

#### 2. Dados de Teste Consistentes

```java
private User createTestUser(String suffix) {
    User user = new User();
    user.setName("Test User " + suffix);
    user.setEmail("test" + suffix + "@test.com");
    user.setRegistrationDate(LocalDate.now());
    return user;
}
```

#### 3. Teste de Relacionamentos

```java
@Test
void testLoanWithUserAndBook() {
    // Arrange
    User user = createTestUser("1");
    userDAO.save(user);

    Book book = createTestBook("Test Book");
    bookDAO.save(book);

    Loan loan = new Loan();
    loan.setUser(user);
    loan.setBook(book);
    loan.setLoanDate(LocalDate.now());
    loan.setStatus(LoanStatus.ACTIVE);

    // Act
    loanDAO.save(loan);

    // Assert
    assertNotNull(loan.getLoanId());

    Loan savedLoan = loanDAO.findById(loan.getLoanId());
    assertNotNull(savedLoan.getUser());
    assertNotNull(savedLoan.getBook());
    assertEquals(user.getUserId(), savedLoan.getUser().getUserId());
    assertEquals(book.getBookId(), savedLoan.getBook().getBookId());
}
```

## Build e Deploy

### Build do Projeto

```bash
# Build completo
mvn clean install

# Build apenas de um plugin específico
mvn clean install -pl plugins/user-plugin

# Pular testes (para desenvolvimento rápido)
mvn clean install -DskipTests

# Build verbose
mvn clean install -X
```

### Estrutura de Output

Após o build, os JARs dos plugins são criados em:

```bash
microkernel/plugins/
├── UserPlugin.jar
├── BookPlugin.jar
├── LoanPlugin.jar
└── ReportPlugin.jar
```

### Deploy em Produção

1. **Preparar ambiente**:

   - Java 24 instalado
   - MariaDB configurado
   - Variáveis de ambiente definidas

2. **Build para produção**:

   ```bash
   mvn clean install -Pprod
   ```

3. **Configurar persistence.xml para produção**
4. **Executar aplicação**:

   ```bash
   java -jar app/target/app-1.0-SNAPSHOT.jar
   ```
