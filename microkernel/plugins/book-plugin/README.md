# 📚 Book Management Plugin

> Book catalog management for the BookStore system

## 📋 Overview

The Book Management Plugin handles all book catalog operations in the BookStore system. It provides a modern interface for managing books with advanced search capabilities and inventory control.

## ✨ Features

- **📚 Complete Book Management**: Create, read, update, and delete books
- **🔍 Advanced Search**: Search by title, author, or ISBN
- **📊 Inventory Control**: Track available copies and total stock
- **✅ Available Filter**: Show only books with available copies
- **🔢 Numeric Validation**: Smart validation for year and copies fields
- **📱 Modern TableView**: Responsive table with organized columns
- **🔄 Auto-refresh**: Automatic updates when switching tabs

## 🏗️ Structure

```text
book-plugin/
├── src/main/java/
│   └── br/edu/ifba/inf008/plugins/
│       ├── BookPlugin.java              # Main plugin class
│       └── book/
│           ├── persistence/
│           │   ├── JPAUtil.java         # Delegation to core JPAUtil
│           │   └── BookDAO.java         # Database operations
│           └── ui/
│               └── BookManagementController.java  # JavaFX Controller
├── src/main/resources/
│   └── br/edu/ifba/inf008/plugins/book/ui/
│       ├── book-management.fxml         # FXML Interface
│       ├── icons/                       # Images and icons
│       └── css/
│           └── book-styles.css          # Custom styles
└── src/test/
    ├── java/                            # Unit tests
    └── resources/
        └── META-INF/
            └── persistence.xml          # Test configuration (H2)
```

## 🔧 Key Components

### BookDAO

- Advanced search with multiple fields
- Available books filtering
- Test environment detection
- Transaction safety

### BookManagementController

- TableView with formatted columns
- Real-time numeric validation
- Search with availability filter
- Form validation and confirmation dialogs

## 🚀 Usage

1. **Add Book**: Fill form with title, author, ISBN, year, and copies
2. **Search**: Use search field with title, author, or ISBN
3. **Filter**: Toggle "Available Only" to show books with copies
4. **Update**: Select book from table and modify details
5. **Delete**: Remove books with confirmation dialog

## 🔗 Dependencies

- BookStore Core System
- JavaFX 17+
- Hibernate 6.5.2+
- H2 (testing) / MariaDB (production)
