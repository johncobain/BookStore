# Resumo do Projeto BookStore - Sistema de Livraria com Microkernel

## 🎯 Visão Geral

Sistema completo de gerenciamento de livraria desenvolvido com **arquitetura de microkernel** e **plugins modulares**. O projeto demonstra conceitos avançados de engenharia de software, incluindo persistência compartilhada, injeção de dependência e modularidade.

## 📅 Cronograma (1 mês - até 05/08/2025)

| Semana                   | Meta                         | Status        |
| ------------------------ | ---------------------------- | ------------- |
| **Semana 1** (até 13/07) | ✅ **Configuração e Base**   | **CONCLUÍDA** |
| **Semana 2** (até 20/07) | Plugin de Usuários (CRUD)    | **PRÓXIMA**   |
| **Semana 3** (até 27/07) | Plugins Livros + Empréstimos | Pendente      |
| **Semana 4** (até 05/08) | Relatórios + Entrega         | Pendente      |

## 🏗️ Arquitetura Implementada

### ✅ **Estrutura Concluída:**

```bash
microkernel/
├── app/                          # 🎯 CORE - Microkernel
│   ├── persistence.xml           # ✅ Configuração centralizada
│   ├── JPAUtil.java              # ✅ Gerenciador de persistência
│   ├── BaseDAO.java              # ✅ Classe base para DAOs
│   └── Core, UIController, etc.  # ✅ Controladores do núcleo
│
├── interfaces/                   # ✅ Contratos comuns
│   ├── IPlugin.java              # ✅ Interface para plugins
│   └── IDAO.java                 # ✅ Interface genérica DAO
│
└── plugins/                      # 🔌 PLUGINS MODULARES
    ├── user-plugin/              # ✅ Plugin de usuários
    ├── book-plugin/              # ✅ Plugin de livros
    ├── loan-plugin/              # ✅ Plugin de empréstimos
    └── report-plugin/            # ✅ Plugin de relatórios
```

### ✅ **Sistema de Persistência Modular:**

- **BaseDAO genérico:** `/app/src/main/java/.../persistence/BaseDAO.java`
- **Interface IDAO:** `/interfaces/src/main/java/.../interfaces/IDAO.java`
- **DAOs específicos:** Cada plugin estende BaseDAO para suas necessidades
- **Referências fracas:** Plugins usam IDs em vez de objetos JPA para relacionamentos
- **Verdadeira modularidade:** Qualquer plugin pode ser removido sem quebrar outros

### ✅ **Arquitetura de Referências Fracas:**

**Problema tradicional:**

```java
// ❌ Acoplamento forte
@ManyToOne
private User user;  // Se user-plugin for removido, quebra
```

**Solução implementada:**

```java
// ✅ Referência fraca
@Column(name = "user_id") 
private Long userId;  // Plugin independente
```

## 🚀 Status Atual - SEMANA 1 CONCLUÍDA

### ✅ **Configurações Funcionais:**

1. **Ambiente:**

   - ✅ Java 24 com SDKMAN
   - ✅ Docker + MariaDB rodando (porta 3307)
   - ✅ Maven build completo (`mvn clean install`)

2. **Persistência:**

   - ✅ Hibernate configurado e testado
   - ✅ Conexão com banco validada
   - ✅ Sistema de delegação implementado

3. **Plugins:**
   - ✅ 4 plugins criados (user, book, loan, report)
   - ✅ Estrutura base implementada
   - ✅ Sistema de carregamento dinâmico funcionando

## 📋 Próximos Passos - SEMANA 2

### 🎯 **Objetivo:** Plugin de Usuários (CRUD Completo)

#### 1. **Criar Modelo de Dados** (1-2 dias)

```java
// user-plugin/src/main/java/.../model/User.java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    // Construtores, getters, setters...
}
```

#### 2. **Implementar DAO** (1-2 dias)

```java
// user-plugin/src/main/java/.../persistence/UserDAO.java
public class UserDAO extends BaseDAO<User, Long> {
    
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
    
    // Métodos específicos de User
    public List<User> findByEmail(String email) {
        // Implementação específica usando EntityManager
    }
}
```

#### 3. **Criar Interface JavaFX** (2-3 dias)

- Formulário de cadastro/edição
- Tabela de listagem
- Botões de ação (Novo, Editar, Excluir)
- Integração com o microkernel (menu)

#### 4. **Testar e Validar** (1 dia)

- Teste manual completo
- Validações de dados
- Ajustes de usabilidade

## 🛠️ Como Desenvolver

### **Fluxo de Desenvolvimento:**

1. **Sempre começar com `mvn clean install`** para garantir que tudo compila
2. **Executar aplicação:** `mvn exec:java -pl app`
3. **Desenvolvimento iterativo:** Modelo → DAO → UI → Testes
4. **Usar `User` como template** para outros plugins

### **Comandos Essenciais:**

```bash
# Compilar tudo
mvn clean install

# Executar aplicação
mvn exec:java -pl app

# Testar Hibernate (user-plugin)
cd plugins/user-plugin && mvn test -Dtest=HibernateTest

# Verificar banco de dados
docker exec -it bookstore-mariadb mariadb -u root -p bookstore
```

## 🎯 Estratégia de Sucesso

### **Semana 2 - Foco Total no User Plugin:**

1. **Não tente fazer tudo de uma vez** - Complete o user-plugin primeiro
2. **Use o Hibernate** - Não faça SQL manual
3. **Interface simples primeiro** - Funcionalidade antes de beleza
4. **Teste cada funcionalidade** - CRUD completo antes de prosseguir

### **Semana 3 - Replicação:**

1. **User-plugin como template** - Copie e adapte para Book e Loan
2. **Referências fracas por ID** - Use `@Column` em vez de `@ManyToOne`
3. **Validações opcionais** - Implementar graceful degradation para plugins ausentes
4. **Transações independentes** - Cada plugin gerencia suas próprias transações

### **Semana 4 - Finalização:**

1. **Relatórios com JPQL** - Consultas entre entidades
2. **Polimento da UI** - Melhorias visuais
3. **Documentação** - README final
4. **Empacotamento** - Preparar para entrega

## 🎉 Pontos Fortes do Projeto Atual

### ✅ **Arquitetura Exemplar:**

- **Separação de responsabilidades** clara
- **Configuração centralizada** profissional  
- **Modularidade real** com plugins completamente independentes
- **Padrão BaseDAO** reutilizável e consistente
- **Referências fracas** garantem verdadeira modularidade

### ✅ **Base Técnica Sólida:**

- **Hibernate configurado** e funcionando
- **Build automatizado** com Maven
- **Estrutura escalável** para novos plugins
- **Testes funcionais** implementados

### ✅ **Diferencial Acadêmico:**

- Demonstra **compreensão arquitetural** avançada
- Aplica **padrões de design** profissionais
- Utiliza **tecnologias modernas** (Jakarta EE, Hibernate 6.5)
- Mostra **organização** e planejamento

## 🚨 Pontos de Atenção

1. **Não subestime a UI** - Reserve tempo adequado para JavaFX
2. **Teste frequentemente** - `mvn exec:java -pl app` após cada mudança
3. **Mantenha o foco** - Complete cada plugin antes do próximo
4. **Documente decisões** - Atualize os arquivos .md conforme evolui

## 🏆 Resultado Esperado

Um sistema completo de livraria que demonstra:

- ✅ **Arquitetura de microkernel** implementada
- ✅ **Plugins verdadeiramente modulares** - removíveis sem quebrar o sistema
- ✅ **Persistência baseada em padrões** com BaseDAO reutilizável
- ✅ **Interface gráfica** intuitiva
- ✅ **Operações CRUD** completas
- ✅ **Referências fracas** entre plugins para máxima independência
- ✅ **Relatórios** informativos

**Diferencial:** Sistema onde qualquer plugin pode ser removido e o restante continua funcionando perfeitamente! 🚀
