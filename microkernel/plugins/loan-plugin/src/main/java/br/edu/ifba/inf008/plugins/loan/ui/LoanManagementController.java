package br.edu.ifba.inf008.plugins.loan.ui;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.loan.persistence.LoanDAO;
import br.edu.ifba.inf008.shell.model.Loan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class LoanManagementController {
  LoanDAO loanDAO = new LoanDAO();

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
          label.setText("User: " + loan.getUser().getName() + " (" + loan.getUser().getEmail() + ") | Book: " + loan.getBook().getTitle() + " (" + loan.getBook().getAuthor() + ") | Loan Date: " + loan.getLoanDate() + " - Return Date: " + (loan.getReturnDate() != null ? loan.getReturnDate() : "Not Returned"));
          setGraphic(hbox);

          // returnButton.setOnAction(event -> handleReturn(getItem()));
          // infoButton.setOnAction(event -> handleInfo(getItem()));
          // updateButton.setOnAction(event -> handleUpdate(getItem()));
          // deleteButton.setOnAction(event -> handleDelete(getItem()));
        }
      }
    });
  }
}
