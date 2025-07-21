package br.edu.ifba.inf008.plugins.loan.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.loan.persistence.LoanDAO;
import br.edu.ifba.inf008.shell.model.Loan;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class LoanManagementController {
  LoanDAO loanDAO = new LoanDAO();

  @FXML private TextField searchField;
  @FXML private ToggleGroup searchTypeToggleGroup;
  @FXML private CheckBox activeLoansCheckBox;

  @FXML private ListView<Loan> loanListView;

  private IUIController uiController;
  private final ObservableList<Loan> loans = FXCollections.observableArrayList();
  
  @FXML
  public void initialize() {
    this.uiController = ICore.getInstance().getUIController();

    loadInitialData();
    loanListView.setItems(loans);

    configureLoanCellFactory();
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

          returnButton.setOnAction(event -> handleReturn(getItem()));
          infoButton.setOnAction(event -> handleInfo(getItem()));
          updateButton.setOnAction(event -> handleUpdate(getItem()));
          deleteButton.setOnAction(event -> handleDelete(getItem()));
        }
      }
    });
  }

  @FXML
  private void handleSave(){}

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
  private void handleClear(){}

  private void handleDelete(Loan loan){
    System.out.println("deleting");
  }
  
  private void handleUpdate(Loan loan){
    System.out.println("updating");
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
    System.out.println("returning");
  }

  private String formatDate(LocalDate date) {
    return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
  }
}
