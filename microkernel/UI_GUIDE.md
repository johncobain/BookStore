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

#### VS Code + Scene Builder: Configuração Completa

**VS Code** oferece uma excelente experiência para desenvolvimento JavaFX quando configurado adequadamente. Combinado com **Scene Builder**, proporciona um ambiente poderoso e eficiente.

##### 1. Instalação do Scene Builder

```bash
# Ubuntu/Debian - via Snap (recomendado)
sudo snap install scenebuilder

# Ubuntu/Debian - via .deb
wget https://download2.gluonhq.com/scenebuilder/17.0.0/install/linux/SceneBuilder-17.0.0.deb
sudo dpkg -i SceneBuilder-17.0.0.deb

# Fedora/CentOS/RHEL
sudo rpm -i https://download2.gluonhq.com/scenebuilder/17.0.0/install/linux/SceneBuilder-17.0.0.rpm

# Arch Linux
yay -S scenebuilder

# Verificar instalação
which scenebuilder
scenebuilder --version
```

##### 2. Configuração do VS Code para JavaFX

**Extensões Essenciais:**

```bash
# Instalar extensões via linha de comando
code --install-extension vscjava.vscode-java-pack
code --install-extension vscjava.vscode-maven
code --install-extension ms-vscode.vscode-json
code --install-extension bradlc.vscode-tailwindcss  # Para CSS
code --install-extension formulahendry.code-runner
```

**Extensões Recomendadas:**

- **Extension Pack for Java** (RedHat) - Pacote completo Java
- **Maven for Java** - Suporte Maven integrado
- **Debugger for Java** - Debug avançado
- **Test Runner for Java** - Execução de testes
- **Project Manager for Java** - Gerenciamento de projetos
- **Language Support for Java** - IntelliSense Java
- **XML** (Red Hat) - Suporte FXML aprimorado
- **CSS Peek** - Navegação CSS
- **Auto Rename Tag** - Renomeação automática de tags XML
- **Bracket Pair Colorizer** - Destaque de brackets
- **GitLens** - Git integrado avançado

##### 3. Configuração do Workspace VS Code

**Criar `.vscode/settings.json`:**

```json
{
  "java.home": "/usr/lib/jvm/java-24-openjdk-amd64",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-24",
      "path": "/usr/lib/jvm/java-24-openjdk-amd64",
      "default": true
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic",
  "java.configuration.checkProjectSettingsExclusions": false,
  "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
  "java.saveActions.organizeImports": true,
  "java.completion.favoriteStaticMembers": [
    "org.junit.jupiter.api.Assertions.*",
    "javafx.application.Platform.*"
  ],
  "files.associations": {
    "*.fxml": "xml"
  },
  "xml.validation.enabled": true,
  "xml.validation.namespaces.enabled": "always",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": "explicit"
  },
  "maven.terminal.useJavaHome": true,
  "maven.executable.path": "/usr/bin/mvn",
  "code-runner.executorMap": {
    "java": "cd $dir && mvn exec:java -pl app"
  }
}
```

**Criar `.vscode/launch.json`:**

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "BookStore Application",
      "request": "launch",
      "mainClass": "br.edu.ifba.inf008.App",
      "projectName": "app",
      "cwd": "${workspaceFolder}",
      "vmArgs": [
        "--module-path",
        "/home/john/javafx/javafx-sdk-21.0.2/lib",
        "--add-modules",
        "javafx.controls,javafx.fxml,javafx.web"
      ],
      "args": [],
      "console": "integratedTerminal"
    },
    {
      "type": "java",
      "name": "Debug BookStore",
      "request": "launch",
      "mainClass": "br.edu.ifba.inf008.App",
      "projectName": "app",
      "cwd": "${workspaceFolder}",
      "vmArgs": [
        "--module-path",
        "/home/john/javafx/javafx-sdk-21.0.2/lib",
        "--add-modules",
        "javafx.controls,javafx.fxml,javafx.web",
        "-Djavafx.verbose=true"
      ],
      "args": [],
      "console": "integratedTerminal",
      "stopOnEntry": false
    }
  ]
}
```

**Criar `.vscode/tasks.json`:**

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Clean and Compile",
      "type": "shell",
      "command": "mvn",
      "args": ["clean", "compile"],
      "group": "build",
      "presentation": {
        "echo": true,
        "reveal": "always",
        "focus": false,
        "panel": "shared"
      },
      "problemMatcher": "$java"
    },
    {
      "label": "Package Application",
      "type": "shell",
      "command": "mvn",
      "args": ["clean", "package"],
      "group": "build",
      "presentation": {
        "echo": true,
        "reveal": "always",
        "focus": false,
        "panel": "shared"
      },
      "problemMatcher": "$java"
    },
    {
      "label": "Run BookStore",
      "type": "shell",
      "command": "mvn",
      "args": ["exec:java", "-pl", "app"],
      "group": {
        "kind": "build",
        "isDefault": true
      },
      "presentation": {
        "echo": true,
        "reveal": "always",
        "focus": false,
        "panel": "shared"
      },
      "problemMatcher": []
    },
    {
      "label": "Run Tests",
      "type": "shell",
      "command": "mvn",
      "args": ["test"],
      "group": "test",
      "presentation": {
        "echo": true,
        "reveal": "always",
        "focus": false,
        "panel": "shared"
      },
      "problemMatcher": "$java"
    },
    {
      "label": "Open Scene Builder",
      "type": "shell",
      "command": "scenebuilder",
      "args": ["${file}"],
      "group": "build",
      "presentation": {
        "echo": true,
        "reveal": "silent",
        "focus": false,
        "panel": "shared"
      },
      "options": {
        "cwd": "${workspaceFolder}"
      }
    }
  ]
}
```

##### 4. Integração Scene Builder com VS Code

**Configurar abertura automática:**

1. **Associar arquivos .fxml ao Scene Builder:**

   ```bash
   # Definir Scene Builder como editor padrão para .fxml
   echo '[Default Applications]
   application/fxml=scenebuilder.desktop' >> ~/.local/share/applications/mimeapps.list

   # Criar entrada no menu de contexto
   mkdir -p ~/.local/share/file-manager/actions
   cat > ~/.local/share/file-manager/actions/scenebuilder.desktop << 'EOF'
   [Desktop Entry]
   Type=Action
   Name=Open with Scene Builder
   Icon=scenebuilder
   Profiles=profile-zero;

   [X-Action-Profile profile-zero]
   Exec=scenebuilder %f
   MimeTypes=application/xml;text/xml;
   Name=Default profile
   EOF
   ```

2. **Comando rápido no VS Code:**

   ```json
   // Em settings.json, adicionar:
   "files.associations": {
     "*.fxml": "xml"
   },
   "terminal.integrated.env.linux": {
     "SCENE_BUILDER_HOME": "/snap/scenebuilder/current"
   }
   ```

3. **Snippet para abrir Scene Builder:**

```json
// Em .vscode/settings.json
"editor.quickSuggestions": {
  "strings": true
},
"editor.suggest.snippetsPreventQuickSuggestions": false
```

**Criar `.vscode/fxml.code-snippets`:**

```json
{
  "Open in Scene Builder": {
    "prefix": "scenebuilder",
    "body": [
      "// Open current .fxml file in Scene Builder:",
      "// Ctrl+Shift+P -> Tasks: Run Task -> Open Scene Builder"
    ],
    "description": "Reminder to open FXML in Scene Builder"
  },
  "FXML Template": {
    "prefix": "fxml",
    "body": [
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
      "",
      "<?import javafx.geometry.Insets?>",
      "<?import javafx.scene.control.*?>",
      "<?import javafx.scene.layout.*?>",
      "",
      "<${1:VBox} xmlns=\"http://javafx.com/javafx/11.0.1\" xmlns:fx=\"http://javafx.com/fxml/1\"",
      "      fx:controller=\"${2:br.edu.ifba.inf008.controller.Controller}\">",
      "   <children>",
      "      $3",
      "   </children>",
      "   <padding>",
      "      <Insets bottom=\"20.0\" left=\"20.0\" right=\"20.0\" top=\"20.0\" />",
      "   </padding>",
      "</${1:VBox}>"
    ],
    "description": "FXML template with controller"
  },
  "JavaFX Controller": {
    "prefix": "fxcontroller",
    "body": [
      "package ${1:br.edu.ifba.inf008.controller};",
      "",
      "import javafx.fxml.FXML;",
      "import javafx.fxml.Initializable;",
      "import javafx.scene.control.*;",
      "import javafx.event.ActionEvent;",
      "",
      "import java.net.URL;",
      "import java.util.ResourceBundle;",
      "",
      "public class ${2:Controller} implements Initializable {",
      "",
      "    @Override",
      "    public void initialize(URL url, ResourceBundle resourceBundle) {",
      "        $3",
      "    }",
      "",
      "}"
    ],
    "description": "JavaFX Controller template"
  }
}
```

##### 5. Workflow Recomendado VS Code + Scene Builder

**Fluxo de Desenvolvimento:**

1. **Criar Layout no Scene Builder:**

   ```bash
   # 1. Criar novo arquivo .fxml no VS Code
   touch src/main/resources/fxml/new-interface.fxml

   # 2. Abrir no Scene Builder via Command Palette:
   # Ctrl+Shift+P -> Tasks: Run Task -> Open Scene Builder

   # 3. Ou via terminal integrado:
   scenebuilder src/main/resources/fxml/new-interface.fxml
   ```

2. **Definir Controller no Scene Builder:**

   - **Controller class:** `br.edu.ifba.inf008.controller.NewController`
   - **fx:id** para elementos que precisam de interação
   - **onAction** para botões e eventos

3. **Gerar Controller no VS Code:**

   ```java
   // Usar snippet fxcontroller
   // Ctrl+Shift+P -> Insert Snippet -> JavaFX Controller
   ```

4. **Conectar campos FXML:**

```java
@FXML private TextField nameField;
@FXML private Button saveButton;
@FXML private TableView<User> dataTable;
```

##### 6. Atalhos Produtivos VS Code

**Atalhos Customizados (keybindings.json):**

```json
[
  {
    "key": "ctrl+shift+s",
    "command": "workbench.action.tasks.runTask",
    "args": "Open Scene Builder",
    "when": "editorLangId == xml && resourceExtname == .fxml"
  },
  {
    "key": "ctrl+shift+r",
    "command": "workbench.action.tasks.runTask",
    "args": "Run BookStore"
  },
  {
    "key": "ctrl+shift+t",
    "command": "workbench.action.tasks.runTask",
    "args": "Run Tests"
  },
  {
    "key": "ctrl+shift+b",
    "command": "workbench.action.tasks.runTask",
    "args": "Clean and Compile"
  }
]
```

**Atalhos Úteis VS Code:**

```bash
Ctrl+Shift+P         # Command Palette
Ctrl+P               # Quick Open File
Ctrl+Shift+E         # Explorer
Ctrl+Shift+F         # Search in Files
Ctrl+Shift+G         # Source Control
Ctrl+Shift+X         # Extensions
Ctrl+`               # Terminal
Ctrl+Shift+`         # New Terminal
F5                   # Start Debugging
Ctrl+F5              # Run Without Debugging
Ctrl+Shift+D         # Debug View
Ctrl+K Ctrl+S        # Keyboard Shortcuts
Ctrl+,               # Settings
```

##### 7. Debug Configuration Avançada

**Multi-module debugging:**

```json
{
  "type": "java",
  "name": "Debug with Hot Reload",
  "request": "launch",
  "mainClass": "br.edu.ifba.inf008.App",
  "projectName": "app",
  "vmArgs": [
    "--module-path",
    "/home/john/javafx/javafx-sdk-21.0.2/lib",
    "--add-modules",
    "javafx.controls,javafx.fxml,javafx.web",
    "-Djavafx.verbose=true",
    "-XX:+AllowRedefinitionToAddDeleteMethods"
  ],
  "console": "integratedTerminal",
  "stopOnEntry": false,
  "stepFilters": {
    "skipClasses": ["java.*", "javax.*", "sun.*", "com.sun.*"],
    "skipSynthetics": true
  }
}
```

**Vantagens VS Code + Scene Builder:**

- **Leveza**: VS Code consome menos recursos que IDEs pesadas
- **Extensibilidade**: Marketplace rico em extensões
- **Git integrado**: GitLens oferece controle de versão avançado
- **Terminal integrado**: Acesso direto ao shell
- **Multi-projeto**: Suporte nativo a workspaces
- **Customização**: Altamente configurável
- **Gratuito**: Completamente free e open source
- **Multi-plataforma**: Funciona em Linux, Windows, macOS
- **Scene Builder**: Integração perfeita para design visual FXML
- **Live reload**: Mudanças refletidas rapidamente
- **IntelliSense**: Autocompletar Java robusto
- **Debugging**: Debug visual poderoso

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

/* ========== ROOT STYLES ========== */
.root {
  -fx-font-family: "Segoe UI", "Roboto", "Arial", sans-serif;
  -fx-font-size: 14px;
  -fx-base: #ffffff;
  -fx-background: #f5f5f5;
}

/* ========== HEADER ========== */
.header-container {
  -fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);
  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 2, 0, 0, 1);
}

.app-bar {
  -fx-padding: 10 20;
  -fx-spacing: 15;
}

.app-title {
  -fx-text-fill: white;
  -fx-font-size: 20px;
  -fx-font-weight: bold;
}

.header-actions .icon-button {
  -fx-background-color: transparent;
  -fx-text-fill: white;
  -fx-font-size: 16px;
  -fx-padding: 8;
  -fx-background-radius: 20;
  -fx-min-width: 36px;
  -fx-min-height: 36px;
}

.header-actions .icon-button:hover {
  -fx-background-color: rgba(255, 255, 255, 0.1);
}

.nav-bar {
  -fx-background-color: #34495e;
  -fx-padding: 0 20 5 20;
  -fx-spacing: 0;
}

/* ========== SIDEBAR ========== */
.side-panel {
  -fx-background-color: white;
  -fx-border-color: #e0e0e0;
  -fx-border-width: 0 1 0 0;
  -fx-padding: 20;
}

.side-section {
  -fx-spacing: 10;
  -fx-padding: 0 0 20 0;
}

.side-section-title {
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
  -fx-font-size: 12px;
  -fx-padding: 0 0 5 0;
}

.quick-actions .quick-action-btn {
  -fx-background-color: #ecf0f1;
  -fx-text-fill: #2c3e50;
  -fx-padding: 8 12;
  -fx-background-radius: 6;
  -fx-border-radius: 6;
  -fx-alignment: CENTER_LEFT;
  -fx-font-size: 13px;
}

.quick-actions .quick-action-btn:hover {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
}

.stats-grid .stat-label {
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
}

.stats-grid .stat-value {
  -fx-text-fill: #2c3e50;
  -fx-font-weight: bold;
  -fx-font-size: 12px;
}

.activity-list {
  -fx-background-color: transparent;
  -fx-border-color: transparent;
}

.activity-list .list-cell {
  -fx-background-color: transparent;
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
  -fx-padding: 4 0;
}

/* ========== MAIN CONTENT ========== */
.main-content {
  -fx-background-color: #f8f9fa;
}

.welcome-pane {
  -fx-background-color: white;
}

.welcome-content {
  -fx-padding: 60;
}

.welcome-title {
  -fx-font-size: 32px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
}

.welcome-subtitle {
  -fx-font-size: 16px;
  -fx-text-fill: #7f8c8d;
}

.welcome-cards {
  -fx-padding: 20 0;
}

.welcome-card {
  -fx-background-color: white;
  -fx-padding: 30;
  -fx-background-radius: 10;
  -fx-border-radius: 10;
  -fx-border-color: #e0e0e0;
  -fx-border-width: 1;
  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 4, 0, 0, 2);
  -fx-min-width: 200;
  -fx-spacing: 10;
}

.welcome-card:hover {
  -fx-border-color: #3498db;
  -fx-effect: dropshadow(three-pass-box, rgba(52, 152, 219, 0.3), 8, 0, 0, 4);
}

.card-icon {
  -fx-font-size: 48px;
}

.card-title {
  -fx-font-size: 18px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
}

.card-description {
  -fx-font-size: 12px;
  -fx-text-fill: #7f8c8d;
  -fx-text-alignment: center;
  -fx-wrap-text: true;
}

.card-button {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
  -fx-background-radius: 6;
  -fx-border-radius: 6;
  -fx-padding: 8 16;
  -fx-font-weight: bold;
}

.card-button:hover {
  -fx-background-color: #2980b9;
}

/* ========== TAB PANE ========== */
.main-tab-pane {
  -fx-background-color: white;
}

.main-tab-pane .tab-header-area {
  -fx-background-color: #ecf0f1;
  -fx-border-color: #bdc3c7;
  -fx-border-width: 0 0 1 0;
}

.main-tab-pane .tab {
  -fx-background-color: transparent;
  -fx-border-color: transparent;
  -fx-padding: 10 20;
}

.main-tab-pane .tab:selected {
  -fx-background-color: white;
  -fx-border-color: #3498db;
  -fx-border-width: 0 0 2 0;
}

.main-tab-pane .tab .tab-label {
  -fx-text-fill: #2c3e50;
  -fx-font-weight: bold;
}

/* ========== STATUS BAR ========== */
.status-bar {
  -fx-background-color: #ecf0f1;
  -fx-border-color: #bdc3c7;
  -fx-border-width: 1 0 0 0;
  -fx-padding: 5 20;
}

.status-text {
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
}

.connection-status {
  -fx-font-size: 12px;
}

.time-label {
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
}

/* ========== ANIMATIONS ========== */
.welcome-card {
  -fx-scale-x: 1;
  -fx-scale-y: 1;
}

.welcome-card:hover {
  -fx-scale-x: 1.02;
  -fx-scale-y: 1.02;
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

## Personalização da Tela Principal

### Modificando o UIController

O `UIController` é responsável pela janela principal da aplicação. Para personalizar a interface principal:

#### 1. Localização dos Arquivos

```bash
app/src/main/java/br/edu/ifba/inf008/shell/UIController.java
app/src/main/resources/fxml/main-window.fxml (criar se não existir)
app/src/main/resources/css/main-style.css (criar se não existir)
```

#### 2. Estrutura da Janela Principal Moderna

**Criar main-window.fxml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.image.ImageView?>
<?import javafx.scene.image.Image?>

<BorderPane xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="br.edu.ifba.inf008.shell.UIController"
            stylesheets="@../css/main-style.css">

    <!-- Header/Top Bar -->
    <top>
        <VBox styleClass="header-container">
            <!-- App Bar -->
            <HBox styleClass="app-bar" alignment="CENTER_LEFT">
                <children>
                    <ImageView fitHeight="32" fitWidth="32" preserveRatio="true">
                        <image>
                            <Image url="@../images/bookstore-icon.png"/>
                        </image>
                    </ImageView>
                    <Label styleClass="app-title" text="BookStore Blackbird"/>
                    <Region HBox.hgrow="ALWAYS"/>
                    <HBox styleClass="header-actions" spacing="10">
                        <Button styleClass="icon-button" text="🔍"/>
                        <Button styleClass="icon-button" text="⚙️"/>
                        <Button styleClass="icon-button" text="👤"/>
                    </HBox>
                </children>
            </HBox>

            <!-- Navigation Bar -->
            <HBox fx:id="navigationBar" styleClass="nav-bar">
                <!-- Menus dos plugins serão adicionados aqui dinamicamente -->
            </HBox>
        </VBox>
    </top>

    <!-- Sidebar/Left Panel -->
    <left>
        <VBox fx:id="sidePanel" styleClass="side-panel" prefWidth="250">
            <children>
                <!-- Quick Actions -->
                <VBox styleClass="side-section">
                    <Label styleClass="side-section-title" text="Quick Actions"/>
                    <VBox styleClass="quick-actions" spacing="5">
                        <Button styleClass="quick-action-btn" text="📚 New Book"/>
                        <Button styleClass="quick-action-btn" text="👤 New User"/>
                        <Button styleClass="quick-action-btn" text="📝 New Loan"/>
                        <Button styleClass="quick-action-btn" text="📊 Reports"/>
                    </VBox>
                </VBox>

                <!-- Statistics -->
                <VBox styleClass="side-section">
                    <Label styleClass="side-section-title" text="Statistics"/>
                    <GridPane styleClass="stats-grid" hgap="10" vgap="5">
                        <Label styleClass="stat-label" text="Total Books:" GridPane.columnIndex="0" GridPane.rowIndex="0"/>
                        <Label fx:id="totalBooksLabel" styleClass="stat-value" text="0" GridPane.columnIndex="1" GridPane.rowIndex="0"/>

                        <Label styleClass="stat-label" text="Active Loans:" GridPane.columnIndex="0" GridPane.rowIndex="1"/>
                        <Label fx:id="activeLoansLabel" styleClass="stat-value" text="0" GridPane.columnIndex="1" GridPane.rowIndex="1"/>

                        <Label styleClass="stat-label" text="Users:" GridPane.columnIndex="0" GridPane.rowIndex="2"/>
                        <Label fx:id="totalUsersLabel" styleClass="stat-value" text="0" GridPane.columnIndex="1" GridPane.rowIndex="2"/>
                    </GridPane>
                </VBox>

                <!-- Recent Activity -->
                <VBox styleClass="side-section" VBox.vgrow="ALWAYS">
                    <Label styleClass="side-section-title" text="Recent Activity"/>
                    <ListView fx:id="activityList" styleClass="activity-list"/>
                </VBox>
            </children>
        </VBox>
    </left>

    <!-- Main Content Area -->
    <center>
        <VBox styleClass="main-content">
            <!-- Welcome/Dashboard quando nenhuma aba está aberta -->
            <StackPane fx:id="welcomePane" styleClass="welcome-pane">
                <VBox styleClass="welcome-content" alignment="CENTER" spacing="20">
                    <Label styleClass="welcome-title" text="Welcome to BookStore Blackbird"/>
                    <Label styleClass="welcome-subtitle" text="Select an option from the menu to get started"/>

                    <!-- Cards de ação rápida -->
                    <HBox styleClass="welcome-cards" spacing="20" alignment="CENTER">
                        <VBox styleClass="welcome-card" alignment="CENTER" spacing="10">
                            <Label styleClass="card-icon" text="📚"/>
                            <Label styleClass="card-title" text="Manage Books"/>
                            <Label styleClass="card-description" text="Add, edit and organize your book catalog"/>
                            <Button styleClass="card-button" text="Open Books"/>
                        </VBox>

                        <VBox styleClass="welcome-card" alignment="CENTER" spacing="10">
                            <Label styleClass="card-icon" text="👥"/>
                            <Label styleClass="card-title" text="Manage Users"/>
                            <Label styleClass="card-description" text="Register and manage library users"/>
                            <Button styleClass="card-button" text="Open Users"/>
                        </VBox>

                        <VBox styleClass="welcome-card" alignment="CENTER" spacing="10">
                            <Label styleClass="card-icon" text="📋"/>
                            <Label styleClass="card-title" text="Loan Management"/>
                            <Label styleClass="card-description" text="Track book loans and returns"/>
                            <Button styleClass="card-button" text="Open Loans"/>
                        </VBox>
                    </HBox>
                </VBox>
            </StackPane>

            <!-- Tab Pane para conteúdo dos plugins -->
            <TabPane fx:id="mainTabPane" styleClass="main-tab-pane" VBox.vgrow="ALWAYS">
                <!-- Abas dos plugins serão adicionadas aqui -->
            </TabPane>
        </VBox>
    </center>

    <!-- Footer/Status Bar -->
    <bottom>
        <HBox styleClass="status-bar" alignment="CENTER_LEFT">
            <Label fx:id="statusLabel" styleClass="status-text" text="Ready"/>
            <Region HBox.hgrow="ALWAYS"/>
            <HBox styleClass="status-info" spacing="10">
                <Label fx:id="connectionStatus" styleClass="connection-status" text="🟢 Connected"/>
                <Label fx:id="timeLabel" styleClass="time-label" text=""/>
            </HBox>
        </HBox>
    </bottom>
</BorderPane>
```

#### 3. CSS Moderno (main-style.css)

```css
/* ========== ROOT STYLES ========== */
.root {
  -fx-font-family: "Segoe UI", "Roboto", "Arial", sans-serif;
  -fx-font-size: 14px;
  -fx-base: #ffffff;
  -fx-background: #f5f5f5;
}

/* ========== HEADER ========== */
.header-container {
  -fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);
  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 2, 0, 0, 1);
}

.app-bar {
  -fx-padding: 10 20;
  -fx-spacing: 15;
}

.app-title {
  -fx-text-fill: white;
  -fx-font-size: 20px;
  -fx-font-weight: bold;
}

.header-actions .icon-button {
  -fx-background-color: transparent;
  -fx-text-fill: white;
  -fx-font-size: 16px;
  -fx-padding: 8;
  -fx-background-radius: 20;
  -fx-min-width: 36px;
  -fx-min-height: 36px;
}

.header-actions .icon-button:hover {
  -fx-background-color: rgba(255, 255, 255, 0.1);
}

.nav-bar {
  -fx-background-color: #34495e;
  -fx-padding: 0 20 5 20;
  -fx-spacing: 0;
}

/* ========== SIDEBAR ========== */
.side-panel {
  -fx-background-color: white;
  -fx-border-color: #e0e0e0;
  -fx-border-width: 0 1 0 0;
  -fx-padding: 20;
}

.side-section {
  -fx-spacing: 10;
  -fx-padding: 0 0 20 0;
}

.side-section-title {
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
  -fx-font-size: 12px;
  -fx-padding: 0 0 5 0;
}

.quick-actions .quick-action-btn {
  -fx-background-color: #ecf0f1;
  -fx-text-fill: #2c3e50;
  -fx-padding: 8 12;
  -fx-background-radius: 6;
  -fx-border-radius: 6;
  -fx-alignment: CENTER_LEFT;
  -fx-font-size: 13px;
}

.quick-actions .quick-action-btn:hover {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
}

.stats-grid .stat-label {
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
}

.stats-grid .stat-value {
  -fx-text-fill: #2c3e50;
  -fx-font-weight: bold;
  -fx-font-size: 12px;
}

.activity-list {
  -fx-background-color: transparent;
  -fx-border-color: transparent;
}

.activity-list .list-cell {
  -fx-background-color: transparent;
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
  -fx-padding: 4 0;
}

/* ========== MAIN CONTENT ========== */
.main-content {
  -fx-background-color: #f8f9fa;
}

.welcome-pane {
  -fx-background-color: white;
}

.welcome-content {
  -fx-padding: 60;
}

.welcome-title {
  -fx-font-size: 32px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
}

.welcome-subtitle {
  -fx-font-size: 16px;
  -fx-text-fill: #7f8c8d;
}

.welcome-cards {
  -fx-padding: 20 0;
}

.welcome-card {
  -fx-background-color: white;
  -fx-padding: 30;
  -fx-background-radius: 10;
  -fx-border-radius: 10;
  -fx-border-color: #e0e0e0;
  -fx-border-width: 1;
  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 4, 0, 0, 2);
  -fx-min-width: 200;
  -fx-spacing: 10;
}

.welcome-card:hover {
  -fx-border-color: #3498db;
  -fx-effect: dropshadow(three-pass-box, rgba(52, 152, 219, 0.3), 8, 0, 0, 4);
}

.card-icon {
  -fx-font-size: 48px;
}

.card-title {
  -fx-font-size: 18px;
  -fx-font-weight: bold;
  -fx-text-fill: #2c3e50;
}

.card-description {
  -fx-font-size: 12px;
  -fx-text-fill: #7f8c8d;
  -fx-text-alignment: center;
  -fx-wrap-text: true;
}

.card-button {
  -fx-background-color: #3498db;
  -fx-text-fill: white;
  -fx-background-radius: 6;
  -fx-border-radius: 6;
  -fx-padding: 8 16;
  -fx-font-weight: bold;
}

.card-button:hover {
  -fx-background-color: #2980b9;
}

/* ========== TAB PANE ========== */
.main-tab-pane {
  -fx-background-color: white;
}

.main-tab-pane .tab-header-area {
  -fx-background-color: #ecf0f1;
  -fx-border-color: #bdc3c7;
  -fx-border-width: 0 0 1 0;
}

.main-tab-pane .tab {
  -fx-background-color: transparent;
  -fx-border-color: transparent;
  -fx-padding: 10 20;
}

.main-tab-pane .tab:selected {
  -fx-background-color: white;
  -fx-border-color: #3498db;
  -fx-border-width: 0 0 2 0;
}

.main-tab-pane .tab .tab-label {
  -fx-text-fill: #2c3e50;
  -fx-font-weight: bold;
}

/* ========== STATUS BAR ========== */
.status-bar {
  -fx-background-color: #ecf0f1;
  -fx-border-color: #bdc3c7;
  -fx-border-width: 1 0 0 0;
  -fx-padding: 5 20;
}

.status-text {
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
}

.connection-status {
  -fx-font-size: 12px;
}

.time-label {
  -fx-text-fill: #7f8c8d;
  -fx-font-size: 12px;
}

/* ========== ANIMATIONS ========== */
.welcome-card {
  -fx-scale-x: 1;
  -fx-scale-y: 1;
}

.welcome-card:hover {
  -fx-scale-x: 1.02;
  -fx-scale-y: 1.02;
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

---

# ENGLISH SECTION: FXML ClassLoader Solution Guide

## Critical Issue: ClassNotFoundException in Plugin FXML Loading

### Problem Description

When loading FXML files from plugin JARs, you may encounter:

```
Caused by: java.lang.ClassNotFoundException: br.edu.ifba.inf008.plugins.user.ui.UserViewController
```

This occurs because FXML loaders use the default context classloader, which doesn't have access to classes loaded by plugin-specific URLClassLoaders.

### Root Cause

In microkernel architectures:

1. Plugins are loaded with separate URLClassLoaders
2. FXMLLoader uses the context classloader by default
3. Context classloader cannot see plugin-specific classes
4. Controller instantiation fails during FXML loading

### Solution: Explicit ClassLoader Configuration

**Critical Fix**: Always set the classloader for FXMLLoader when loading from plugins:

```java
// ❌ WRONG - Will fail with ClassNotFoundException
FXMLLoader loader = new FXMLLoader(
    classLoader.getResource("path/to/fxml")
);
Node content = loader.load(); // Fails here

// ✅ CORRECT - Works in plugin architecture
ClassLoader classLoader = getClass().getClassLoader();
FXMLLoader loader = new FXMLLoader(
    classLoader.getResource("br/edu/ifba/inf008/plugins/user/ui/user-management.fxml")
);
loader.setClassLoader(classLoader); // Essential for plugins!
Node content = loader.load(); // Works correctly
```

### Complete Working Plugin Example

```java
package br.edu.ifba.inf008.plugins;

import java.io.IOException;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public class UserPlugin implements IPlugin {
    @Override
    public boolean init() {
        System.out.println("🔌 UserPlugin...");

        try {
            IUIController uiController = ICore.getInstance().getUIController();
            MenuItem menuItem = uiController.createMenuItem("Users", "Manage Users");

            menuItem.setOnAction(event -> {
                System.out.println("🎯 Opening Users Interface...");
                try {
                    // Use the plugin's classloader for resource loading
                    ClassLoader classLoader = getClass().getClassLoader();
                    FXMLLoader loader = new FXMLLoader(
                        classLoader.getResource("br/edu/ifba/inf008/plugins/user/ui/user-management.fxml")
                    );

                    // CRITICAL: Set classloader for controller instantiation
                    loader.setClassLoader(classLoader);

                    Node content = loader.load();
                    uiController.createTab("👥 User Management", content);
                    System.out.println("✅ Interface loaded successfully!");

                } catch(IOException e) {
                    System.err.println("❌ Error opening Users Interface: " + e.getMessage());
                    e.printStackTrace();
                    uiController.showAlert("Error", "Failed to open Users Interface: " + e.getMessage());
                }
            });

            System.out.println("✅ UserPlugin initialized successfully!");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error initializing UserPlugin: " + e.getMessage());
            return false;
        }
    }
}
```

### Testing the Solution

Create unit tests to verify FXML loading works:

```java
@Test
public void testFXMLLoading() {
    // Initialize JavaFX toolkit
    Platform.startup(() -> {});

    assertDoesNotThrow(() -> {
        ClassLoader classLoader = getClass().getClassLoader();
        FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/user/ui/user-management.fxml")
        );

        // Essential fix for plugin architectures
        loader.setClassLoader(classLoader);

        Node content = loader.load();
        assertNotNull(content, "FXML content should be loaded successfully");

        UserViewController controller = loader.getController();
        assertNotNull(controller, "Controller should be instantiated");
    });
}
```

### VS Code + Scene Builder Workflow

1. **Install Scene Builder**: Download from [Gluon](https://gluonhq.com/products/scene-builder/)
2. **Create FXML**: In `src/main/resources` with proper package structure
3. **Design in Scene Builder**: Right-click FXML → "Open with Scene Builder"
4. **Set Controller**: In Scene Builder, specify the controller class
5. **Implement Controller**: Create Java class with @FXML annotations
6. **Test Integration**: Use the corrected classloader approach

### Debugging Tips

```bash
# Verify FXML is in plugin JAR
jar tf plugins/UserPlugin.jar | grep user-management.fxml

# Check class inclusion
jar tf plugins/UserPlugin.jar | grep UserViewController.class

# Run with detailed output
mvn exec:java -pl app
```

### Key Takeaways

1. **Always use `loader.setClassLoader(classLoader)`** when loading FXML from plugins
2. **Use the plugin's classloader** for both resource loading and controller instantiation
3. **Test FXML loading independently** before full plugin integration
4. **Verify resource inclusion** in plugin JARs during build process

This solution resolves the ClassNotFoundException and enables seamless FXML integration in microkernel plugin architectures.
