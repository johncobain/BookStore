package br.edu.ifba.inf008.plugins.book.ui;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.book.persistence.BookDAO;
import br.edu.ifba.inf008.shell.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class BookManagementController {
  BookDAO bookDAO = new BookDAO();

  @FXML private TextField searchField;
  @FXML private ToggleGroup searchTypeToggleGroup;
  @FXML private TextField formTitleField;
  @FXML private TextField formAuthorField;
  @FXML private TextField formIsbnField;
  @FXML private TextField formYearField;
  @FXML private TextField formCopiesField;
  @FXML private Button saveButton;
  @FXML private ListView<Book> bookListView;

  private IUIController uiController;
  private final ObservableList<Book> books = FXCollections.observableArrayList();
  private Book currentBook = null;
  private boolean isUpdating = false;

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
    bookListView.setItems(books);

    configureBookCellFactory();
  }

  private void loadInitialData() {
    books.addAll(bookDAO.findAll());
  }

  private void configureBookCellFactory() {
    bookListView.setCellFactory(lv -> new ListCell<Book>(){
      private final HBox hbox = new HBox(10);
      private final Label label = new Label();
      private final Button infoButton = new Button("ℹ️");
      private final Button updateButton = new Button("✏️");
      private final Button deleteButton = new Button("🗑️");
      private final Pane spacer = new Pane();

      {
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(label, spacer, infoButton, updateButton, deleteButton);
        
        deleteButton.getStyleClass().add("button-danger");
        updateButton.getStyleClass().add("button-info");
      }

      @Override
      protected void updateItem(Book book, boolean empty) {
      super.updateItem(book, empty);
      if (empty || book == null) {
          setText(null);
          setGraphic(null);
      } else {
          label.setText(book.getTitle() + " (" + book.getPublishedYear() + ") - " + book.getAuthor() + " - ISBN: " + book.getIsbn() + " - Copies: " + book.getCopiesAvailable());
          setGraphic(hbox);

          infoButton.setOnAction(event -> handleInfo(getItem()));
          updateButton.setOnAction(event -> handleUpdate(getItem()));
          deleteButton.setOnAction(event -> handleDelete(getItem()));
        }
      }
    });
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
                  bookListView.setItems(books);
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
                  bookListView.setItems(books);
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
    String query = searchField.getText().toLowerCase().trim();

    if (query.isEmpty()) {
      bookListView.setItems(books);
      return;
    }

    ObservableList<Book> filteredBooks = books.filtered(
      book -> {
        if (searchTypeToggleGroup.getSelectedToggle().getUserData().equals("title")){
          return book.getTitle().toLowerCase().contains(query);
        } else if (searchTypeToggleGroup.getSelectedToggle().getUserData().equals("author")) {
          return book.getAuthor().toLowerCase().contains(query);
        } else {
          return book.getIsbn().toLowerCase().contains(query);
        }
      }
    );
    bookListView.setItems(filteredBooks);
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
  }

  private void handleDelete(Book book){
    uiController.showConfirmation(
        "Delete Book", 
        "Are you sure you want to delete the book " + book.getTitle() + "?", 
        () -> {
            try {
                bookDAO.delete(book);
                books.remove(book);
                bookListView.setItems(books);
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

  private void handleInfo(Book book){
    uiController.showAlert("Book info", "Id: " + book.getBookId() + "\nTitle: " + book.getTitle() + "\nAuthor: " + book.getAuthor() +
        "\nPublished Year: " + book.getPublishedYear() + "\nISBN: " + book.getIsbn() + "\nCopies Available: " + book.getCopiesAvailable());
}
}
