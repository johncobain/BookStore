# 📚 BookStore - Microkernel Library Management System

A complete library management system built with **microkernel architecture** and **modular plugins**. Academic project demonstrating advanced software engineering concepts.

## 🏗️ Architecture

This project uses a **microkernel pattern** with modular plugins:

- **Core System**: Basic infrastructure with plugin loading and persistence
- **Plugins**: Independent modules handling specific business functionality
- **Shared Models**: Common entities (User, Book, Loan) used across plugins

## 🛠️ Technologies

- **Java 21+** - Programming language
- **Maven** - Build and dependency management
- **Hibernate 6.5** - ORM and persistence
- **JavaFX** - Graphical user interface
- **MariaDB** - Database
- **Docker** - Database containerization

## 🚀 Quick Start

```bash
# Navigate to microkernel directory
cd microkernel/

# Start database
docker compose up -d

# Build and run
mvn clean install
mvn exec:java -pl app
```

## 📝 Documentation

- **[Main System Documentation](microkernel/README.md)** - Complete setup and usage guide
- **[Development Pipeline](microkernel/PIPELINE.md)** - Project timeline and milestones

### Plugin Documentation

- **[👥 User Plugin](microkernel/plugins/user-plugin/README.md)** - User management with smart deletion
- **[📚 Book Plugin](microkernel/plugins/book-plugin/README.md)** - Book catalog with inventory control
- **[📋 Loan Plugin](microkernel/plugins/loan-plugin/README.md)** - Loan system with automatic inventory
- **[📊 Report Plugin](microkernel/plugins/report-plugin/README.md)** - Reports and analytics with CSV export

## 📹 Video Demonstration

Watch the video demonstration of the BookStore system [here (Demonstração BookStore Blackbird - Projeto POO JavaFX)](https://youtu.be/m16Ob3twXm8).

## 👥 Author

Developed for INF008 course at IFBA by [Andrey Gomes](https://github.com/johncobain)
