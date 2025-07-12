# Criando um Plugin de Teste

Este guia mostra como criar um plugin de teste para validar a arquitetura de microkernel do projeto BookStore. Se funcionar, significa que a base está correta e você pode construir os outros plugins com confiança.

Vamos criar um plugin simples chamado `test-plugin` que:

- Imprima uma mensagem no console quando for carregado
- Adicione um item de menu na interface principal
- Crie uma nova aba quando o menu for clicado

## Passo 1: Criar a Estrutura do Plugin

Dentro da pasta `microkernel/plugins/`, crie a estrutura:

```bash
plugins/
└── test-plugin/
    ├── pom.xml
    └── src/
        └── main/
            └── java/
                └── br/edu/ifba/inf008/plugins/
                    └── TestPlugin.java
```

## Passo 2: Usar a Interface IPlugin Existente

O projeto já tem a interface `IPlugin` definida em `interfaces/src/main/java/br/edu/ifba/inf008/interfaces/IPlugin.java`:

```java
package br.edu.ifba.inf008.interfaces;

public interface IPlugin {
    public abstract boolean init();
}
```

Esta interface é simples - apenas um método `init()` que retorna `boolean` para indicar sucesso ou falha na inicialização.

## Passo 3: Configurar o pom.xml do Plugin

Crie o arquivo `pom.xml` seguindo o padrão dos outros plugins do projeto:

**Arquivo:** `plugins/test-plugin/pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>br.edu.ifba.inf008</groupId>
        <artifactId>parent-project</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../..</relativePath>
    </parent>

    <artifactId>test-plugin</artifactId>

    <dependencies>
        <dependency>
            <groupId>br.edu.ifba.inf008</groupId>
            <artifactId>interfaces</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Configure maven-jar-plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <!-- Change the output directory -->
                    <outputDirectory>${project.basedir}/../</outputDirectory>
                    <!-- Change the JAR file name -->
                    <finalName>TestPlugin</finalName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Passo 4: Atualizar o pom.xml Principal

Adicione o novo plugin ao `microkernel/pom.xml`:

```xml
<modules>
    <module>interfaces</module>
    <module>app</module>
    <module>plugins/myplugin</module>
    <!-- ADICIONE A LINHA ABAIXO -->
    <module>plugins/test-plugin</module>
</modules>
```

## Passo 5: Criar a Classe do Plugin

Seguindo o padrão do `MyPlugin` existente, crie o plugin de teste:

**Arquivo:** `plugins/test-plugin/src/main/java/br/edu/ifba/inf008/plugins/TestPlugin.java`

```java
package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;

import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

/**
 * Plugin de teste que demonstra a integração com o microkernel.
 */
public class TestPlugin implements IPlugin {

    @Override
    public boolean init() {
        System.out.println(">>>>>>>>> Test Plugin has been loaded <<<<<<<<<");

        // Obter o controlador de UI do núcleo
        IUIController uiController = ICore.getInstance().getUIController();

        // Criar item de menu
        MenuItem menuItem = uiControler.createMenuItem("Test", "Execute Test Plugin");

        // Definir ação do menu
        menuItem.setOnAction(event -> {
            // Criar conteúdo da aba
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.getChildren().addAll(
                new Label("Test Plugin Working!"),
                new Label("This plugin was loaded by the microkernel!"),
                new Label("Data/Hora: " + java.time.LocalDateTime.now())
            );

            // Criar nova aba
            uiControler.createTab("Test", content);

            System.out.println("Test Plugin executed - new tab created!");
        });

        return true; // Inicialização bem-sucedida
    }
}
```

## Passo 6: Como o Sistema Funciona

O projeto usa um sistema de carregamento de plugins baseado em:

1. **PluginController**: Carrega arquivos `.jar` da pasta `plugins/`
2. **Convenção de nomes**: Busca classes com padrão `br.edu.ifba.inf008.plugins.{NomeDoPlugin}`
3. **ICore e IUIController**: Fornecem acesso aos recursos do microkernel

O carregamento acontece automaticamente quando a aplicação inicia através do `Core.init()` no `App.java`.

## Passo 7: Construir e Executar

No terminal, na raiz do projeto (`microkernel/`):

```bash
mvn clean install
```

Depois execute a aplicação:

```bash
mvn exec:java -pl app
```

## O que Esperar

Se tudo funcionar corretamente:

1. **No console**: Mensagem `>>>>>>>> Plugin de Teste foi carregado com sucesso! <<<<<<<<`
2. **Na interface**: Um menu "Teste" com item "Executar Plugin de Teste"
3. **Ao clicar**: Uma nova aba "Teste Plugin" será criada com informações do plugin
4. **No console**: Mensagem `Plugin de teste executado - nova aba criada!`

## Próximos Passos

Com o plugin de teste funcionando, você pode criar os plugins reais do sistema:

- `user-plugin`: Gerenciamento de usuários
- `book-plugin`: Gerenciamento de livros
- `loan-plugin`: Gerenciamento de empréstimos
- `report-plugin`: Relatórios do sistema

Cada um seguirá o mesmo padrão: implementar `IPlugin`, usar `IUIController` para criar menus e abas, e integrar com o banco de dados usando Hibernate.
