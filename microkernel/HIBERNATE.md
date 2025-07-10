# Guia Detalhado: Como Configurar e Usar o Hibernate (JPA)

Aqui está um passo a passo para integrar o Hibernate em um dos seus plugins (ex: user-plugin). Você repetirá o processo para os outros plugins que acessam o banco.

## Passo 1: Adicionar as Dependências no Maven

No arquivo `pom.xml` do seu user-plugin (e dos outros plugins), adicione as seguintes dependências:

```xml
<dependencies>
    <dependency>
        <groupId>br.edu.ifba.inf008</groupId>
        <artifactId>interfaces</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.5.2.Final</version>
    </dependency>

    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
        <version>3.4.0</version>
    </dependency>

    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>22.0.1</version>
    </dependency>

    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>22.0.1</version>
    </dependency>
</dependencies>
```

## Passo 2: Criar o Arquivo de Configuração (persistence.xml)

Este é o arquivo que diz ao Hibernate como se conectar ao banco de dados e quais classes são entidades. Crie a seguinte estrutura de pastas e o arquivo dentro do seu plugin: `src/main/resources/META-INF/persistence.xml`.

**Arquivo:** `src/main/resources/META-INF/persistence.xml`

```xml

<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">

    <persistence-unit name="bookstore-pu" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <class>br.edu.ifba.inf008.plugins.user.model.User</class>
        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="org.mariadb.jdbc.Driver" />
            <property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://127.0.0.1:3307/bookstore" />
            <property name="jakarta.persistence.jdbc.user" value="root" />
            <property name="jakarta.persistence.jdbc.password" value="root" />

            <property name="hibernate.dialect" value="org.hibernate.dialect.MariaDBDialect" />

            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />

            <property name="hibernate.hbm2ddl.auto" value="update" />
        </properties>
    </persistence-unit>
</persistence>
```

## Passo 3: Mapear sua Classe de Modelo como uma Entidade

Agora, você precisa "ensinar" ao Hibernate que sua classe `User.java` corresponde a uma tabela no banco de dados. Você faz isso com anotações da JPA.

**Arquivo:** `src/main/java/br/edu/ifba/inf008/plugins/user/model/User.java`

```java

package br.edu.ifba.inf008.plugins.user.model;

import jakarta.persistence.\*;
import java.time.LocalDate;

@Entity // 1. Marca esta classe como uma entidade que pode ser persistida
@Table(name = "users") // 2. Mapeia esta classe para a tabela "users" no banco
public class User {

    @Id // 3. Marca este campo como a chave primária (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. Informa que o banco gera o valor (ex: AUTO_INCREMENT)
    private Long id;

    @Column(name = "name", nullable = false) // 5. Mapeia para a coluna "name", que não pode ser nula
    private String name;

    @Column(name = "email", nullable = false, unique = true) // Mapeia para a coluna "email", que deve ser única
    private String email;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    // Construtores, Getters e Setters... (essenciais para o Hibernate)
    public User() {}

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}
```

## Passo 4: Adaptar seu DAO para Usar o EntityManager

O `EntityManager` é o objeto do Hibernate/JPA que você usa para realizar as operações de persistência (salvar, buscar, excluir). Seu `UserDAO` não usará mais JDBC, mas sim o `EntityManager`.

Primeiro, é uma boa prática ter uma classe utilitária para gerenciar o `EntityManagerFactory`, que é um objeto caro de ser criado.

**Arquivo:** `src/main/java/br/edu/ifba/inf008/plugins/user/persistence/JPAUtil.java`

```java

package br.edu.ifba.inf008.plugins.user.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    // A EntityManagerFactory é criada apenas uma vez por aplicação
    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("bookstore-pu");

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }
}
```

Agora, seu DAO fica incrivelmente mais simples e legível.

**Arquivo:** `src/main/java/br/edu/ifba/inf008/plugins/user/persistence/UserDAO.java`

```java

package br.edu.ifba.inf008.plugins.user.persistence;

import br.edu.ifba.inf008.plugins.user.model.User;
import jakarta.persistence.EntityManager;
import java.util.List;

public class UserDAO {

    public void save(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Inicia a transação
            em.persist(user); // Persiste o objeto (cria um novo registro)
            em.getTransaction().commit(); // Confirma a transação
        } finally {
            em.close(); // Fecha o EntityManager
        }
    }

    public User update(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        User updatedUser = null;
        try {
            em.getTransaction().begin();
            updatedUser = em.merge(user); // Merge atualiza um objeto existente
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return updatedUser;
    }

    public void delete(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id); // Encontra o usuário pelo ID
            if (user != null) {
                em.remove(user); // Remove o objeto
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public User findById(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL (Java Persistence Query Language) - parece SQL, mas opera sobre objetos
            String jpql = "SELECT u FROM User u";
            return em.createQuery(jpql, User.class).getResultList();
        } finally {
            em.close();
        }
    }
}
```

Seguindo estes passos, você terá o Hibernate funcionando no seu projeto, o que vai simplificar muito o desenvolvimento das funcionalidades de persistência de dados.
