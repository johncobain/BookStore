# Development Pipeline - BookStore

**Schedule:** 1 month (until 08/05/2025)
**Goal:** Complete bookstore system with microkernel architecture and plugins

## Overview

The project uses a weekly delivery-focused approach, prioritizing robust configuration in the first week to accelerate development in the following weeks. Using Hibernate as ORM significantly reduces the complexity of data access.

---

## Week 1 (until 07/13): Configuration and Foundation

**Goal:** 100% functional environment and base project compiling

### Essential Tasks

1. **Environment Setup**

   - [x] Install SDKMAN and Java 24
   - [x] Install Docker and start MariaDB with `docker-compose up -d`
   - [x] Test database connection (DBeaver or CLI client)
   - [x] Configure Java 24 in project (`sdk use ...`)

2. **Project Configuration**

   - [x] Run `mvn install` in root directory (`microkernel/`)
   - [x] Create first empty plugin (`user-plugin`)
   - [x] Add plugin to main POM
   - [x] Verify that `mvn install` still works

3. **Hibernate Configuration** ⚠️ **CRITICAL**
   - [x] Add dependencies (`hibernate-core`, `mariadb-java-client`) to plugin POMs
   - [x] Create `persistence.xml` file with database configuration
   - [x] Implement utility class `JPAUtil` to manage EntityManagerFactory
   - [x] Configure `hibernate.hbm2ddl.auto=update`

**⚠️ Note:** This is the most critical week - highest time and study investment, but essential for project success.

## Week 2 (until 07/20): User Plugin (Complete CRUD)

**Goal:** Complete user management functionality as model for other plugins

### Development Tasks

1. **Data Model**

   - [x] Create `User.java` class with JPA annotations (`@Entity`, `@Table`, `@Id`, `@Column`)
   - [x] Define attributes: name, email, registrationDate

2. **Persistence Layer**

   - [x] Implement `UserDAO.java` using EntityManager
   - [x] Methods: `save()`, `update()`, `delete()`, `findById()`, `findAll()`
   - [x] Manage transactions properly

3. **Graphical Interface**

   - [x] Create JavaFX screen for user listing
   - [x] Registration and editing forms
   - [x] Connect UI with DAO

4. **Microkernel Integration**

   - [x] Integrate plugin to main system
   - [x] Create menu/button to access user management

5. **Functional Tests** (Recommended)
   - [x] Create `UserDAOTest.java`
   - [x] Test each CRUD operation
   - [x] Validate business rules

## Week 3 (until 07/27): Pattern Replication

**Goal:** Implement Books and Loans plugins

### Books Plugin

1. **Base Structure**

   - [x] Use `user-plugin` as template for `book-plugin`
   - [x] Create `Book.java` with attributes: title, author, isbn, publicationYear, availableCopies
   - [x] Implement `BookDAO.java` (adapted copy of UserDAO)

2. **Interface and Integration**
   - [x] UI for book management
   - [x] Integration with microkernel

### Loans Plugin

1. **Complex Model**

   - [x] Create `Loan.java` with JPA relationships (`@ManyToOne`)
   - [x] Map relationships with User and Book
   - [x] Attributes: id, user, book, loanDate, returnDate

2. **Business Logic**

   - [x] Implement `LoanDAO.java` with transactions
   - [x] Method `createLoan()`: create loan + decrease stock
   - [x] Method `registerReturn()`: finalize loan + increase stock
   - [x] Availability validations

3. **Advanced Interface**

   - [x] Form with ComboBox for users and books
   - [x] Available copies validation
   - [x] Active loans listing

4. **Functional Tests**
   - [x] `BookDAOTest.java`
   - [x] `LoanDAOTest.java` (especially important to validate business rules)

## Week 4 (until 08/05): Finalization and Delivery

**Goal:** Reports, refinement and final delivery

### Reports Plugin

1. **Implementation**
   - [x] Create `report-plugin`
   - [x] Implement `ReportDAO.java` with JPQL queries
   - [x] UI for report display

### Refinement and Quality

1. **General Review**

   - [x] Evaluate UI/UX - is the system easy to use?
   - [x] Test all main use cases
   - [x] Verify integration between plugins

2. **Documentation**

   - [x] Write final `README.md` with:
     - Compilation instructions (`mvn install`)
     - Execution instructions (`mvn exec:java -pl app`)
     - System usage guide

3. **Delivery Preparation**
   - [ ] Run `mvn clean` to clean temporary files
   - [ ] Test compilation and execution in clean environment
   - [ ] Compress source code (`.zip` or `.tar.gz`)

### Final Delivery

- [ ] **Email with exact subject:** `INF008 T2 Andrey Gomes da Silva Nascimento`
- [ ] Attach compressed project file
- [ ] Include execution instructions

---
