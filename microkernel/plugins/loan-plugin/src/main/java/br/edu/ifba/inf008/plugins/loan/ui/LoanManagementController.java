package br.edu.ifba.inf008.plugins.loan.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IRefreshable;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.loan.persistence.LoanDAO;
import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.model.User;
import br.edu.ifba.inf008.shell.model.Book;
import javafx.util.StringConverter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class LoanManagementController implements IRefreshable {
  LoanDAO loanDAO = new LoanDAO();

  @FXML private TextField searchField;
  @FXML private ToggleGroup searchTypeToggleGroup;
  @FXML private CheckBox activeLoansCheckBox;
  @FXML private ComboBox<User> userComboBox;
  @FXML private ComboBox<Book> bookComboBox;
  @FXML private DatePicker loanDatePicker;
  @FXML private Label returnDateLabel;
  @FXML private DatePicker returnDatePicker;
  @FXML private TableView<Loan> loanTableView;
  @FXML private Button saveButton;
  @FXML private Button returnButton;
  @FXML private Button updateButton;
  @FXML private Button deleteButton;

  private IUIController uiController;
  private final ObservableList<Loan> loans = FXCollections.observableArrayList();
  private Loan currentLoan = null;
  private boolean isUpdating = false;

  @Override
  public void refresh() {
    loadInitialData();
    searchField.clear(); 
    configureLoanComboBoxes();
    handleClear();
  }
  
  @FXML
  public void initialize() {
    this.uiController = ICore.getInstance().getUIController();

    loadInitialData();
    configureLoanTableView();
    configureLoanComboBoxes();
    configureLoanDatePicker();
    setupButtonStates();
  }

  private void loadInitialData() {
    loans.setAll(loanDAO.findAll());
    loanTableView.setItems(loans);
  }
  
  @FXML
  private void handleReturnSelected(){
    Loan selectedLoan = loanTableView.getSelectionModel().getSelectedItem();
    if(selectedLoan != null){
      handleReturn(selectedLoan);
    }else{
      uiController.showAlert("No Selection", "Please select a loan to Return.");
    }
  }

  @FXML
  private void handleUpdateSelected(){
    Loan selectedLoan = loanTableView.getSelectionModel().getSelectedItem();
    if(selectedLoan != null){
      handleUpdate(selectedLoan);
    }else{
      uiController.showAlert("No Selection", "Please select a loan to Update.");
    }
  }

  @FXML
  private void handleDeleteSelected(){
    Loan selectedLoan = loanTableView.getSelectionModel().getSelectedItem();
    if(selectedLoan != null){
      handleDelete(selectedLoan);
    }else{
      uiController.showAlert("No Selection", "Please select a loan to Delete.");
    }
  }
  
  @FXML
  private void handleSave(){
    User selectedUser = userComboBox.getValue();
    Book selectedBook = bookComboBox.getValue();
    LocalDate selectedDate = loanDatePicker.getValue();
    LocalDate returnDate = returnDatePicker.getValue();
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
    if (returnDatePicker.getEditor().getText().trim().isEmpty()) {
      returnDate = null;
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

    loanTableView.getSelectionModel().clearSelection();
  }
  
  
  private void handleReturn(Loan loan){
    uiController.showConfirmation("Return Book", "Are you sure you want to return this book?", () -> {
      try {
        loanDAO.returnLoan(loan);
        Loan updatedLoan = loanDAO.findById(loan.getLoanId());
        loans.set(loans.indexOf(loan), updatedLoan);
        uiController.showAlert("Success", "Book returned successfully!");
      } catch (Exception e) {
        uiController.showAlert("Error", "Failed to return book: " + e.getMessage());
      }
    });
  }

  private void configureLoanTableView() {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    loanTableView.setPlaceholder(new Label("No loans found"));

    TableColumn<Loan, Integer> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));

    TableColumn<Loan, String> userNameCol = new TableColumn<>("User Name");
    userNameCol.setCellValueFactory(cellData -> {
      User user = cellData.getValue().getUser();
      return new javafx.beans.property.SimpleStringProperty(user != null ? user.getName() : "");
    });

    TableColumn<Loan, String> userEmailCol = new TableColumn<>("User Email");
    userEmailCol.setCellValueFactory(cellData -> {
      User user = cellData.getValue().getUser();
      return new javafx.beans.property.SimpleStringProperty(user != null ? user.getEmail() : "");
    });

    TableColumn<Loan, String> bookTitleCol = new TableColumn<>("Book Title");
    bookTitleCol.setCellValueFactory(cellData -> {
      Book book = cellData.getValue().getBook();
      return new javafx.beans.property.SimpleStringProperty(book != null ? book.getTitle() : "");
    });

    TableColumn<Loan, String> bookAuthorCol = new TableColumn<>("Book Author");
    bookAuthorCol.setCellValueFactory(cellData -> {
      Book book = cellData.getValue().getBook();
      return new javafx.beans.property.SimpleStringProperty(book != null ? book.getAuthor() : "");
    });

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
        if (empty){
          setText("");
        } else if (date == null) {
          setText("Active");
        }else{
          setText(date.format(dateFormatter));
        }
      }
    });

    loanTableView.getColumns().addAll(
      Arrays.asList(idCol, userNameCol, userEmailCol, bookTitleCol, bookAuthorCol, loanDateCol, returnDateCol)
    );

    loanTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private void setupButtonStates(){
    returnButton.setDisable(true);
    updateButton.setDisable(true);
    deleteButton.setDisable(true);

    loanTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      boolean hasSelection = newSelection != null;
      returnButton.setDisable(!hasSelection || newSelection.getReturnDate() != null);
      updateButton.setDisable(!hasSelection);
      deleteButton.setDisable(!hasSelection);
    });
  }

  private void configureLoanComboBoxes(){
    List<User> users = loanDAO.listUsers();
    List<Book> books = loanDAO.listBooks();
    userComboBox.setItems(FXCollections.observableArrayList(users));
    bookComboBox.setItems(FXCollections.observableArrayList(books));

     userComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(empty || user == null ? null : user.getName() + " (" + user.getEmail() + ")");
      }
    });
    userComboBox.setConverter(new StringConverter<User>() {
      @Override
      public String toString(User user) {
        return user == null ? "" : user.getName();
      }

      @Override
      public User fromString(String string) {
        return users.stream()
          .filter(u -> u.getName().equals(string))
          .findFirst()
          .orElse(null);
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
    bookComboBox.setConverter(new StringConverter<Book>() {
      @Override
      public String toString(Book book) {
        return book == null ? "" : book.getTitle();
      }

      @Override
      public Book fromString(String string) {
        return books.stream()
          .filter(b -> b.getTitle().equals(string))
          .findFirst()
          .orElse(null);
      }
    });
    bookComboBox.setButtonCell(bookComboBox.getCellFactory().call(null));

    userComboBox.setEditable(true);
    bookComboBox.setEditable(true);

    FilteredList<User> filteredUsers = new FilteredList<>(FXCollections.observableArrayList(users), p -> true);
      userComboBox.setItems(filteredUsers);
      userComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> Platform.runLater(() -> {
        if (userComboBox.getSelectionModel().getSelectedItem() == null ||
              !userComboBox.getSelectionModel().getSelectedItem().getName().equals(newText)) {
          filteredUsers.setPredicate(user -> user.getName().toLowerCase().contains(newText.toLowerCase().trim()));
        }
      }));
      
      userComboBox.getEditor().setOnMouseClicked(e -> {
        if (!userComboBox.isShowing()) {
          userComboBox.show();
        }
      });

      FilteredList<Book> filteredBooks = new FilteredList<>(FXCollections.observableArrayList(books), p -> true);
      bookComboBox.setItems(filteredBooks);
      bookComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> Platform.runLater(() -> {
          if (bookComboBox.getSelectionModel().getSelectedItem() == null ||
              !bookComboBox.getSelectionModel().getSelectedItem().getTitle().equals(newText)) {
            filteredBooks.setPredicate(book -> book.getTitle().toLowerCase().contains(newText.toLowerCase().trim()));
          }
      }));

      bookComboBox.getEditor().setOnMouseClicked(e -> {
        if (!bookComboBox.isShowing()) {
          bookComboBox.show();
        }
      });
  }

  private void configureLoanDatePicker() {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    loanDatePicker.setValue(LocalDate.now());
    loanDatePicker.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty){
        super.updateItem(date,empty);
        setDisable(empty || date.isAfter(LocalDate.now()));
      }
    });
    loanDatePicker.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate date) {
        return date != null ? date.format(dateFormatter) : "";
      }

      @Override
      public LocalDate fromString(String string) {
        return string != null && !string.isEmpty() ? LocalDate.parse(string, dateFormatter) : null;
      }
    });
    loanDatePicker.setEditable(false);

    returnDatePicker.setValue(null);
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
    returnDatePicker.setEditable(true);
    returnDatePicker.getEditor().textProperty().addListener((obs, oldText, newText) -> {
      if (returnDatePicker.getEditor().getText().trim().isEmpty()) {
        returnDatePicker.setValue(null);
      }else {
        try {
          LocalDate parsedDate = LocalDate.parse(newText, dateFormatter);
          returnDatePicker.setValue(parsedDate);
        } catch (Exception e) {
        }
      }
    });

    returnDatePicker.setVisible(false);
    returnDateLabel.setVisible(false);
  }
}
