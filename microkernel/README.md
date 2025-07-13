# Build instructions

mvn install

## Execution instructions

mvn exec:java -pl app

## New plugin creation instructions

1. Create your plugin folder in "plugins"
2. Add you new plugin submodule in main pom.xml:

   ```xml
       <modules>
           <module>interfaces</module>
           <module>app</module>
           <module>plugins/myplugin</module>
           ADD IT HERE
       </modules>
   ```

3. Create your new plugin's pom.xml (check existing plugins as template)
4. Create your DAO extending BaseDAO<Entity, ID>:

   ```java
   public class MyDAO extends BaseDAO<MyEntity, Long> {
       @Override
       protected Class<MyEntity> getEntityClass() {
           return MyEntity.class;
       }
   }
   ```

5. Use **weak references** (IDs only) to other plugins' entities
6. Remember to use plugin's package conventions:

   `br/edu/ifba/inf008/plugins/<YourPluginNameInCamelCase>.java`

7. Run "mvn install" and "mvn exec:java -pl app"

## Modular Architecture

This project implements **true modularity** - any plugin can be removed without breaking the system:

- ✅ Plugins use BaseDAO for consistent CRUD operations
- ✅ Entities reference other plugins by ID only (weak references)  
- ✅ System gracefully degrades when plugins are missing
- ✅ Compilation succeeds even with removed plugins

**📖 For details:** See [MODULAR_ARCHITECTURE.md](MODULAR_ARCHITECTURE.md)
