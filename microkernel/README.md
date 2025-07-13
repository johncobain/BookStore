# BookStore Microkernel

## Build instructions

mvn install

## Execution instructions

mvn exec:java -pl app

## New plugin creation instructions

1. Create your plugin folder in "plugins"
2. Add your new plugin submodule in main pom.xml:

   ```xml
       <modules>
           <module>interfaces</module>
           <module>app</module>
           <module>plugins/myplugin</module>
           <!-- ADD YOUR PLUGIN HERE -->
       </modules>
   ```

3. Create your new plugin's pom.xml (check existing plugins as template)
4. Create your DAO extending BaseDAO<Entity, ID>:

   ```java
   public class MyDAO extends BaseDAO<MyEntity, Integer> {
       @Override
       protected Class<MyEntity> getEntityClass() {
           return MyEntity.class;
       }
   }
   ```

5. **Use core models:** Import from `br.edu.ifba.inf008.shell.model.*` (don't duplicate models)
6. **Use weak references:** IDs only to other plugins' entities
7. Remember plugin package conventions: `br/edu/ifba/inf008/plugins/<YourPluginNameInCamelCase>.java`
8. Run "mvn install" and "mvn exec:java -pl app"

## Modular Architecture

This project implements **true modularity** goal - plugins should be removable without breaking the system:

- ✅ Plugins use BaseDAO for consistent CRUD operations
- ⚠️ **Currently only User model** centralized (Book, Loan, Report pending)
- ✅ System designed for graceful degradation when plugins are missing
- ⚠️ **Full independence** pending completion of core models

## Documentation

� **Complete Documentation:**

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System architecture and design patterns
- **[SETUP.md](SETUP.md)** - Environment setup and configuration
- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Plugin development and best practices  
- **[UI_GUIDE.md](UI_GUIDE.md)** - User interface development and testing

**💡 Key Insight:** Models must be in core (not plugins) for true modularity. Plugins should only contain DAOs, Controllers, and Views.
