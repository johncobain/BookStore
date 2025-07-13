# Guia de Interface de Usuário e Testes - BookStore

## Visão Geral da Interface

O sistema BookStore utiliza **JavaFX** como framework de interface gráfica, integrado com a arquitetura de microkernel através de plugins que criam suas próprias interfaces especializadas.

### Características da UI

- **Interface baseada em abas**: Cada plugin pode criar suas próprias abas
- **Menu dinâmico**: Plugins registram automaticamente seus menus
- **Componentes reutilizáveis**: Padrões consistentes entre plugins
- **Responsiva**: Adaptável a diferentes resoluções
- **Moderna**: Design limpo e intuitivo

## Estrutura da Interface

### UIController (Core)

O `UIController` gerencia a janela principal e fornece serviços para os plugins:

```java
public interface IUIController {
    MenuItem createMenuItem(String menuText, String itemText);
    void createTab(String title, Node content);
    void showAlert(String title, String message);
    void showConfirmation(String title, String message, Runnable onConfirm);
}
```

### Padrão de Integração

```java
public class ExamplePlugin implements IPlugin {

    @Override
    public boolean init() {
        IUIController uiController = ICore.getInstance().getUIController();

        // Criar menu item
        MenuItem menuItem = uiController.createMenuItem("Example", "Open Example");

        // Definir ação
        menuItem.setOnAction(event -> {
            Node content = createExampleInterface();
            uiController.createTab("Example", content);
        });

        return true;
    }

    private Node createExampleInterface() {
        // Implementação da interface específica do plugin
        return new VBox();
    }
}
```

## Desenvolvimento com JavaFX

### Ferramentas Recomendadas

#### 1. IntelliJ IDEA + Scene Builder

**Scene Builder** é uma ferramenta visual para criar interfaces FXML:

```bash
# Download Scene Builder
# https://gluonhq.com/products/scene-builder/

# Configurar no IntelliJ:
# File → Settings → Languages & Frameworks → JavaFX
# Path to SceneBuilder: /path/to/SceneBuilder
```

**Vantagens:**

- Design visual drag-and-drop
- Código FXML limpo e organizado
- Separação entre UI e lógica
- Preview em tempo real

#### 2. Estrutura de Projeto FXML

```bash
plugin/
├── src/main/java/
│   └── br/edu/ifba/inf008/plugins/user/
│       ├── UserPlugin.java          # Classe principal
│       ├── controller/
│       │   └── UserController.java  # Controller FXML
│       ├── model/
│       │   └── UserModel.java       # Modelo de dados
│       └── persistence/
│           └── UserDAO.java         # Acesso a dados
└── src/main/resources/
    └── fxml/
        ├── user-management.fxml     # Layout principal
        ├── user-form.fxml          # Formulário
        └── user-list.fxml          # Lista de usuários
```

### Exemplo de Interface Completa

#### 1. FXML Layout (user-management.fxml)

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<VBox xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="br.edu.ifba.inf008.plugins.user.controller.UserController">
   <children>
      <!-- Título -->
      <HBox alignment="CENTER_LEFT" spacing="10.0">
         <children>
            <Label styleClass="title" text="User Management" />
         </children>
         <VBox.margin>
            <Insets bottom="20.0" />
         </VBox.margin>
      </HBox>

      <!-- Formulário -->
      <TitledPane expanded="false" text="Add New User">
         <content>
            <VBox spacing="10.0">
               <children>
                  <HBox spacing="10.0">
                     <children>
                        <Label text="Name:" />
                        <TextField fx:id="nameField" promptText="Enter user name" HBox.hgrow="ALWAYS" />
                     </children>
                  </HBox>
                  <HBox spacing="10.0">
                     <children>
                        <Label text="Email:" />
                        <TextField fx:id="emailField" promptText="Enter email address" HBox.hgrow="ALWAYS" />
                     </children>
                  </HBox>
                  <HBox alignment="CENTER_RIGHT" spacing="10.0">
                     <children>
                        <Button fx:id="clearButton" onAction="#clearForm" text="Clear" />
                        <Button fx:id="saveButton" onAction="#saveUser" styleClass="primary-button" text="Save User" />
                     </children>
                  </HBox>
               </children>
               <padding>
                  <Insets bottom="10.0" left="10.0" right="10.0" top="10.0" />
               </padding>
            </VBox>
         </content>
      </TitledPane>

      <!-- Tabela de usuários -->
      <VBox VBox.vgrow="ALWAYS">
         <children>
            <Label styleClass="section-title" text="Registered Users" />
            <TableView fx:id="userTable" VBox.vgrow="ALWAYS">
               <columns>
                  <TableColumn fx:id="idColumn" prefWidth="75.0" text="ID" />
                  <TableColumn fx:id="nameColumn" prefWidth="200.0" text="Name" />
                  <TableColumn fx:id="emailColumn" prefWidth="250.0" text="Email" />
                  <TableColumn fx:id="dateColumn" prefWidth="150.0" text="Registration Date" />
                  <TableColumn fx:id="actionsColumn" prefWidth="120.0" text="Actions" />
               </columns>
            </TableView>
         </children>
         <VBox.margin>
            <Insets top="20.0" />
         </VBox.margin>
      </VBox>
   </children>
   <padding>
      <Insets bottom="20.0" left="20.0" right="20.0" top="20.0" />
   </padding>
</VBox>
```

#### 2. Controller FXML

```java
package br.edu.ifba.inf008.plugins.user.controller;

import br.edu.ifba.inf008.shell.model.User;
import br.edu.ifba.inf008.plugins.user.persistence.UserDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Button saveButton;
    @FXML private Button clearButton;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> dateColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    private UserDAO userDAO;
    private ObservableList<User> userList;
    private User selectedUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userDAO = new UserDAO();
        userList = FXCollections.observableArrayList();

        setupTable();
        loadUsers();
        setupValidation();
    }

    private void setupTable() {
        // Configurar colunas
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Formatação personalizada para data
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getRegistrationDate();
            String formattedDate = date != null ?
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            return new SimpleStringProperty(formattedDate);
        });

        // Coluna de ações
        actionsColumn.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> editUser(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> deleteUser(getTableRow().getItem()));
                editBtn.getStyleClass().add("small-button");
                deleteBtn.getStyleClass().addAll("small-button", "danger-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        // Configurar tabela
        userTable.setItems(userList);
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editUser(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupValidation() {
        // Validação em tempo real
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private void validateForm() {
        boolean isValid = !nameField.getText().trim().isEmpty() &&
                         isValidEmail(emailField.getText().trim());
        saveButton.setDisable(!isValid);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @FXML
    private void saveUser() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (selectedUser == null) {
                // Criar novo usuário
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setRegistrationDate(LocalDate.now());
                userDAO.save(user);

                showAlert("Success", "User created successfully!");
            } else {
                // Atualizar usuário existente
                selectedUser.setName(name);
                selectedUser.setEmail(email);
                userDAO.update(selectedUser);

                showAlert("Success", "User updated successfully!");
                selectedUser = null;
                saveButton.setText("Save User");
            }

            clearForm();
            loadUsers();

        } catch (Exception e) {
            showAlert("Error", "Failed to save user: " + e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        selectedUser = null;
        saveButton.setText("Save User");
        nameField.requestFocus();
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.findAll();
            userList.clear();
            userList.addAll(users);
        } catch (Exception e) {
            showAlert("Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void editUser(User user) {
        if (user != null) {
            selectedUser = user;
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            saveButton.setText("Update User");
            nameField.requestFocus();
        }
    }

    private void deleteUser(User user) {
        if (user != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Delete");
            confirmation.setHeaderText("Delete User");
            confirmation.setContentText("Are you sure you want to delete user '" + user.getName() + "'?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        userDAO.delete(user.getUserId());
                        loadUsers();
                        showAlert("Success", "User deleted successfully!");
                    } catch (Exception e) {
                        showAlert("Error", "Failed to delete user: " + e.getMessage());
                    }
                }
            });
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

#### 3. CSS Styling (styles.css)

```css
/* Estilos globais */
.root {
  -fx-font-family: "Segoe UI", Arial, sans-serif;
  -fx-font-size: 14px;
}

/* Títulos */
.title {
  -fx-font-size: 24px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
}

.section-title {
  -fx-font-size: 16px;
  -fx-font-weight: bold;
  -fx-text-fill: #34495e;
  -fx-padding: 0 0 10 0;
}

/* Botões */
.button {
  -fx-background-color: #ecf0f1;
  -fx-border-color: #bdc3c7;
  -fx-border-radius: 4px;
  -fx-background-radius: 4px;
  -fx-padding: 8 16;
  -fx-text-fill: #2c3e50;
}

.button:hover {
  -fx-background-color: #d5dbdb;
}

.primary-button {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
  -fx-border-color: #2980b9;
}

.primary-button:hover {
  -fx-background-color: #2980b9;
}

.danger-button {
  -fx-background-color: #e74c3c;
  -fx-text-fill: white;
  -fx-border-color: #c0392b;
}

.danger-button:hover {
  -fx-background-color: #c0392b;
}

.small-button {
  -fx-padding: 4 8;
  -fx-font-size: 12px;
}

/* Campos de texto */
.text-field {
  -fx-border-color: #bdc3c7;
  -fx-border-radius: 4px;
  -fx-background-radius: 4px;
  -fx-padding: 8;
}

.text-field:focused {
  -fx-border-color: #3498db;
  -fx-effect: dropshadow(gaussian, rgba(52, 152, 219, 0.5), 4, 0, 0, 0);
}

/* Tabelas */
.table-view {
  -fx-border-color: #bdc3c7;
  -fx-border-radius: 4px;
}

.table-view .column-header {
  -fx-background-color: #ecf0f1;
  -fx-border-color: #bdc3c7;
  -fx-font-weight: bold;
}

.table-row-cell:selected {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
}

.table-row-cell:hover {
  -fx-background-color: #ecf0f1;
}

/* TitledPane */
.titled-pane > .title {
  -fx-background-color: #34495e;
  -fx-text-fill: white;
  -fx-font-weight: bold;
}

.titled-pane > .content {
  -fx-border-color: #bdc3c7;
  -fx-border-width: 0 1 1 1;
}
```

#### 4. Carregamento no Plugin

```java
public class UserPlugin implements IPlugin {

    @Override
    public boolean init() {
        IUIController uiController = ICore.getInstance().getUIController();

        MenuItem usersMenu = uiController.createMenuItem("Users", "Manage Users");

        usersMenu.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/user-management.fxml")
                );
                Node content = loader.load();

                // Aplicar CSS
                content.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
                );

                uiController.createTab("User Management", content);

            } catch (IOException e) {
                e.printStackTrace();
                uiController.showAlert("Error", "Failed to load user interface: " + e.getMessage());
            }
        });

        return true;
    }
}
```

## Boas Práticas de UI

### 1. Organização de Código

```java
// Separar responsabilidades
public class UserController {

    // Campos FXML
    @FXML private TextField nameField;

    // Dados e DAOs
    private UserDAO userDAO;
    private ObservableList<User> userList;

    // Inicialização
    @Override
    public void initialize() { /* ... */ }

    // Ações da UI
    @FXML private void saveUser() { /* ... */ }

    // Métodos auxiliares
    private void validateForm() { /* ... */ }
    private void loadUsers() { /* ... */ }

    // Configuração da UI
    private void setupTable() { /* ... */ }
}
```

### 2. Validação e Feedback

```java
// Validação em tempo real
private void setupValidation() {
    nameField.textProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal.trim().length() < 2) {
            nameField.setStyle("-fx-border-color: red;");
        } else {
            nameField.setStyle("");
        }
        validateForm();
    });
}

// Feedback visual
private void showSuccess(String message) {
    Label successLabel = new Label(message);
    successLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

    // Remover após 3 segundos
    Timeline timeline = new Timeline(new KeyFrame(
        Duration.seconds(3),
        e -> successLabel.setVisible(false)
    ));
    timeline.play();
}
```

### 3. Responsividade

```java
// Layout responsivo
private void setupResponsiveLayout() {
    // Redimensionar colunas automaticamente
    nameColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.3));
    emailColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.4));
    dateColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.2));
    actionsColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.1));
}
```

### 4. Acessibilidade

```xml
<!-- FXML com acessibilidade -->
<TextField fx:id="nameField"
           promptText="Enter user name"
           accessibleText="User name input field"
           accessibleHelp="Enter the full name of the user" />

<Button fx:id="saveButton"
        text="Save User"
        accessibleText="Save user button"
        mnemonicParsing="true"
        text="_Save User" />
```

## Testes de Interface

### Testes Unitários com TestFX

#### Configuração de Dependências

```xml
<dependency>
    <groupId>org.testfx</groupId>
    <artifactId>testfx-junit5</artifactId>
    <version>4.0.16-alpha</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testfx</groupId>
    <artifactId>testfx-core</artifactId>
    <version>4.0.16-alpha</version>
    <scope>test</scope>
</dependency>
```

#### Exemplo de Teste

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class UserControllerTest {

    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/user-management.fxml")
        );
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void shouldCreateUserWhenFormIsValid(FxRobot robot) {
        // Arrange
        robot.clickOn("#nameField");
        robot.write("João Silva");

        robot.clickOn("#emailField");
        robot.write("joao@email.com");

        // Act
        robot.clickOn("#saveButton");

        // Assert
        robot.lookup("#userTable").queryTableView().getItems()
             .stream()
             .anyMatch(user -> ((User) user).getName().equals("João Silva"));
    }

    @Test
    void shouldShowErrorWhenEmailIsInvalid(FxRobot robot) {
        // Arrange
        robot.clickOn("#nameField");
        robot.write("Test User");

        robot.clickOn("#emailField");
        robot.write("invalid-email");

        // Assert
        TextField emailField = robot.lookup("#emailField").query();
        assertTrue(emailField.getStyleClass().contains("error"));

        Button saveButton = robot.lookup("#saveButton").query();
        assertTrue(saveButton.isDisabled());
    }

    @Test
    void shouldEditUserWhenDoubleClicked(FxRobot robot) {
        // Arrange - assumindo que há um usuário na tabela
        robot.doubleClickOn("João Silva");

        // Assert
        TextField nameField = robot.lookup("#nameField").query();
        assertEquals("João Silva", nameField.getText());

        Button saveButton = robot.lookup("#saveButton").query();
        assertEquals("Update User", saveButton.getText());
    }
}
```

### Testes de DAO

#### Estratégias de Isolamento

```java
@TestMethodOrder(OrderAnnotation.class)
class UserDAOTest {

    private UserDAO userDAO;
    private List<Integer> testUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @AfterEach
    void cleanup() {
        // Limpar usuários de teste
        testUserIds.forEach(id -> {
            try {
                userDAO.delete(id);
            } catch (Exception e) {
                // Ignorar erros de limpeza
            }
        });
        testUserIds.clear();
    }

    @Test
    @Order(1)
    void testSaveUser() {
        // Arrange
        User user = createTestUser("1");

        // Act
        userDAO.save(user);
        testUserIds.add(user.getUserId());

        // Assert
        assertNotNull(user.getUserId());
        assertTrue(user.getUserId() > 0);
    }

    @Test
    @Order(2)
    void testFindById() {
        // Arrange
        User originalUser = createTestUser("2");
        userDAO.save(originalUser);
        testUserIds.add(originalUser.getUserId());

        // Act
        User foundUser = userDAO.findById(originalUser.getUserId());

        // Assert
        assertNotNull(foundUser);
        assertEquals(originalUser.getName(), foundUser.getName());
        assertEquals(originalUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void testFindByEmail() {
        // Arrange
        User user = createTestUser("3");
        userDAO.save(user);
        testUserIds.add(user.getUserId());

        // Act
        User foundUser = userDAO.findByEmail(user.getEmail());

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        User user = createTestUser("4");
        userDAO.save(user);
        testUserIds.add(user.getUserId());

        // Act
        user.setName("Updated Name");
        User updatedUser = userDAO.update(user);

        // Assert
        assertEquals("Updated Name", updatedUser.getName());

        // Verificar no banco
        User dbUser = userDAO.findById(user.getUserId());
        assertEquals("Updated Name", dbUser.getName());
    }

    @Test
    void testDeleteUser() {
        // Arrange
        User user = createTestUser("5");
        userDAO.save(user);
        Integer userId = user.getUserId();

        // Act
        userDAO.delete(userId);

        // Assert
        User deletedUser = userDAO.findById(userId);
        assertNull(deletedUser);

        // Não adicionar à lista de limpeza pois já foi deletado
    }

    @Test
    void testFindAll() {
        // Arrange
        User user1 = createTestUser("6");
        User user2 = createTestUser("7");

        userDAO.save(user1);
        userDAO.save(user2);

        testUserIds.add(user1.getUserId());
        testUserIds.add(user2.getUserId());

        // Act
        List<User> allUsers = userDAO.findAll();

        // Assert
        assertTrue(allUsers.size() >= 2);
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals(user1.getEmail())));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals(user2.getEmail())));
    }

    // Método auxiliar para criar usuários de teste
    private User createTestUser(String suffix) {
        User user = new User();
        user.setName("Test User " + suffix);
        user.setEmail("test" + suffix + "@test.com");
        user.setRegistrationDate(LocalDate.now());
        return user;
    }
}
```

### Testes de Integração

```java
@TestMethodOrder(OrderAnnotation.class)
class UserIntegrationTest {

    @Test
    @Order(1)
    void testCompleteUserWorkflow() {
        UserDAO userDAO = new UserDAO();

        // 1. Criar usuário
        User user = new User();
        user.setName("Integration Test User");
        user.setEmail("integration@test.com");
        user.setRegistrationDate(LocalDate.now());

        userDAO.save(user);
        assertNotNull(user.getUserId());

        // 2. Buscar usuário
        User foundUser = userDAO.findById(user.getUserId());
        assertNotNull(foundUser);
        assertEquals("Integration Test User", foundUser.getName());

        // 3. Atualizar usuário
        foundUser.setName("Updated Integration User");
        User updatedUser = userDAO.update(foundUser);
        assertEquals("Updated Integration User", updatedUser.getName());

        // 4. Verificar atualização
        User reloadedUser = userDAO.findById(user.getUserId());
        assertEquals("Updated Integration User", reloadedUser.getName());

        // 5. Deletar usuário
        userDAO.delete(user.getUserId());
        User deletedUser = userDAO.findById(user.getUserId());
        assertNull(deletedUser);
    }

    @Test
    void testUserEmailUniqueness() {
        UserDAO userDAO = new UserDAO();

        // Criar primeiro usuário
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("unique@test.com");
        user1.setRegistrationDate(LocalDate.now());
        userDAO.save(user1);

        // Tentar criar segundo usuário com mesmo email
        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("unique@test.com");
        user2.setRegistrationDate(LocalDate.now());

        // Deve lançar exceção
        assertThrows(Exception.class, () -> userDAO.save(user2));

        // Cleanup
        userDAO.delete(user1.getUserId());
    }
}
```

### Executando Testes

```bash
# Executar todos os testes
mvn test

# Executar apenas testes de DAO
mvn test -Dtest="*DAO*Test"

# Executar apenas testes de UI
mvn test -Dtest="*Controller*Test"

# Executar com relatório de cobertura
mvn test jacoco:report

# Executar testes em modo verbose
mvn test -X
```

## Conclusão

Este guia fornece uma base sólida para desenvolver interfaces de usuário modernas e testáveis no sistema BookStore. As práticas apresentadas garantem:

- **Separação de responsabilidades** entre UI e lógica de negócio
- **Interfaces responsivas** e acessíveis
- **Código testável** com cobertura adequada
- **Experiência de usuário** consistente entre plugins
- **Manutenibilidade** a longo prazo

Seguindo esses padrões, o desenvolvimento de novos plugins será mais rápido e consistente.
