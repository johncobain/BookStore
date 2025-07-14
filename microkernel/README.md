# 📚 BookStore Microkernel

> Sistema de gerenciamento de livraria baseado em arquitetura de microkernel com plugins modulares

[![Java](https://img.shields.io/badge/Java-24-blue.svg)](https://adoptium.net/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-green.svg)](https://maven.apache.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-24-orange.svg)](https://openjfx.io/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.4+-red.svg)](https://hibernate.org/)

## 🚀 Início Rápido

### Pré-requisitos

- Java 24 (recomendado via SDKMAN)
- Maven 3.8+
- MariaDB/MySQL (via Docker)

### Compilação e Execução

```bash
# Compilar projeto
mvn clean install

# Executar aplicação
mvn exec:java -pl app

# Executar testes
mvn test
```

## 🏗️ Arquitetura Modular

### Componentes Principais

┌───────────────────────────────────────────────────────────────────────────────────────┐
│ Componente │ Descrição │ Status │
├───────────────────│───────────────────────────────────────────│───────────────────────┤
│ **User Plugin** │ Gerenciamento de usuários com UI completa │ ✅ Implementado │
│ **Book Plugin** │ Gerenciamento de livros │ 🔄 Em desenvolvimento │
│ **Loan Plugin** │ Controle de empréstimos │ 🔄 Em desenvolvimento │
│ **Report Plugin** │ Relatórios e analytics │ 🔄 Em desenvolvimento │
└───────────────────────────────────────────────────────────────────────────────────────┘

### Características Arquiteturais

- **🔧 Microkernel Pattern**: Core minimalista com funcionalidades em plugins
- **🔗 Baixo Acoplamento**: Plugins se comunicam via interfaces bem definidas
- **📦 Modularidade**: Plugins podem ser removidos sem quebrar o sistema
- **🗄️ Persistência Centralizada**: Hibernate com EntityManager compartilhado
- **🎨 UI Dinâmica**: Interface JavaFX construída dinamicamente pelos plugins

## 🔌 Desenvolvimento de Plugins

### Estrutura Padrão

```text
plugins/meu-plugin/
├── pom.xml
└── src/main/java/br/edu/ifba/inf008/plugins/
    ├── MeuPlugin.java              # Classe principal
    └── meu/
        ├── model/                  # Modelos (se necessário)
        ├── persistence/            # DAOs
        │   ├── JPAUtil.java       # Delegação para core
        │   └── MeuDAO.java        # DAO específico
        └── ui/                     # Interface gráfica
            ├── controller/         # Controllers JavaFX
            └── fxml/              # Arquivos FXML
```

### Exemplo de Plugin Básico

```java
@Component("meu-plugin")
public class MeuPlugin implements IPlugin {

    @Override
    public boolean init() {
        try {
            IUIController ui = ICore.getInstance().getUIController();

            // Criar menu
            MenuItem menu = ui.createMenuItem("Management", "Meus Dados");
            menu.setOnAction(e -> abrirInterface());

            // Adicionar card na tela inicial
            ui.addPluginCard(
                "meu-plugin", "🔧", "Meu Plugin",
                "Descrição do plugin", this::abrirInterface
            );

            return true;
        } catch (Exception e) {
            logger.error("Erro ao inicializar plugin", e);
            return false;
        }
    }
}
```

### DAO com BaseDAO

```java
public class MeuDAO extends BaseDAO<MinhaEntidade, Long> {

    @Override
    protected Class<MinhaEntidade> getEntityClass() {
        return MinhaEntidade.class;
    }

    // Métodos específicos
    public List<MinhaEntidade> findByStatus(Status status) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery(
                "SELECT e FROM MinhaEntidade e WHERE e.status = :status",
                MinhaEntidade.class)
                .setParameter("status", status)
                .getResultList();
        }
    }
}
```

## 📊 Padrões de Persistência

### Entidades Centralizadas

Modelos compartilhados ficam em `app/src/main/java/br/edu/ifba/inf008/shell/model/`:

- `User.java` - ✅ Usuários do sistema
- `Book.java` - 🔄 Livros da biblioteca
- `Loan.java` - 🔄 Empréstimos
- `Report.java` - 🔄 Relatórios

### Relacionamentos JPA

```java
@Entity
public class Loan {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;        // Referência forte via JPA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;        // Referência forte via JPA
}
```

## 🎨 Desenvolvimento de UI

### Padrão FXML + Controller

```java
public class MeuController {
    @FXML private ListView<MinhaEntidade> listView;
    @FXML private TextField nomeField;

    private MeuDAO dao = new MeuDAO();
    private IUIController uiController = ICore.getInstance().getUIController();

    @FXML
    public void initialize() {
        carregarDados();
        configurarCellFactory();
    }

    @FXML
    private void handleSalvar() {
        // Validação + persistência + feedback
    }
}
```

## 🧪 Testes

### Estrutura de Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn integration-test

# Teste específico de plugin
mvn test -Dtest=UserDAOTest
```

### Exemplo de Teste DAO

```java
class MeuDAOTest {
    private MeuDAO dao;
    private List<MinhaEntidade> criadosNoTeste;

    @BeforeEach
    void setUp() {
        dao = new MeuDAO();
        criadosNoTeste = new ArrayList<>();
    }

    @AfterEach
    void cleanup() {
        // Limpar dados de teste
        criadosNoTeste.forEach(dao::delete);
    }
}
```

## 📚 Documentação Completa

| Documento                                    | Descrição                                                    | Público Alvo                      |
| -------------------------------------------- | ------------------------------------------------------------ | --------------------------------- |
| **[📖 API Reference](API_REFERENCE.md)**     | Documentação detalhada de todas as interfaces e APIs         | Desenvolvedores                   |
| **[🏗️ Architecture Guide](ARCHITECTURE.md)** | Arquitetura detalhada, padrões de design e decisões técnicas | Arquitetos/Desenvolvedores Senior |
| **[👨‍💻 Development Guide](DEVELOPMENT.md)**   | Guia completo de desenvolvimento e boas práticas             | Desenvolvedores                   |
| **[🎨 UI Development Guide](UI_GUIDE.md)**   | Desenvolvimento de interfaces JavaFX e UX patterns           | Desenvolvedores Frontend          |
| **[⚙️ Setup Guide](SETUP.md)**               | Configuração do ambiente de desenvolvimento                  | Desenvolvedores/DevOps            |
| **[📋 Development Pipeline](PIPE.md)**       | Pipeline de desenvolvimento e cronograma                     | Gestores de Projeto               |
| **[📝 TODO & Roadmap](TODO.md)**             | Plano de desenvolvimento e tarefas pendentes                 | Toda a equipe                     |

## 🛠️ Status do Projeto

### Funcionalidades Implementadas

- ✅ **Core Microkernel**: Gerenciamento de plugins e persistência
- ✅ **User Management**: CRUD completo com interface JavaFX
- ✅ **Plugin System**: Carregamento dinâmico de plugins
- ✅ **Persistence Layer**: Hibernate com BaseDAO genérico
- ✅ **UI Framework**: Interface modular com cards e menus dinâmicos

### Próximos Passos

- 🔄 **Book Management**: CRUD de livros
- 🔄 **Loan Management**: Sistema de empréstimos
- 🔄 **Reporting**: Relatórios e analytics
- 🔄 **Authentication**: Sistema de autenticação completo

## 🤝 Contribuindo

### Adicionando um Novo Plugin

1. **Criar estrutura**: `mkdir -p plugins/meu-plugin/src/main/java/br/edu/ifba/inf008/plugins`
2. **Configurar POM**: Copiar de plugin existente e adaptar
3. **Implementar IPlugin**: Classe principal que implementa `IPlugin`
4. **Adicionar ao build**: Incluir módulo no `pom.xml` raiz
5. **Testar**: `mvn clean install && mvn exec:java -pl app`

### Convenções de Código

- **Packages**: `br.edu.ifba.inf008.plugins.{nome-plugin}`
- **Classes**: `{Nome}Plugin.java` para classe principal
- **DAOs**: Estender `BaseDAO<Entidade, ID>`
- **UI**: Usar FXML + Controller pattern
- **Testes**: Sufixo `Test.java` com limpeza automática

## 📄 Licença

Este projeto é parte do curso de Engenharia de Software - IFBA Campus Salvador.

---

> 💡 **Dica**: Para desenvolvimento mais eficiente, mantenha o container MariaDB sempre rodando e use `mvn exec:java -pl app` para testes rápidos.
