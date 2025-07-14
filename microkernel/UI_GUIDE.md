# 🎨 Guia de Interface de Usuário - BookStore

> Guia completo para desenvolvimento de interfaces JavaFX no sistema BookStore

## 📋 Visão Geral

O BookStore utiliza **JavaFX** como framework de interface gráfica, implementando uma arquitetura modular onde cada plugin pode contribuir com suas próprias interfaces através do padrão **FXML + Controller**.

### Características da UI

- **🏗️ Arquitetura Modular**: Cada plugin cria suas próprias interfaces
- **🎨 Design Responsivo**: Layouts que se adaptam ao conteúdo
- **🎭 Tema Consistente**: CSS centralizado para aparência unificada
- **🔄 Interação Dinâmica**: Menus e cards criados dinamicamente
- **📱 Componentes Reutilizáveis**: ListView, FormFields, Cards padronizados

## 🏗️ Estrutura da Interface Principal

### Componentes Principais

```text
┌────────────────────────────────────────┐
│ MenuBar (Plugins contribuem)           │
├────────────────────────────────────────┤
│ TabPane                                │
│ ├── 🏠 Home (Cards dos plugins)        │
│ ├── 👥 User Management                 │
│ ├── 📚 Book Management                 │
│ └── 📋 Loan Management                 │
└────────────────────────────────────────┘
```

### Home Screen - Cards Dinâmicos

A tela inicial apresenta cards que são criados dinamicamente pelos plugins:

```java
// Plugin adiciona seu card
uiController.addPluginCard(
    "user-plugin",           // ID único
    "👥",                   // Ícone
    "User Management",       // Título
    "Manage library users.", // Descrição
    this::openUserInterface  // Ação ao clicar
);
```

## 🔌 Desenvolvimento de Interface para Plugins

### 1. Estrutura Recomendada

```text
src/main/
├── java/br/edu/ifba/inf008/plugins/meu/
│   └── ui/
│       ├── MeuController.java
│       └── model/
│           └── MeuViewModel.java
└── resources/br/edu/ifba/inf008/plugins/meu/ui/
    ├── minha-interface.fxml
    └── css/
        └── meu-estilo.css
```

### 2. Padrão FXML + Controller

#### Arquivo FXML (minha-interface.fxml)

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import java.net.URL?>

<BorderPane xmlns="http://javafx.com/javafx/24.0.1"
           xmlns:fx="http://javafx.com/fxml/1"
           fx:controller="br.edu.ifba.inf008.plugins.meu.ui.MeuController">

  <!-- CSS opcional -->
  <stylesheets>
    <URL value="@css/meu-estilo.css" />
  </stylesheets>

  <padding>
    <Insets bottom="25" left="25" right="25" top="25" />
  </padding>

  <left>
    <VBox spacing="20" prefWidth="400" styleClass="left-panel">
      <!-- Formulários e controles -->
    </VBox>
  </left>

  <center>
    <ListView fx:id="minhaListView" styleClass="custom-list-view"/>
  </center>
</BorderPane>
```

#### Controller JavaFX

```java
public class MeuController {

    // Injeção automática de componentes FXML
    @FXML private ListView<MinhaEntidade> minhaListView;
    @FXML private TextField nomeField;
    @FXML private Button salvarButton;

    // Dependências
    private MeuDAO dao = new MeuDAO();
    private IUIController uiController = ICore.getInstance().getUIController();
    private final ObservableList<MinhaEntidade> items = FXCollections.observableArrayList();

    /**
     * Inicialização automática após carregamento do FXML
     */
    @FXML
    public void initialize() {
        carregarDadosIniciais();
        configurarCellFactory();
        configurarEventListeners();
    }

    /**
     * Handler para eventos de botão
     */
    @FXML
    private void handleSalvar() {
        String nome = nomeField.getText();

        // Validação
        if (nome.isEmpty()) {
            uiController.showAlert("Erro", "Nome é obrigatório");
            return;
        }

        // Confirmação
        uiController.showConfirmation(
            "Confirmar",
            "Salvar registro?",
            () -> {
                try {
                    // Lógica de negócio
                    MinhaEntidade nova = new MinhaEntidade(nome);
                    dao.save(nova);
                    items.add(nova);

                    // Feedback
                    uiController.showAlert("Sucesso", "Registro salvo!");
                    limparFormulario();
                } catch (Exception e) {
                    uiController.showAlert("Erro", "Falha ao salvar: " + e.getMessage());
                }
            }
        );
    }
}
```

### 3. Carregamento da Interface no Plugin

```java
public class MeuPlugin implements IPlugin {

    @Override
    public boolean init() {
        try {
            IUIController ui = ICore.getInstance().getUIController();

            // Criar menu
            MenuItem menu = ui.createMenuItem("Management", "Meus Dados");
            menu.setOnAction(e -> abrirInterface());

            // Adicionar card
            ui.addPluginCard("meu-plugin", "🔧", "Meus Dados",
                           "Gerenciar meus dados", this::abrirInterface);

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao inicializar plugin: " + e.getMessage());
            return false;
        }
    }

    private void abrirInterface() {
        try {
            // Carregar FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/edu/ifba/inf008/plugins/meu/ui/minha-interface.fxml")
            );

            Node content = loader.load();

            // Criar aba
            ICore.getInstance().getUIController()
                .createTab("🔧 Meus Dados", content);

        } catch (IOException e) {
            ICore.getInstance().getUIController()
                .showAlert("Erro", "Falha ao carregar interface: " + e.getMessage());
        }
    }
}
```

## 📝 Padrões de Componentes

### 1. ListView com Botões de Ação

```java
private void configurarCellFactory() {
    minhaListView.setCellFactory(lv -> new ListCell<MinhaEntidade>() {
        private final HBox hbox = new HBox(10);
        private final Label label = new Label();
        private final Button editButton = new Button("✏️");
        private final Button deleteButton = new Button("🗑️");
        private final Pane spacer = new Pane();

        {
            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(label, spacer, editButton, deleteButton);

            editButton.getStyleClass().add("button-info");
            deleteButton.getStyleClass().add("button-danger");
        }

        @Override
        protected void updateItem(MinhaEntidade item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                label.setText(item.getNome());
                setGraphic(hbox);

                editButton.setOnAction(e -> handleEditar(item));
                deleteButton.setOnAction(e -> handleDeletar(item));
            }
        }
    });
}
```

### 2. Formulário com Validação

```java
private boolean validarFormulario() {
    List<String> erros = new ArrayList<>();

    if (nomeField.getText().trim().isEmpty()) {
        erros.add("Nome é obrigatório");
    }

    if (emailField.getText().trim().isEmpty()) {
        erros.add("Email é obrigatório");
    } else if (!emailField.getText().contains("@")) {
        erros.add("Email deve ter formato válido");
    }

    if (!erros.isEmpty()) {
        uiController.showAlert("Validação", String.join("\n", erros));
        return false;
    }

    return true;
}
```

### 3. Busca e Filtros

```java
@FXML
private void handleBuscar() {
    String termo = buscaField.getText().toLowerCase().trim();

    if (termo.isEmpty()) {
        minhaListView.setItems(todosItems);
    } else {
        ObservableList<MinhaEntidade> filtrados = todosItems.filtered(
            item -> item.getNome().toLowerCase().contains(termo) ||
                   item.getDescricao().toLowerCase().contains(termo)
        );
        minhaListView.setItems(filtrados);
    }
}
```

## 🎨 Estilos CSS

### CSS Base (aplicado automaticamente)

O sistema aplica estilos base automaticamente. Para personalizar, crie arquivo CSS:

```css
/* meu-estilo.css */

.left-panel {
  -fx-background-color: #f8f9fa;
  -fx-padding: 20px;
  -fx-spacing: 15px;
}

.custom-list-view {
  -fx-background-color: white;
  -fx-border-color: #dee2e6;
  -fx-border-width: 1px;
}

.custom-list-view .list-cell {
  -fx-padding: 12px;
  -fx-border-color: transparent transparent #e9ecef transparent;
  -fx-border-width: 0 0 1px 0;
}

.custom-list-view .list-cell:selected {
  -fx-background-color: #e3f2fd;
}

.button-primary {
  -fx-background-color: #007bff;
  -fx-text-fill: white;
  -fx-font-weight: bold;
  -fx-padding: 8px 16px;
}

.button-danger {
  -fx-background-color: #dc3545;
  -fx-text-fill: white;
  -fx-font-size: 12px;
  -fx-padding: 4px 8px;
}

.button-info {
  -fx-background-color: #17a2b8;
  -fx-text-fill: white;
  -fx-font-size: 12px;
  -fx-padding: 4px 8px;
}
```

## 🧪 Testes de Interface

### Testes Unitários de Controller

```java
@ExtendWith(MockitoExtension.class)
class MeuControllerTest {

    @Mock private MeuDAO mockDao;
    @Mock private IUIController mockUiController;

    @InjectMocks private MeuController controller;

    @Test
    void testInicializacao() {
        // Given
        when(mockDao.findAll()).thenReturn(Arrays.asList(
            new MinhaEntidade("Teste 1"),
            new MinhaEntidade("Teste 2")
        ));

        // When
        controller.initialize();

        // Then
        assertEquals(2, controller.getItems().size());
        verify(mockDao).findAll();
    }

    @Test
    void testValidacaoFormulario() {
        // Given
        controller.getNomeField().setText("");

        // When
        boolean valido = controller.validarFormulario();

        // Then
        assertFalse(valido);
        verify(mockUiController).showAlert(eq("Validação"), anyString());
    }
}
```

### Testes de Integração de Interface

```java
@TestMethodOrder(OrderAnnotation.class)
class IntegracaoUITest {

    @Test
    @Order(1)
    void testCarregarInterface() {
        // Verifica se FXML carrega sem erros
        assertDoesNotThrow(() -> {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/minha-interface.fxml")
            );
            loader.load();
        });
    }

    @Test
    @Order(2)
    void testPluginRegistraInterface() {
        // Verifica se plugin registra interface corretamente
        MeuPlugin plugin = new MeuPlugin();
        assertTrue(plugin.init());

        // Verificar se menu foi criado
        // Verificar se card foi adicionado
    }
}
```

## 📚 Boas Práticas

### 1. Organização de Código

- **Separar responsabilidades**: Controller apenas coordena, lógica no DAO/Service
- **Usar ViewModels**: Para lógica complexa de apresentação
- **Lazy Loading**: Carregar dados conforme necessário
- **Cache local**: Manter cópia dos dados para performance

### 2. Tratamento de Erros

```java
private void executarComTratamento(Runnable acao, String operacao) {
    try {
        acao.run();
    } catch (Exception e) {
        logger.error("Erro em " + operacao, e);
        uiController.showAlert("Erro",
            "Falha em " + operacao + ": " + e.getMessage());
    }
}
```

### 3. Feedback ao Usuário

```java
private void operacaoComFeedback(String operacao, Runnable acao) {
    uiController.showConfirmation(
        "Confirmar " + operacao,
        "Tem certeza que deseja " + operacao.toLowerCase() + "?",
        () -> {
            try {
                acao.run();
                uiController.showAlert("Sucesso", operacao + " realizada com sucesso!");
            } catch (Exception e) {
                uiController.showAlert("Erro", "Falha em " + operacao + ": " + e.getMessage());
            }
        }
    );
}
```

### 4. Performance

- **ObservableList**: Use para listas que mudam frequentemente
- **Cell Factory**: Reutilize células para melhor performance
- **Lazy Loading**: Carregue dados sob demanda
- **Debounce**: Para campos de busca em tempo real

## 🔧 Debugging e Troubleshooting

### Problemas Comuns

1. **FXML não carrega**

   - Verificar caminho do arquivo
   - Confirmar que controller está no classpath
   - Verificar sintaxe do FXML

2. **@FXML não injeta**

   - Confirmar fx:id no FXML
   - Verificar nome do campo no controller
   - Garantir que initialize() é chamado

3. **Estilos não aplicam**
   - Verificar caminho do CSS
   - Confirmar sintaxe CSS
   - Verificar ordem de carregamento

### Ferramentas de Debug

```java
// Log de carregamento FXML
System.out.println("Carregando FXML: " + fxmlPath);

// Verificar componentes injetados
@FXML
public void initialize() {
    System.out.println("ListView injetado: " + (minhaListView != null));
    System.out.println("Botão injetado: " + (salvarButton != null));
}

// Verificar dados carregados
private void carregarDados() {
    List<MinhaEntidade> dados = dao.findAll();
    System.out.println("Dados carregados: " + dados.size() + " itens");
    items.setAll(dados);
}
```

---
