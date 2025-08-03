# 📚 BookStore Microkernel

> Library management system with microkernel architecture and modular plugins

## 📋 Overview

BookStore is a library management system built with a **microkernel architecture**. The core system provides basic infrastructure while plugins handle specific business functionality like user management, book catalog, loans, and reports.

## ✨ Features

- **👥 User Management**: Complete CRUD operations with smart deletion
- **📚 Book Catalog**: Inventory control with availability tracking
- **📋 Loan System**: Book lending with automatic inventory management
- **📊 Reports & Analytics**: Dynamic reports with CSV export
- **🎨 Modern UI**: JavaFX interface with responsive design

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker (for database)

### Running with Script (Recommended)

```bash
# Start database
./app.sh start-db

# Build and run application
./app.sh kickstart

# Re-run without rebuilding
./app.sh run

# See more commands
./app.sh
```

### Running Manually

```bash
# Start database
docker compose up -d

# Build project
mvn clean install

# Run application
mvn exec:java -pl app

# Run tests
mvn test
```

## 🏗️ Architecture

The system uses a **microkernel pattern** with:

- **Core**: Minimal system with plugin loading, UI framework, and persistence
- **Plugins**: Independent modules for specific functionality
- **Shared Models**: User, Book, and Loan entities used across plugins
- **Database**: MariaDB with Hibernate ORM

## 📦 Plugins

| Plugin                                              | Description                           | Status      |
| --------------------------------------------------- | ------------------------------------- | ----------- |
| [👥 User Plugin](plugins/user-plugin/README.md)     | User management with smart deletion   | ✅ Complete |
| [📚 Book Plugin](plugins/book-plugin/README.md)     | Book catalog with inventory control   | ✅ Complete |
| [📦 Loan Plugin](plugins/loan-plugin/README.md)     | Loan system with automatic inventory  | ✅ Complete |
| [📊 Report Plugin](plugins/report-plugin/README.md) | Reports and analytics with CSV export | ✅ Complete |

## 📋 Development Pipeline

See [PIPELINE.md](PIPELINE.md) for detailed development timeline and milestones.
