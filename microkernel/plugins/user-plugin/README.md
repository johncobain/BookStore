# 👥 User Plugin - BookStore

> User management plugin for the BookStore system

## 📋 Overview

The User Plugin is responsible for complete user management in the BookStore system. It provides a modern interface with TableView for viewing and manipulating user data, including advanced features like intelligent deletion and search.

## ✨ Features

- **👥 Complete User Management**: Create, read, update, and delete users
- **🔍 Advanced Search**: Search by user name or email
- **⚠️ Deleting Users with Loans**: Warns before deleting users with active loans
- **📱 Modern TableView**: Responsive table with organized columns
- **🔄 Auto-refresh**: Automatic updates when switching tabs

## 🏗️ Structure

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

## 🔧 Key Components

### UserDAO

- Advanced search with multiple fields
- Has loans filtering
- Advanced deletion
- Test environment detection
- Transaction safety

### UserManagementController

- TableView with formatted columns
- Form validation and confirmation dialogs
- Delete with loan validation

## 🚀 Usage

1. **Add User**: Fill form with name and email
2. **Search**: Use search field with name or email
3. **Update**: Select user from table and modify details
4. **Delete**: Remove users with confirmation dialog

## ⚠️ Special Behaviors

### Deleting Users with Loans

When a user has active loans:

1. **Special warning** is displayed informing about the loans
2. **Automatic cleanup**:
   - Removes all user loans
   - Increments available copies of borrowed books
   - Removes the user from the system
3. **Atomic transaction** ensures data consistency

## 🔗 Dependencies

- BookStore Core System
- JavaFX 17+
- Hibernate 6.5.2+
- H2 (testing) / MariaDB (production)
