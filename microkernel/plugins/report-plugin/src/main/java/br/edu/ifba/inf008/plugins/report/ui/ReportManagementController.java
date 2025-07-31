package br.edu.ifba.inf008.plugins.report.ui;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.report.persistence.ReportDAO;
import br.edu.ifba.inf008.shell.model.Loan;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class ReportManagementController {
  ReportDAO reportDAO = new ReportDAO();

  @FXML private ToggleGroup searchTypeToggleGroup;
  @FXML private Label initialDateLabel;
  @FXML private DatePicker initialDatePicker;
  @FXML private Label finalDateLabel;
  @FXML private DatePicker finalDatePicker;
  @FXML private TableView<Loan> loanTableView;


  private IUIController uiController;

  @FXML
  public void initialize() {
    this.uiController = ICore.getInstance().getUIController();

    configureDatePicker();
    configureLoanTableView();

    searchTypeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
      if (newToggle != null) {
        String type = newToggle.getUserData().toString();
        boolean showInitial = !"allTime".equals(type);
        boolean showFinal = "between".equals(type);

        initialDatePicker.setVisible(showInitial);
        initialDateLabel.setVisible(showInitial);

        finalDatePicker.setVisible(showFinal);
        finalDateLabel.setVisible(showFinal);
      }
    });
  }

  @FXML
  private void handleAllLoans() {
    List<Loan> loans;
    loans = switch (searchTypeToggleGroup.getSelectedToggle().getUserData().toString()) {
      case "allTime" -> reportDAO.findAll();
      case "before" -> reportDAO.findByInitialDate(initialDatePicker.getValue(), "before");
      case "after" -> reportDAO.findByInitialDate(initialDatePicker.getValue(), "after");
      case "onDate" -> reportDAO.findByInitialDate(initialDatePicker.getValue(), "onDate");
      case "between" -> reportDAO.findByInitialAndFinalDate(initialDatePicker.getValue(), finalDatePicker.getValue());
      default -> reportDAO.findAll();
    };
    loanTableView.setItems(FXCollections.observableArrayList(loans));
  }

  @FXML
  private void handleActiveLoans() {
    List<Loan> loans;
    loans = switch (searchTypeToggleGroup.getSelectedToggle().getUserData().toString()) {
      case "allTime" -> reportDAO.findByStatus(true);
      case "before" -> reportDAO.findByStatusAndInitialDate(true, initialDatePicker.getValue(), "before");
      case "after" -> reportDAO.findByStatusAndInitialDate(true, initialDatePicker.getValue(), "after");
      case "onDate" -> reportDAO.findByStatusAndInitialDate(true, initialDatePicker.getValue(), "onDate");
      case "between" -> reportDAO.findByStatusAndInitialDate(true, initialDatePicker.getValue(), "between");
      default -> reportDAO.findByStatus(true);
    };
    loanTableView.setItems(FXCollections.observableArrayList(loans));
  }

  @FXML
  private void handleReturnedLoans() {
    List<Loan> loans;
    loans = switch (searchTypeToggleGroup.getSelectedToggle().getUserData().toString()) {
      case "allTime" -> reportDAO.findByStatus(false);
      case "before" -> reportDAO.findByStatusAndInitialDate(false, initialDatePicker.getValue(), "before");
      case "after" -> reportDAO.findByStatusAndInitialDate(false, initialDatePicker.getValue(), "after");
      case "onDate" -> reportDAO.findByStatusAndInitialDate(false, initialDatePicker.getValue(), "onDate");
      case "between" -> reportDAO.findByStatusAndInitialDate(false, initialDatePicker.getValue(), "between");
      default -> reportDAO.findByStatus(false);
    };
    loanTableView.setItems(FXCollections.observableArrayList(loans));
  }

  @FXML
  private void handleExportReport(){
    List<Loan> loans = loanTableView.getItems();
    if (loans == null || loans.isEmpty()) {
      uiController.showAlert("No Data", "No loans available to export.");
      return;
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export Report to CSV");
    fileChooser.setInitialFileName("report.csv");
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
      new FileChooser.ExtensionFilter("All Files", "*.*")
    );

    File file = fileChooser.showSaveDialog(loanTableView.getScene().getWindow());
    if (file == null) return;

    try(PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
      writer.println("Loan ID,User Name,User Email,Book Title,Book Author,Loan Date,Return Date");
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      for (Loan loan : loans) {
        String line = String.format("%d,%s,%s,%s,%s,%s,%s",
          loan.getLoanId(),
          escapeCsv(loan.getUser().getName()),
          escapeCsv(loan.getUser().getEmail()),
          escapeCsv(loan.getBook().getTitle()),
          escapeCsv(loan.getBook().getAuthor()),
          loan.getLoanDate() != null ? loan.getLoanDate().format(dateFormatter) : "",
          loan.getReturnDate() != null ? loan.getReturnDate().format(dateFormatter) : ""
        );
        writer.println(line); 
      }

      uiController.showAlert("Export Success", "Report exported to " + file.getAbsolutePath());
    } catch (Exception e) {
      uiController.showAlert("Export Error", "Failed to export report: " + e.getMessage());
    }
  }

  private String escapeCsv(String value) {
    if (value == null) return "";
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }

  private void configureDatePicker(){
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    initialDatePicker.setValue(LocalDate.now());
    initialDatePicker.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate date) {
        return date != null ? date.format(dateFormatter) : "";
      }

      @Override
      public LocalDate fromString(String string) {
        return string != null && !string.isEmpty() ? LocalDate.parse(string, dateFormatter) : null;
      }
    });

    initialDatePicker.setEditable(false);
    initialDatePicker.setVisible(false);
    initialDateLabel.setVisible(false);
    
    finalDatePicker.setValue(LocalDate.now());
    finalDatePicker.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate date) {
        return date != null ? date.format(dateFormatter) : "";
      }

      @Override
      public LocalDate fromString(String string) {
        return string != null && !string.isEmpty() ? LocalDate.parse(string, dateFormatter) : null;
      }
    });

    finalDatePicker.setEditable(false);
    finalDatePicker.setVisible(false);
    finalDateLabel.setVisible(false);
  }

  private void configureLoanTableView() {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    loanTableView.setPlaceholder(new Label("No loans found"));
    TableColumn<Loan, Integer> idCol = new TableColumn<>("Loan ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));

    TableColumn<Loan, String> userNameCol = new TableColumn<>("User Name");
    userNameCol.setCellValueFactory(cellData -> 
      new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser().getName()));

    TableColumn<Loan, String> userEmailCol = new TableColumn<>("User Email");
    userEmailCol.setCellValueFactory(cellData -> 
      new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser().getEmail()));

    TableColumn<Loan, String> bookTitleCol = new TableColumn<>("Book Title");
    bookTitleCol.setCellValueFactory(cellData -> 
      new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBook().getTitle()));

    TableColumn<Loan, String> bookAuthorCol = new TableColumn<>("Book Author");
    bookAuthorCol.setCellValueFactory(cellData -> 
      new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBook().getAuthor()));

    TableColumn<Loan, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
    loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
    loanDateCol.setCellFactory(column -> new javafx.scene.control.TableCell<Loan, LocalDate>() {
      @Override
      protected void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setText(empty || date == null ? "" : date.format(dateFormatter));
      }
    });

    TableColumn<Loan, LocalDate> returnDateCol = new TableColumn<>("Return Date");
    returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
    returnDateCol.setCellFactory(column -> new javafx.scene.control.TableCell<Loan, LocalDate>() {
      @Override
      protected void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setText(empty || date == null ? "" : date.format(dateFormatter));
      }
    });

    loanTableView.getColumns().addAll(
      Arrays.asList(
        idCol, userNameCol, userEmailCol, bookTitleCol, bookAuthorCol, loanDateCol, returnDateCol
      )
    );
    loanTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }
}
