package br.edu.ifba.inf008.plugins.loan.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.loan.persistence.LoanDAO;
import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.model.User;
import br.edu.ifba.inf008.shell.model.Book;
import javafx.util.StringConverter;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class LoanManagementController {
  LoanDAO loanDAO = new LoanDAO();

  @FXML private TextField searchField;
  @FXML private ToggleGroup searchTypeToggleGroup;
  @FXML private CheckBox activeLoansCheckBox;
  @FXML private ComboBox<User> userComboBox;
  @FXML private ComboBox<Book> bookComboBox;
  @FXML private DatePicker loanDatePicker;
  @FXML private Label returnDateLabel;
  @FXML private DatePicker returnDatePicker;
  @FXML private ListView<Loan> loanListView;
  @FXML private Button saveButton;
  

  private IUIController uiController;
  private final ObservableList<Loan> loans = FXCollections.observableArrayList();
  private Loan currentLoan = null;
  private boolean isUpdating = false;
  
  @FXML
  public void initialize() {
    this.uiController = ICore.getInstance().getUIController();

    loadInitialData();
    loanListView.setItems(loans);
    configureLoanCellFactory();

    userComboBox.setItems(FXCollections.observableArrayList(loanDAO.listUsers()));
    bookComboBox.setItems(FXCollections.observableArrayList(loanDAO.listBooks()));

     userComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(empty || user == null ? null : user.getName() + " (" + user.getEmail() + ")");
      }
    });
    userComboBox.setButtonCell(userComboBox.getCellFactory().call(null));

    bookComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);
        setText(empty || book == null ? null : book.getTitle() + " (" + book.getAuthor() + ")");
      }
    });
    bookComboBox.setButtonCell(bookComboBox.getCellFactory().call(null));

    loanDatePicker.setValue(LocalDate.now());
    loanDatePicker.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty){
        super.updateItem(date,empty);
        setDisable(empty || date.isAfter(LocalDate.now()));
      }
    });
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    loanDatePicker.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate date) {
        return date != null ? date.format(dateFormatter) : "";
      }

      @Override
      public LocalDate fromString(String string) {
        if (string == null || string.trim().isEmpty()) {
          return null;
        }
        return LocalDate.parse(string, dateFormatter);
      }
    });

    returnDatePicker.setValue(LocalDate.now());
    returnDatePicker.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty){
        super.updateItem(date,empty);
        setDisable(empty || date.isBefore(loanDatePicker.getValue()));
      }
    });
    returnDatePicker.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate date) {
        return date != null ? date.format(dateFormatter) : "";
      }

      @Override
      public LocalDate fromString(String string) {
        if (string == null || string.trim().isEmpty()) {
          return null;
        }
        return LocalDate.parse(string, dateFormatter);
      }
    });

    returnDatePicker.setVisible(false);
    returnDateLabel.setVisible(false);
  }

  private void loadInitialData() {
    loans.setAll(loanDAO.findAll());
  }

  private void configureLoanCellFactory() {
    loanListView.setCellFactory(lv -> new ListCell<Loan>(){
      private final HBox hbox = new HBox(10);
      private final Label label = new Label();
      private final Button returnButton = new Button("Return");
      private final Button infoButton = new Button("ℹ️");
      private final Button updateButton = new Button("✏️");
      private final Button deleteButton = new Button("🗑️");
      private final Pane spacer = new Pane();

      {
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(label, spacer, returnButton, infoButton, updateButton, deleteButton);
        
        deleteButton.getStyleClass().add("button-danger");
        updateButton.getStyleClass().add("button-info");
        returnButton.getStyleClass().add("button-success");
      }

      @Override
      protected void updateItem(Loan loan, boolean empty) {
        super.updateItem(loan, empty);
        if (empty || loan == null) {
          setGraphic(null);
          setText(null);
        } else {
          label.setText("ID: " + loan.getLoanId() + " | User: " + loan.getUser().getName() + " | Book: " + loan.getBook().getTitle() + " | Loan: " + formatDate(loan.getLoanDate()) + " - Return: " + formatDate(loan.getReturnDate()));
          setGraphic(hbox);

          if (loan.getReturnDate() != null) {
            returnButton.setDisable(true);
          } else {
            returnButton.setDisable(false);
          }
          returnButton.setOnAction(event -> handleReturn(getItem()));
          infoButton.setOnAction(event -> handleInfo(getItem()));
          updateButton.setOnAction(event -> handleUpdate(getItem()));
          deleteButton.setOnAction(event -> handleDelete(getItem()));
        }
      }
    });
  }

  @FXML
  private void handleSave(){
    User selectedUser = userComboBox.getValue();
    Book selectedBook = bookComboBox.getValue();
    LocalDate selectedDate = loanDatePicker.getValue();
    LocalDate returnDate = returnDatePicker.getValue();
    if (returnDatePicker.getEditor().getText().trim().isEmpty()) {
      returnDate = null;
    }
    if (selectedDate == null) {
        uiController.showAlert("Invalid Date", "Please select a loan date.");
        return;
    }
    if (selectedDate.isAfter(LocalDate.now())) {
        uiController.showAlert("Invalid Date", "Loan date cannot be in the future.");
        return;
    }
    if (returnDate != null && returnDate.isBefore(selectedDate)) {
        uiController.showAlert("Invalid Date", "Return date cannot be before loan date.");
        return;
    }
    if (selectedUser == null) {
      uiController.showAlert("Invalid User", "Please select a user.");
      return;
    }
    if (selectedBook == null) {
      uiController.showAlert("Invalid Book", "Please select a book.");
      return; 
    }
    
    if(isUpdating && currentLoan != null){
      Loan updatedLoan = new Loan(currentLoan.getLoanId(), selectedUser, selectedBook, selectedDate, returnDate);
      int loanIndex = loans.indexOf(currentLoan);
      if (loanIndex == -1) {
        uiController.showAlert("Error", "Loan not found for update.");
        return;
      }
      uiController.showConfirmation(
          "Update Loan", 
          "Are you sure you want to update the loan for book " + selectedBook.getTitle() + "?", 
          () -> {
              try {
                  loanDAO.update(updatedLoan);
                  loans.set(loanIndex, updatedLoan);
                  loanListView.setItems(loans);
                  uiController.showAlert("Success", "Loan updated successfully!");
              } catch (Exception e) {
                  uiController.showAlert("Error", "Failed to update loan: " + e.getMessage());
              }
          }
      );
    }else{
      Loan newLoan = new Loan(selectedUser, selectedBook, selectedDate, null);
      uiController.showConfirmation(
        "Create Loan",
        "Are you sure you want to create a loan for book " + selectedBook.getTitle() + "?",
        () -> {
          try {
            loanDAO.save(newLoan);
            loans.add(newLoan);
            loanListView.setItems(loans);
            uiController.showAlert("Success", "Loan created successfully!");
          } catch (Exception e) {
            uiController.showAlert("Error", "Failed to create loan: " + e.getMessage());
          }
        }
      );
    }
    handleClear();
  }

  @FXML
  private void handleSearch(){
    String field = searchField.getText().toLowerCase().trim();

    if (field.isEmpty()) {
      if(activeLoansCheckBox.isSelected()) {
        loans.setAll(loanDAO.findActiveLoans());
      } else {
        loans.setAll(loanDAO.findAll());
      }
      loanListView.setItems(loans);
      return;
    }
    String searchType = (String) searchTypeToggleGroup.getSelectedToggle().getUserData();

    if (searchType.equals("loanId")) {
      try {
        Integer loanId = Integer.parseInt(field);
        Loan loan = loanDAO.findById(loanId);
        if (loan != null) {
          loans.setAll(loan);
        } else {
          uiController.showAlert("Not Found", "No loan found with ID: " + loanId);
        }
        loanListView.setItems(loans);
        return;
      } catch (NumberFormatException e) {
        uiController.showAlert("Invalid input", "Please enter a valid loan ID.");
        return;
      }
    }

    loans.setAll(
      activeLoansCheckBox.isSelected() ?
      loanDAO.findActiveLoans(field) : loanDAO.findAll(field)
    );
    loanListView.setItems(loans);
  }

  @FXML
  private void handleClear(){
    currentLoan = null;
    isUpdating = false;
    userComboBox.setValue(null);
    bookComboBox.setValue(null);
    loanDatePicker.setValue(LocalDate.now());
    returnDatePicker.setVisible(false);
    returnDateLabel.setVisible(false);
    saveButton.setText("Create Loan");
  }

  private void handleDelete(Loan loan){
    uiController.showConfirmation("Delete Loan", "Are you sure you want to delete this loan?", () -> {
      try {
        loanDAO.delete(loan);
        loans.remove(loan);
        loanListView.setItems(loans);
        uiController.showAlert("Success", "Loan deleted successfully!");
      } catch (Exception e) {
        uiController.showAlert("Error", "Failed to delete loan: " + e.getMessage());
      }
    });
  }
  
  private void handleUpdate(Loan loan){
    isUpdating = true;
    currentLoan = loan;
    userComboBox.setValue(loan.getUser());
    bookComboBox.setValue(loan.getBook());
    loanDatePicker.setValue(loan.getLoanDate());
    returnDatePicker.setValue(loan.getReturnDate());
    returnDatePicker.setVisible(true);
    returnDateLabel.setVisible(true);
    saveButton.setText("Update Loan");
  }
  
  private void handleInfo(Loan loan){
    String message = "ID: " + loan.getLoanId() + "\n" +
                     "User Name: " + loan.getUser().getName() + "\n" +
                     "User Email: " + loan.getUser().getEmail() + "\n" +
                     "Book Title: " + loan.getBook().getTitle() + "\n" +
                     "Book Author: " + loan.getBook().getAuthor() + "\n" +
                     "Loan Date: " + formatDate(loan.getLoanDate()) + "\n" +
                     "Return Date: " + formatDate(loan.getReturnDate());
    uiController.showAlert("Loan info", message);
  }
  
  private void handleReturn(Loan loan){
    uiController.showConfirmation("Return Book", "Are you sure you want to return this book?", () -> {
      try {
        loanDAO.returnLoan(loan);
        Loan updatedLoan = loanDAO.findById(loan.getLoanId());
        loans.set(loans.indexOf(loan), updatedLoan);
        loanListView.setItems(loans);
        uiController.showAlert("Success", "Book returned successfully!");
      } catch (Exception e) {
        uiController.showAlert("Error", "Failed to return book: " + e.getMessage());
      }
    });
  }

  private String formatDate(LocalDate date) {
    return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
  }
}
