# Arquitetura do Sistema BookStore

## Visão Geral da Arquitetura

O BookStore implementa uma **arquitetura de microkernel** que separa as funcionalidades essenciais (core) das funcionalidades específicas (plugins), promovendo modularidade, extensibilidade e manutenibilidade.

### Princípios Arquiteturais

- **Separação de responsabilidades**: Core gerencia infraestrutura, plugins implementam regras de negócio
- **Baixo acoplamento**: Plugins se comunicam através de interfaces bem definidas
- **Alta coesão**: Cada plugin tem responsabilidade única e bem definida
- **Extensibilidade**: Novos plugins podem ser adicionados sem modificar o core
- **Modularidade**: Componentes podem ser desenvolvidos e testados independentemente

## Estrutura do Projeto

```bash
microkernel/
├── interfaces/          # Contratos e interfaces compartilhadas
├── app/                # Core do microkernel
│   ├── src/main/java/br/edu/ifba/inf008/
│   │   ├── App.java                    # Ponto de entrada
│   │   └── shell/                      # Núcleo do sistema
│   │       ├── Core.java              # Coordenador central
│   │       ├── PluginController.java  # Carregamento de plugins
│   │       ├── UIController.java      # Interface gráfica
│   │       ├── model/                 # Modelos centralizados
│   │       │   ├── User.java         # ✅ Implementado
│   │       │   ├── Book.java         # 🔄 Planejado
│   │       │   ├── Loan.java         # 🔄 Planejado
│   │       │   └── Report.java       # 🔄 Planejado
│   │       └── persistence/           # Camada de dados
│   │           ├── JPAUtil.java      # Gerenciador JPA
│   │           ├── BaseDAO.java      # DAO genérico
│   │           └── IDAO.java         # Interface DAO
│   └── src/main/resources/META-INF/
│       └── persistence.xml           # Configuração JPA centralizada
└── plugins/            # Plugins modulares
    ├── user-plugin/   # Gerenciamento de usuários
    ├── book-plugin/   # Gerenciamento de livros
    ├── loan-plugin/   # Gerenciamento de empréstimos
    └── report-plugin/ # Relatórios
```

## Padrões Arquiteturais Implementados

### 1. Microkernel Pattern

#### **Core mínimo + Plugins especializados**

- **Core responsabilidades**: Carregamento de plugins, persistência, interface gráfica, modelos compartilhados
- **Plugin responsabilidades**: Lógica de negócio específica, interfaces especializadas, validações

### 2. Plugin Architecture

#### **Carregamento dinâmico e interface padronizada**

```java
// Interface padrão para todos os plugins
public interface IPlugin {
    boolean init(); // Inicialização do plugin
}

// Carregamento automático via reflexão
// Busca por classes no padrão: br.edu.ifba.inf008.plugins.{NomePlugin}
```

### 3. Repository Pattern (DAO)

#### **Abstração da camada de dados**

```java
// Interface genérica para operações CRUD
public interface IDAO<T, ID> {
    void save(T entity);
    T update(T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
}

// Implementação base usando JPA
public abstract class BaseDAO<T, ID> implements IDAO<T, ID> {
    // Implementação comum usando EntityManager
}
```

### 4. Persistence Centralization

#### **Configuração unificada e delegação**

- **persistence.xml centralizado** no core
- **JPAUtil centralizado** gerencia EntityManagerFactory
- **Delegação nos plugins** via métodos que retornam Object (evita dependências diretas)

## Modelos de Dados Centralizados

### Estratégia Atual: Modelos no Core

Todos os modelos estão centralizados no core (`/app/src/main/java/.../shell/model/`) para:

✅ **Simplicidade de configuração**  
✅ **Relacionamentos JPA diretos** entre entidades  
✅ **Transações unificadas**  
✅ **Facilidade de desenvolvimento**

### Padrão de IDs e Convenções

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId; // Tipo atual (sendo padronizado para Long)

    // Atributos com convenções
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Relacionamentos usando anotações JPA
}
```

### Relacionamentos Entre Entidades

**Usando referências diretas JPA (strong references):**

```java
// Em Loan.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "book_id", nullable = false)
private Book book;
```

**Vantagens:**

- Navegação direta entre objetos: `loan.getUser().getName()`
- Validação automática de integridade referencial
- Lazy loading para performance
- Transações automáticas

## Persistência Compartilhada

### Configuração Centralizada

```xml
<!-- persistence.xml no core -->
<persistence-unit name="bookstore-pu" transaction-type="RESOURCE_LOCAL">
    <class>br.edu.ifba.inf008.shell.model.User</class>
    <class>br.edu.ifba.inf008.shell.model.Book</class>
    <class>br.edu.ifba.inf008.shell.model.Loan</class>
    <class>br.edu.ifba.inf008.shell.model.Report</class>

    <properties>
        <property name="jakarta.persistence.jdbc.url"
                  value="jdbc:mariadb://127.0.0.1:3307/bookstore" />
        <!-- Configurações do Hibernate -->
    </properties>
</persistence-unit>
```

### Padrão de Delegação

**JPAUtil centralizado:**

```java
// No core (/app/)
public class JPAUtil {
    private static final EntityManagerFactory FACTORY =
        Persistence.createEntityManagerFactory("bookstore-pu");

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }

    // Para plugins (evita dependência direta)
    public static Object getEntityManagerAsObject() {
        return FACTORY.createEntityManager();
    }
}
```

**JPAUtil nos plugins (delegação):**

```java
// Em cada plugin
public class JPAUtil {
    public static Object getEntityManager() {
        // Delega para o JPAUtil centralizado
        return br.edu.ifba.inf008.shell.persistence.JPAUtil.getEntityManagerAsObject();
    }
}
```

**Uso nos plugins:**

```java
// Plugin faz cast simples
Object emObject = JPAUtil.getEntityManager();
EntityManager em = (EntityManager) emObject;

// Uso normal do JPA
em.getTransaction().begin();
// ... operações ...
em.getTransaction().commit();
em.close();
```

## Interface de Usuário (JavaFX)

### Arquitetura da UI

- **UIController**: Gerencia janela principal, menus e abas
- **Plugin interfaces**: Cada plugin cria suas próprias telas
- **Padrão Observer**: Plugins se registram para eventos da UI

### Padrões de Interface

```java
// Plugin cria menu item
MenuItem menuItem = uiController.createMenuItem("Users", "Manage Users");

// Plugin define ação
menuItem.setOnAction(event -> {
    // Criar interface do plugin
    VBox content = createUserManagementInterface();
    uiController.createTab("User Management", content);
});
```

## Tecnologias e Frameworks

### Core Technologies

- **Java 24**: Linguagem principal
- **JavaFX**: Interface gráfica
- **Maven**: Gerenciamento de dependências e build
- **JPA/Hibernate**: ORM para persistência
- **MariaDB**: Banco de dados
- **Docker**: Containerização do banco

### Dependências Principais

```xml
<!-- Hibernate ORM -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.5.2.Final</version>
</dependency>

<!-- MariaDB Driver -->
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>3.4.0</version>
</dependency>

<!-- JavaFX Controls -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.2</version>
</dependency>
```

## Vantagens da Arquitetura

### ✅ Modularidade

- Plugins independentes com responsabilidades específicas
- Core focado apenas em infraestrutura
- Desenvolvimento paralelo de funcionalidades

### ✅ Extensibilidade

- Novos plugins podem ser adicionados facilmente
- Interface padronizada via IPlugin
- Carregamento dinâmico de funcionalidades

### ✅ Manutenibilidade

- Separação clara de responsabilidades
- Código do core estável e reutilizável
- Alterações em plugins não afetam outros componentes

### ✅ Testabilidade

- Plugins podem ser testados isoladamente
- Mock da interface ICore para testes unitários
- Testes de integração no nível da aplicação

## Limitações e Trade-offs

### ⚠️ Complexidade Inicial

- Configuração mais complexa que monolito
- Necessário entendimento de padrões arquiteturais
- Curva de aprendizado para novos desenvolvedores

### ⚠️ Acoplamento via Modelos

- Modelos centralizados criam dependência entre plugins
- Mudanças em modelos afetam múltiplos plugins
- Menos independência que verdadeira SOA

### ⚠️ Performance

- Overhead de carregamento de plugins
- Reflexão para descoberta de classes
- Múltiplas camadas de abstração

## Padrões de Desenvolvimento

### Convenções de Código

- **Package structure**: `br.edu.ifba.inf008.plugins.{plugin-name}`
- **Class naming**: `{Feature}Plugin` para classes principais
- **DAO naming**: `{Entity}DAO` para classes de acesso a dados
- **Controller naming**: `{Feature}Controller` para lógica de negócio

### Boas Práticas

1. **Inicialização de plugins**: Sempre retornar boolean em `init()`
2. **Gestão de recursos**: Fechar EntityManager em finally blocks
3. **Transações**: Usar try-catch para rollback em caso de erro
4. **Interface gráfica**: Usar FXML quando possível para separar UI da lógica
5. **Testes**: Criar testes para DAOs e lógica de negócio

Esta arquitetura fornece uma base sólida e escalável para o sistema BookStore, permitindo crescimento orgânico através de plugins especializados mantendo a simplicidade do core.
