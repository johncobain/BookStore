# 📋 Loan Management Plugin

> Book lending management for the BookStore system

## � Overview

The Loan Management Plugin handles all book lending operations in the BookStore system. It provides a modern interface for creating, updating, returning, and tracking book loans with automatic inventory management.

## ✨ Features

- **📚 Complete Loan Management**: Create, update, return, and delete loans
- **🔍 Smart Search**: Search by loan ID, borrower name, or email
- **🎯 Active Loan Filter**: Toggle between all loans and active loans only
- **📊 Inventory Control**: Automatic book copy management
- **📅 Date Validation**: Smart date pickers with constraints
- **🔄 Return System**: One-click book return functionality
- **🔄 Auto-refresh**: Automatic updates when switching tabs

## 🏗️ Structure

```text
loan-plugin/
├── src/main/java/
│   └── br/edu/ifba/inf008/plugins/
│       ├── LoanPlugin.java              # Main plugin class
│       └── loan/
│           ├── persistence/
│           │   ├── JPAUtil.java         # Delegation to core JPAUtil
│           │   └── LoanDAO.java         # Database operations
│           └── ui/
│               └── LoanManagementController.java  # JavaFX Controller
├── src/main/resources/
│   └── br/edu/ifba/inf008/plugins/loan/ui/
│       ├── loan-management.fxml         # FXML Interface
│       ├── icons/                       # Images and icons
│       └── css/
│           └── loan-styles.css          # Custom styles
└── src/test/
    ├── java/                            # Unit tests
    └── resources/
        └── META-INF/
            └── persistence.xml          # Test configuration (H2)
```

## 🔧 Key Components

### LoanDAO

- Automatic book inventory management
- Active loan tracking
- Transaction safety with rollback
- Test environment detection

### LoanManagementController

- TableView with custom date formatting
- Filtered ComboBoxes for user/book selection
- Date picker configuration and validation
- Form validation and confirmation dialogs

## 🚀 Usage

1. **Create Loan**: Select user and book and set loan date
2. **Search**: Use search field with loan ID or borrower information
3. **Return Book**: Select loan and use return functionality
4. **Update**: Modify loan details with automatic inventory adjustment

## 🔗 Dependencies

- BookStore Core System
- JavaFX 17+
- Hibernate 6.5.2+
- H2 (testing) / MariaDB (production)
