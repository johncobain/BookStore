package br.edu.ifba.inf008.plugins.book.ui;

import java.util.Arrays;
import java.util.List;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IRefreshable;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.book.persistence.BookDAO;
import br.edu.ifba.inf008.shell.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

public class BookManagementController implements IRefreshable{
  BookDAO bookDAO = new BookDAO();

  @FXML private TextField searchField;
  @FXML private ToggleGroup searchTypeToggleGroup;
  @FXML private CheckBox availableOnlyCheckBox;
  @FXML private TextField formTitleField;
  @FXML private TextField formAuthorField;
  @FXML private TextField formIsbnField;
  @FXML private TextField formYearField;
  @FXML private TextField formCopiesField;
  @FXML private Button saveButton;
  @FXML private TableView<Book> bookTableView;
  @FXML private Button updateButton;
  @FXML private Button deleteButton;

  private IUIController uiController;
  private final ObservableList<Book> books = FXCollections.observableArrayList();
  private Book currentBook = null;
  private boolean isUpdating = false;

  @Override
  public void refresh() {
    loadInitialData();
    searchField.clear();
    availableOnlyCheckBox.setSelected(false);
    handleClear();
  }

  @FXML
    public void initialize() {
    this.uiController = ICore.getInstance().getUIController();

    formYearField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*")) {
        formYearField.setText(newValue.replaceAll("[^\\d]", ""));
      }
    });
    
    formCopiesField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*")) {
          formCopiesField.setText(newValue.replaceAll("[^\\d]", ""));
      }
    });

    loadInitialData();
    configureBookTableView();
    setupButtonStates();
  }

  private void loadInitialData() {
    books.setAll(bookDAO.findAll());
    bookTableView.setItems(books);
  }

  @FXML
  private void handleUpdateSelected() {
    Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
      handleUpdate(selectedBook);
    } else {
      uiController.showAlert("No Selection", "Please select a Book to Update.");
    }
  }

  @FXML
  private void handleDeleteSelected() {
    Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
    if (selectedBook != null) {
      handleDelete(selectedBook);
    } else {
      uiController.showAlert("No Selection", "Please select a Book to Delete.");
    }
  }

  @FXML
  private void handleSave(){
    String title = formTitleField.getText().trim();
    String author = formAuthorField.getText().trim();
    String isbn = formIsbnField.getText().trim();
    String yearText = formYearField.getText().trim();
    String copiesText = formCopiesField.getText().trim();

    if(title.isEmpty() || author.isEmpty() || isbn.isEmpty() || yearText.isEmpty() || copiesText.isEmpty()) {
      this.uiController.showAlert("Validation Error", "All fields must be filled.");
      return;
    }

    int publishedYear;
    int copiesAvailable;
    try {
      publishedYear = Integer.parseInt(yearText);
      copiesAvailable = Integer.parseInt(copiesText);
    } catch (NumberFormatException e) {
      this.uiController.showAlert("Validation Error", "Year and Copies must be valid numbers.");
      return;
    }

    if(isUpdating && currentBook != null) {
      Book updatedBook = new Book(currentBook.getBookId(), title, author, isbn, publishedYear, copiesAvailable);
      int bookIndex = books.indexOf(currentBook);
      if (bookIndex == -1) {
        this.uiController.showAlert("Error", "Book not found for update.");
        return;
      }
      uiController.showConfirmation(
          "Update Book", 
          "Are you sure you want to update the book " + currentBook.getTitle() + "?", 
          () -> {
              try {
                  bookDAO.update(updatedBook);
                  books.set(bookIndex, updatedBook);
                  uiController.showAlert("Success", "Book updated successfully!");
              } catch (Exception e) {
                  uiController.showAlert("Error", "Failed to update book: " + e.getMessage());
              }
          }
      );
    } else {
      Book newBook = new Book(title, author, isbn, publishedYear, copiesAvailable);
      uiController.showConfirmation(
          "Create Book", 
          "Are you sure you want to create the book " + newBook.getTitle() + "?", 
          () -> {
              try {
                  bookDAO.save(newBook);
                  books.add(newBook);
                  uiController.showAlert("Success", "Book created successfully!");
              } catch (Exception e) {
                  uiController.showAlert("Error", "Failed to create book: " + e.getMessage());
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
      if(availableOnlyCheckBox.isSelected()) {
        books.setAll(bookDAO.findAvailableBooks());
      } else {
        books.setAll(bookDAO.findAll());
      }
      return;
    }
    String searchType = (String) searchTypeToggleGroup.getSelectedToggle().getUserData();

    List<Book> searchResults;

    if(availableOnlyCheckBox.isSelected()) {
      searchResults = bookDAO.findAvailableBooks(searchType, field);
    } else {
      searchResults = bookDAO.findAll(searchType, field);
    }

    if (searchResults != null && !searchResults.isEmpty()) {
      books.setAll(searchResults);
    } else {
      books.clear();
    }
  }

  @FXML
  private void handleClear(){
    currentBook = null;
    isUpdating = false;
    formTitleField.clear();
    formAuthorField.clear();
    formIsbnField.clear();
    formYearField.clear();
    formCopiesField.clear();
    saveButton.setText("Create Book");
    bookTableView.getSelectionModel().clearSelection();
  }

  private void handleDelete(Book book){
    uiController.showConfirmation(
        "Delete Book", 
        "Are you sure you want to delete the book " + book.getTitle() + "?", 
        () -> {
            try {
                bookDAO.delete(book);
                books.remove(book);
                uiController.showAlert("Success", "Book deleted successfully!");
            } catch (Exception e) {
                uiController.showAlert("Error", "Failed to delete book: " + e.getMessage());
            }
        }
    );
  }

  private void handleUpdate(Book book){
    currentBook = book;
    isUpdating = true;
    formTitleField.setText(book.getTitle());
    formAuthorField.setText(book.getAuthor());
    formIsbnField.setText(book.getIsbn());
    formYearField.setText(String.valueOf(book.getPublishedYear()));
    formCopiesField.setText(String.valueOf(book.getCopiesAvailable()));
    saveButton.setText("Update Book");
  }

  private void configureBookTableView() {
    bookTableView.setPlaceholder(new Label("No books found"));

    TableColumn<Book, Integer> idColumn = new TableColumn<>("ID");
    idColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));

    TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

    TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
    authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

    TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
    isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

    TableColumn<Book, Integer> yearColumn = new TableColumn<>("Published Year");
    yearColumn.setCellValueFactory(new PropertyValueFactory<>("publishedYear"));

    TableColumn<Book, Integer> copiesColumn = new TableColumn<>("Copies Available");
    copiesColumn.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));

    bookTableView.getColumns().addAll(
      Arrays.asList(idColumn, titleColumn, authorColumn, isbnColumn, yearColumn, copiesColumn));
    bookTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private void setupButtonStates() {
    updateButton.setDisable(true);
    deleteButton.setDisable(true);

    bookTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      boolean hasSelection = newSelection != null;
      updateButton.setDisable(!hasSelection);
      deleteButton.setDisable(!hasSelection);
    });
  }
}
