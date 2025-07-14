# Guia de Configuração e Instalação - BookStore

## Pré-requisitos

### Ferramentas Necessárias

- **SDKMAN**: Gerenciador de SDKs Java
- **Java 24**: JDK mais recente
- **Maven**: Gerenciamento de dependências (incluído no SDKMAN)
- **Docker**: Containerização do banco de dados
- **Git**: Controle de versão

### Verificação do Ambiente

```bash
# Verificar versões instaladas
java --version
mvn --version
docker --version
git --version
```

## Configuração do Ambiente de Desenvolvimento

### 1. Instalação do SDKMAN

```bash
# Instalar SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Verificar instalação
sdk version
```

### 2. Instalação do Java 24

```bash
# Listar versões disponíveis do Java
sdk list java

# Instalar Java 24
sdk install java 24-open

# Definir como padrão
sdk default java 24-open

# Verificar
java --version
```

### 3. Configuração do Maven

```bash
# Instalar Maven (se não estiver instalado)
sdk install maven

# Verificar
mvn --version
```

### 4. Instalação do Docker

#### Ubuntu/Debian

```bash
# Atualizar repositórios
sudo apt update

# Instalar Docker
sudo apt install docker.io docker-compose

# Adicionar usuário ao grupo docker
sudo usermod -aG docker $USER

# Reiniciar sessão ou executar
newgrp docker

# Verificar
docker --version
docker-compose --version
```

#### Outras distribuições

Consulte a documentação oficial do Docker para sua distribuição específica.

## Configuração do Banco de Dados

### 1. Inicialização do MariaDB

```bash
# Navegar para a pasta do Docker
cd BookStore/docker

# Subir o container MariaDB
docker-compose up -d

# Verificar se está rodando
docker ps

# Ver logs (opcional)
docker-compose logs bookstore-mariadb
```

### 2. Configuração Padrão

O arquivo `docker-compose.yml` já vem configurado com:

```yaml
services:
  bookstore-mariadb:
    image: mariadb:11.5
    container_name: bookstore-mariadb
    environment:
      MARIADB_ROOT_PASSWORD: root
      MARIADB_DATABASE: bookstore
      MARIADB_USER: bookstore_user
      MARIADB_PASSWORD: bookstore_pass
    ports:
      - "3307:3306" # Porta externa: 3307
```

### 3. Testando a Conexão

```bash
# Conectar via cliente MariaDB (se instalado)
mysql -h 127.0.0.1 -P 3307 -u root -p bookstore

# Ou via container
docker exec -it bookstore-mariadb mariadb -u root -p bookstore
```

### 4. Personalização de Credenciais

#### Alterando Usuário e Senha

Para alterar as credenciais do banco:

1. **Editar `docker-compose.yml`:**

   ```yaml
   environment:
     MARIADB_ROOT_PASSWORD: sua_nova_senha
     MARIADB_DATABASE: bookstore
     MARIADB_USER: seu_usuario
     MARIADB_PASSWORD: sua_senha
   ```

2. **Recriar o container:**

   ```bash
   cd BookStore/docker
   docker-compose down
   docker volume rm docker_bookstore_data  # ⚠️ Remove dados antigos
   docker-compose up -d
   ```

3. **Atualizar `persistence.xml`:**

```xml
<!-- microkernel/app/src/main/resources/META-INF/persistence.xml -->
<property name="jakarta.persistence.jdbc.user" value="seu_usuario" />
<property name="jakarta.persistence.jdbc.password" value="sua_senha" />
```

## Configuração do Projeto

### 1. Clone e Configuração Inicial

```bash
# Clonar o repositório (se necessário)
git clone <repository-url> BookStore
cd BookStore/microkernel

# Definir Java 24 para o projeto
sdk use java 24-open
```

### 2. Build do Projeto

```bash
# Limpar e compilar tudo
mvn clean install

# Verificar se compilou sem erros
echo "Build Status: $?"
```

### 3. Verificação da Configuração JPA

O arquivo `persistence.xml` está localizado em:

```bash
microkernel/app/src/main/resources/META-INF/persistence.xml
```

Configuração padrão:

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">
    <persistence-unit name="bookstore-pu" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <!-- Entidades configuradas -->
        <class>br.edu.ifba.inf008.shell.model.User</class>
        <class>br.edu.ifba.inf008.shell.model.Book</class>
        <class>br.edu.ifba.inf008.shell.model.Loan</class>
        <class>br.edu.ifba.inf008.shell.model.Report</class>

        <properties>
            <!-- Conexão com banco -->
            <property name="jakarta.persistence.jdbc.driver"
                      value="org.mariadb.jdbc.Driver" />
            <property name="jakarta.persistence.jdbc.url"
                      value="jdbc:mariadb://127.0.0.1:3307/bookstore" />
            <property name="jakarta.persistence.jdbc.user" value="root" />
            <property name="jakarta.persistence.jdbc.password" value="root" />

            <!-- Configurações do Hibernate -->
            <property name="hibernate.dialect"
                      value="org.hibernate.dialect.MariaDBDialect" />
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />
            <property name="hibernate.hbm2ddl.auto" value="update" />
        </properties>
    </persistence-unit>
</persistence>
```

## Execução do Sistema

### 1. Executar a Aplicação

```bash
# Na pasta microkernel/
mvn exec:java -pl app
```

### 2. Execução em Background

```bash
# Para rodar em background
nohup mvn exec:java -pl app > app.log 2>&1 &

# Ver logs
tail -f app.log

# Parar aplicação
pkill -f "mvn exec:java"
```

### 3. Verificação de Funcionamento

Se tudo estiver configurado corretamente, você deve ver:

1. **Console**: Mensagens de carregamento dos plugins
2. **Interface**: Janela JavaFX com menus dos plugins
3. **Logs**: Queries SQL sendo executadas (se `hibernate.show_sql=true`)

## Configuração de IDE

### IntelliJ IDEA

1. **Importar projeto Maven:**

   - File → Open → Selecionar pasta `microkernel/`
   - Aguardar importação das dependências

2. **Configurar SDK:**

   - File → Project Structure → Project
   - Project SDK: Java 24
   - Project Language Level: 24

3. **Configurar JavaFX:**
   - Run → Edit Configurations
   - VM Options: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`

### VS Code

1. **Extensões necessárias:**

   - Extension Pack for Java
   - Maven for Java

2. **Configuração do workspace:**

   ```json
   {
     "java.configuration.updateBuildConfiguration": "automatic",
     "java.compile.nullAnalysis.mode": "automatic",
     "maven.view": "hierarchical"
   }
   ```

### Eclipse

1. **Importar projeto Maven:**

   - File → Import → Existing Maven Projects
   - Selecionar pasta `microkernel/`

2. **Configurar JavaFX:**
   - Run Configurations → VM Arguments
   - Adicionar: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`

## Configuração para Produção

### Variáveis de Ambiente

Para ambientes de produção, use variáveis de ambiente:

```bash
# Configurar variáveis
export DB_HOST=production-db-host
export DB_PORT=3306
export DB_NAME=bookstore_prod
export DB_USER=bookstore_app
export DB_PASSWORD=secure_password_here
```

### Docker Compose para Produção

```yaml
# docker-compose.prod.yml
version: "3.8"
services:
  bookstore-mariadb:
    image: mariadb:11.5
    environment:
      MARIADB_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MARIADB_DATABASE: ${MYSQL_DATABASE}
      MARIADB_USER: ${MYSQL_USER}
      MARIADB_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - bookstore_prod_data:/var/lib/mysql
    ports:
      - "${DB_PORT}:3306"
```

### Persistence.xml para Produção

```xml
<!-- Use placeholders para variáveis -->
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:mariadb://${db.host}:${db.port}/${db.name}" />
<property name="jakarta.persistence.jdbc.user" value="${db.user}" />
<property name="jakarta.persistence.jdbc.password" value="${db.password}" />

<!-- Configurações de produção -->
<property name="hibernate.show_sql" value="false" />
<property name="hibernate.hbm2ddl.auto" value="validate" />
```

## Solução de Problemas Comuns

### Erro: "Access denied for user"

**Causa**: Credenciais incorretas ou usuário não existe

**Solução**:

1. Verificar credenciais no `persistence.xml`
2. Verificar configuração do Docker
3. Recriar container se necessário

### Erro: "Connection refused"

**Causa**: Banco não está rodando ou porta incorreta

**Solução**:

```bash
# Verificar se container está rodando
docker ps

# Verificar porta
netstat -tlnp | grep 3307

# Reiniciar container
docker-compose restart bookstore-mariadb
```

### Erro: "ClassNotFoundException"

**Causa**: Dependências não encontradas ou build incompleto

**Solução**:

```bash
# Recompilar projeto
mvn clean install

# Verificar dependências
mvn dependency:tree
```

### Erro: "Unknown database 'bookstore'"

**Causa**: Banco não foi criado ou configuração incorreta

**Solução**:

1. Verificar variável `MARIADB_DATABASE` no Docker
2. Recriar container com configuração correta
3. Criar banco manualmente se necessário

### Erro de JavaFX Runtime

**Causa**: JavaFX não está no module path

**Solução**:

```bash
# Adicionar VM options
--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
```
