# 📚 BookStore Blackbird - Sistema de Livraria com Microkernel

Sistema completo de gerenciamento de livraria desenvolvido com **arquitetura de microkernel** e **plugins modulares**. Projeto acadêmico que demonstra conceitos avançados de engenharia de software.

## 🏗️ Arquitetura

O projeto utiliza **padrão microkernel** com plugins modulares:

- **Core (app/)**: Núcleo do sistema com persistência centralizada
- **Interfaces**: Contratos comuns para plugins
- **Plugins**: Módulos independentes para diferentes funcionalidades

```bash
microkernel/
├── app/                    # 🎯 Microkernel principal
├── interfaces/             # 📋 Contratos dos plugins
└── plugins/                # 🔌 Plugins modulares
    ├── user-plugin/        # 👥 Gerenciamento de usuários
    ├── book-plugin/        # 📖 Catálogo de livros
    ├── loan-plugin/        # 📝 Sistema de empréstimos
    └── report-plugin/      # 📊 Relatórios e analytics
```

## 🛠️ Tecnologias

- **Java 17+**
- **Maven** - Gerenciamento de dependências e build
- **Hibernate 6.5** - ORM e persistência
- **Jakarta EE** - JPA para persistência de dados
- **JavaFX** - Interface gráfica
- **MariaDB** - Banco de dados
- **Docker** - Containerização do banco

## 🚀 Como Executar

### 1. Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose

### 2. Configurar Banco de Dados

```bash
# Subir MariaDB com Docker
cd docker/
docker-compose up -d

# Verificar se está rodando
docker-compose logs mariadb
```

### 3. Compilar e Executar

```bash
# Compilar todo o projeto
cd microkernel/
mvn clean install

# Executar a aplicação
mvn exec:java -pl app
```

## 🔧 Configuração do Banco

O sistema está configurado para usar MariaDB via Docker:

- **Host**: localhost:3307
- **Banco**: bookstore
- **Usuário**: bookstore_user
- **Senha**: BookStore@777

Para alterar as configurações, edite:

- `docker/docker-compose.yml` - Configuração do MariaDB
- `microkernel/app/src/main/resources/META-INF/persistence.xml` - Conexão da aplicação

## 🔌 Desenvolvendo Plugins

### Criar Novo Plugin

1. Crie uma pasta em `plugins/meu-plugin/`
1. Adicione o módulo no `pom.xml` principal:

```xml
<modules>
    <module>interfaces</module>
    <module>app</module>
    <module>plugins/meu-plugin</module>
</modules>
```

1. Crie o `pom.xml` do plugin (use outro plugin como template)
1. Implemente a interface `IPlugin`
1. Use o namespace: `br.edu.ifba.inf008.plugins.meuplugin`

### Estrutura de Plugin

```java
// Implementar IPlugin
public class MeuPlugin implements IPlugin {
    @Override
    public String getName() { return "Meu Plugin"; }

    @Override
    public void execute() {
        // Lógica do plugin
    }
}

// Usar persistência compartilhada
EntityManager em = JPAUtil.getEntityManager();
```

## 📊 Features Implementadas

- ✅ **Microkernel funcional** com carregamento dinâmico de plugins
- ✅ **Persistência centralizada** com Hibernate
- ✅ **Sistema de plugins** modular e extensível
- ✅ **Configuração Docker** para desenvolvimento
- ✅ **Build automatizado** com Maven
- 🔄 **CRUD de usuários** (em desenvolvimento)
- 🔄 **Gestão de livros** (planejado)
- 🔄 **Sistema de empréstimos** (planejado)
- 🔄 **Relatórios** (planejado)

## 🧪 Testes

```bash
# Testar conexão Hibernate
cd plugins/user-plugin/
mvn test -Dtest=HibernateTest

# Executar todos os testes
mvn test
```

## 📖 Documentação

- [`ARCHITECTURE.md`](microkernel/ARCHITECTURE.md) - Arquitetura e padrões do sistema
- [`SETUP.md`](microkernel/SETUP.md) - Configuração do ambiente e instalação
- [`DEVELOPMENT.md`](microkernel/DEVELOPMENT.md) - Desenvolvimento de plugins
- [`UI_GUIDE.md`](microkernel/UI_GUIDE.md) - Interface gráfica e testes

## 🤝 Contribuindo

1. Faça fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Add nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto é desenvolvido para fins acadêmicos.

## 👥 Autores

Projeto desenvolvido para a disciplina INF008 - IFBA por [Andrey Gomes](https://github.com/johncobain)

---

⭐ **Gostou do projeto? Deixe uma estrela!**
