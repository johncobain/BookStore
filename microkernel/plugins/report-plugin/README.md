# 📊 Report Management Plugin

> Advanced reporting and analytics for the BookStore system

## 📋 Overview

The Report Management Plugin provides comprehensive reporting capabilities for the BookStore system. It offers dynamic loan reports with flexible date filtering and CSV export functionality.

## ✨ Features

- **📊 Dynamic Reports**: Generate reports with flexible date ranges
- **📅 Multiple Filters**: All time, before date, after date, on specific date, between dates
- **🎯 Status Reports**: Separate reports for active loans and returned loans
- **📄 CSV Export**: Export reports to CSV with proper formatting
- **📱 Smart TableView**: Color-coded status display (Active/Returned)
- **🔄 Auto-refresh**: Automatic updates when switching tabs

## 🏗️ Structure

```text
report-plugin/
├── src/main/java/
│   └── br/edu/ifba/inf008/plugins/
│       ├── ReportPlugin.java               # Main plugin class
│       └── report/
│           ├── persistence/
│           │   ├── JPAUtil.java            # Delegation to core JPAUtil
│           │   └── ReportDAO.java          # Database operations
│           └── ui/
│               └── ReportManagementController.java  # JavaFX Controller
├── src/main/resources/
│   └── br/edu/ifba/inf008/plugins/report/ui/
│       ├── report-management.fxml          # FXML Interface
│       ├── icons/                          # Images and icons
│       └── css/
│           └── report-styles.css           # Custom styles
└── src/test/
    ├── java/                               # Unit tests
    └── resources/
        └── META-INF/
            └── persistence.xml             # Test configuration (H2)
```

## 🔧 Key Components

### ReportDAO

- Complex JPQL queries with date filtering
- Status-based filtering (active/returned loans)
- Dynamic query building
- Test environment detection

### ReportManagementController

- Dynamic date picker visibility
- CSV export with proper escaping
- Smart TableView
- Real-time report generation

## 🚀 Usage

1. **Choose Date Filter**: All time, before/after date, on date, or between dates
2. **Select and Generate Report Type**: Click on All Loans, Active Loans, or Returned Loans
3. **Export**: Use Export button to save as CSV file

## 📊 Report Types

- **All Loans**: Complete loan history with date filtering
- **Active Loans**: Currently borrowed books (no return date)
- **Returned Loans**: Completed loans (with return date)

## 🔗 Dependencies

- BookStore Core System
- JavaFX 17+
- Hibernate 6.5.2+
- H2 (testing) / MariaDB (production)
