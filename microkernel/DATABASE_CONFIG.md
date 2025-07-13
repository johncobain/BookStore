# Guia de Configuração do Banco de Dados - BookStore

Este guia explica como alterar o usuário e senha do banco de dados MariaDB tanto no Docker quanto na aplicação BookStore.

## 🐳 Configuração do Docker (MariaDB)

### Localização do Arquivo de Configuração

O projeto BookStore utiliza Docker Compose para gerenciar o banco MariaDB. A configuração está em:

```bash
BookStore/docker/docker-compose.yml
```

### Configuração Atual

```yaml
services:
  bookstore-mariadb:
    image: mariadb:11.5
    container_name: bookstore-mariadb
    environment:
      MARIADB_ROOT_PASSWORD: root # ← Senha do usuário root
      MARIADB_DATABASE: bookstore # ← Nome do banco
      MARIADB_USER: bookstore_user # ← Usuário da aplicação
      MARIADB_PASSWORD: bookstore_pass # ← Senha da aplicação
    ports:
      - "3307:3306" # ← Porta externa:interna
    volumes:
      - bookstore_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - bookstore-network
```

### Como Alterar Usuário e Senha

#### Opção 1: Usuário Aplicação (Recomendado)

Para criar um usuário específico para a aplicação:

```yaml
environment:
  MARIADB_ROOT_PASSWORD: sua_senha_root_aqui
  MARIADB_DATABASE: bookstore
  MARIADB_USER: seu_usuario_aqui # ← Novo usuário
  MARIADB_PASSWORD: sua_senha_aqui # ← Nova senha
```

**Exemplo:**

```yaml
environment:
  MARIADB_ROOT_PASSWORD: admin123
  MARIADB_DATABASE: bookstore
  MARIADB_USER: bookstore_app
  MARIADB_PASSWORD: app123secure
```

#### Opção 2: Usar Apenas Root

Para usar apenas o usuário root:

```yaml
environment:
  MARIADB_ROOT_PASSWORD: sua_nova_senha # ← Nova senha do root
  MARIADB_DATABASE: bookstore
  # Remover ou comentar MARIADB_USER e MARIADB_PASSWORD
```

### Aplicar as Mudanças

Após alterar o `docker-compose.yml`:

```bash
# 1. Parar o container atual
cd BookStore/docker
docker-compose down

# 2. Remover dados antigos (ATENÇÃO: Apaga dados!)
docker volume rm docker_bookstore_data

# 3. Subir com nova configuração
docker-compose up -d

# 4. Verificar se está funcionando
docker-compose logs bookstore-mariadb
```

## ⚙️ Configuração da Aplicação (Hibernate)

### Localização do Arquivo de Configuração 2

A configuração de conexão da aplicação está em:

```bash
microkernel/app/src/main/resources/META-INF/persistence.xml
```

### Configuração Atual 2

```xml
<properties>
  <property name="jakarta.persistence.jdbc.driver" value="org.mariadb.jdbc.Driver" />
  <property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://127.0.0.1:3307/bookstore" />
  <property name="jakarta.persistence.jdbc.user" value="root" />
  <property name="jakarta.persistence.jdbc.password" value="root" />
  <!-- ... outras configurações ... -->
</properties>
```

### Como Alterar na Aplicação

#### Para Usuário Específico da Aplicação

```xml
<properties>
  <property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://127.0.0.1:3307/bookstore" />
  <property name="jakarta.persistence.jdbc.user" value="seu_usuario_aqui" />
  <property name="jakarta.persistence.jdbc.password" value="sua_senha_aqui" />
  <!-- ... outras configurações ... -->
</properties>
```

**Exemplo:**

```xml
<properties>
  <property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://127.0.0.1:3307/bookstore" />
  <property name="jakarta.persistence.jdbc.user" value="bookstore_app" />
  <property name="jakarta.persistence.jdbc.password" value="app123secure" />
  <!-- ... outras configurações ... -->
</properties>
```

#### Para Usar Root com Nova Senha

```xml
<properties>
  <property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://127.0.0.1:3307/bookstore" />
  <property name="jakarta.persistence.jdbc.user" value="root" />
  <property name="jakarta.persistence.jdbc.password" value="sua_nova_senha" />
  <!-- ... outras configurações ... -->
</properties>
```

## 🔄 Processo Completo de Mudança

### Cenário: Trocar de `root/root` para `bookstore_app/app123secure`

#### Passo 1: Alterar Docker

Edite `BookStore/docker/docker-compose.yml`:

```yaml
environment:
  MARIADB_ROOT_PASSWORD: admin123 # Nova senha do root
  MARIADB_DATABASE: bookstore
  MARIADB_USER: bookstore_app # Novo usuário
  MARIADB_PASSWORD: app123secure # Nova senha
```

#### Passo 2: Recriar Container

```bash
cd BookStore/docker
docker-compose down
docker volume rm docker_bookstore_data  # ⚠️ Remove dados antigos
docker-compose up -d
```

#### Passo 3: Alterar Aplicação

Edite `microkernel/app/src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.user" value="bookstore_app" />
<property name="jakarta.persistence.jdbc.password" value="app123secure" />
```

#### Passo 4: Testar

```bash
cd microkernel
mvn clean install
mvn exec:java -pl app
```

## 🧪 Testando a Conexão

### Teste via Cliente MariaDB

```bash
# Conectar via container
docker exec -it bookstore-mariadb mariadb -u bookstore_app -p bookstore

# Ou diretamente pela porta
mysql -h 127.0.0.1 -P 3307 -u bookstore_app -p bookstore
```

### Teste via Aplicação

```bash
cd microkernel/plugins/user-plugin
mvn test -Dtest=HibernateTest
```

Se aparecer:

```plaintext
🎉 HIBERNATE FUNCIONANDO PERFEITAMENTE!
```

A configuração está correta!

## 🔒 Boas Práticas de Segurança

### ✅ **Recomendações:**

1. **Usuário específico:** Crie um usuário dedicado para a aplicação, não use root
2. **Senhas fortes:** Use senhas complexas, especialmente em produção
3. **Permissões mínimas:** O usuário da aplicação só precisa de acesso ao banco `bookstore`

### ⚠️ **Evite:**

1. **Senhas vazias** ou muito simples
2. **Root em produção** - Use apenas para desenvolvimento local
3. **Senhas no código** - Em produção, use variáveis de ambiente

### 🔐 **Configuração de Produção (Exemplo):**

```yaml
# docker-compose.yml (produção)
environment:
  MARIADB_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
  MARIADB_DATABASE: bookstore
  MARIADB_USER: ${MYSQL_USER}
  MARIADB_PASSWORD: ${MYSQL_PASSWORD}
```

```xml
<!-- persistence.xml (produção) -->
<property name="jakarta.persistence.jdbc.user" value="${db.user}" />
<property name="jakarta.persistence.jdbc.password" value="${db.password}" />
```

## 🚨 Solução de Problemas

### Erro: "Access denied for user"

1. Verifique se o usuário/senha no `persistence.xml` estão corretos
2. Confirme se o container foi recriado após alterar `docker-compose.yml`
3. Teste a conexão manualmente com cliente MariaDB

### Erro: "Connection refused"

1. Verifique se o container está rodando: `docker ps`
2. Confirme a porta no `persistence.xml` (3307, não 3306)
3. Teste a conectividade: `telnet 127.0.0.1 3307`

### Erro: "Unknown database"

1. Verifique se `MARIADB_DATABASE` está configurado no Docker
2. Confirme o nome do banco no `persistence.xml`
3. Recrie o container se necessário

## 📝 Resumo dos Arquivos

| Arquivo              | Localização                                    | Responsabilidade        |
| -------------------- | ---------------------------------------------- | ----------------------- |
| `docker-compose.yml` | `BookStore/docker/`                            | Configuração do MariaDB |
| `persistence.xml`    | `microkernel/app/src/main/resources/META-INF/` | Conexão da aplicação    |

**Lembre-se:** Sempre que alterar as configurações do Docker, recrie o container. Sempre que alterar o `persistence.xml`, recompile a aplicação!
