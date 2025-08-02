# 👥 User Plugin - BookStore

> User management plugin for the BookStore system

## 📋 Overview

The User Plugin is responsible for complete user management in the BookStore system. It provides a modern interface with TableView for viewing and manipulating user data, including advanced features like intelligent deletion and search.

## ✨ Features

### 🔧 CRUD Operations

- ✅ **Create users**: Form with required field validation
- ✅ **List users**: Modern TableView with date formatting
- ✅ **Update users**: Edit name and email
- ✅ **Delete users**: Intelligent system that manages related loans

### 🔍 Search System

- **Search by name**: Locate users by full or partial name
- **Search by email**: Filter by full or partial email address

### 🧠 Intelligent Features

- **Safe deletion**: When deleting a user with active loans:
  - Removes all loan records for the user
  - Automatically increments available copies of borrowed books
  - Maintains referential integrity in the database
- **Loan validation**: Checks if user has loans before deletion
- **Atomic transactions**: All operations performed in safe transactions

### 🎨 Modern Interface

- **Responsive TableView**: Displays ID, Name, Email and Registration Date
- **Date formatting**: Brazilian standard (dd/MM/yyyy HH:mm:ss)
- **Contextual buttons**: Enabled/disabled based on selection
- **Validated forms**: Required field verification
- **Smart confirmations**: Different alerts for users with loans
- **Auto-refresh**: Automatic updates when switching tabs

## 🏗️ Architecture

### Main Components

```text
user-plugin/
├── src/main/java/
│   └── br/edu/ifba/inf008/plugins/
│       ├── UserPlugin.java              # Main plugin class
│       └── user/
│           ├── persistence/
│           │   ├── JPAUtil.java         # Delegation to core JPAUtil
│           │   └── UserDAO.java         # Database operations
│           └── ui/
│               └── UserManagementController.java  # JavaFX Controller
├── src/main/resources/
│   └── br/edu/ifba/inf008/plugins/user/ui/
│       ├── user-management.fxml         # FXML Interface
│       ├── icons/                       # Images and icons
│       └── css/
│           └── user-styles.css          # Custom styles
└── src/test/
    ├── java/                            # Unit tests
    └── resources/
        └── META-INF/
            └── persistence.xml          # Test configuration (H2)
```

### Implemented Patterns

- **IPlugin**: Interface for microkernel integration
- **IRefreshable**: Enables automatic interface refresh
- **BaseDAO**: Inherits generic CRUD operations from core
- **FXML + Controller**: Separation between interface and logic
- **Test Environment Detection**: Automatic switching between H2 (tests) and MariaDB (production)

## 🔌 System Integration

### Navigation Menu

The plugin automatically adds:

- **Menu item**: `Management > Users`
- **Home card**: "User Management" with icon and description

### Dependencies

- **Core interfaces**: Communication with the microkernel
- **Shell models**: Access to User, Loan and Book models
- **JavaFX**: Modern graphical interface
- **Hibernate**: JPA persistence
- **JUnit**: Unit testing

### Data Model

Uses the centralized `User` model from core:

```java
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "registered_at")
  private LocalDateTime registeredAt;
}
```

## 🧪 Testing

The plugin includes comprehensive unit tests:

- **12 automated tests** covering all operations
- **H2 in-memory environment** for isolated tests
- **Automatic cleanup** between test executions
- **Complete coverage** of CRUD scenarios and relationships

### Running Tests

```bash
mvn test -pl plugins/user-plugin
```

## ⚠️ Special Behaviors

### Deleting Users with Loans

When a user has active loans:

1. **Special warning** is displayed informing about the loans
2. **Automatic cleanup**:
   - Removes all user loans
   - Increments available copies of borrowed books
   - Removes the user from the system
3. **Atomic transaction** ensures data consistency

This functionality ensures there will be no orphaned data or inconsistencies in the system even during complex deletion operations.
