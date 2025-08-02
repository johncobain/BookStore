package br.edu.ifba.inf008.plugins.user.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IRefreshable;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.user.persistence.UserDAO;
import br.edu.ifba.inf008.shell.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserManagementController implements IRefreshable {
  UserDAO userDAO = new UserDAO();

  @FXML private TextField searchField;
  @FXML private ToggleGroup searchTypeToggleGroup;
  @FXML private TextField formNameField;
  @FXML private TextField formEmailField;
  @FXML private Button saveButton;
  @FXML private TableView<User> userTableView;
  @FXML private Button updateButton;
  @FXML private Button deleteButton;
  
  private IUIController uiController;
  private final ObservableList<User> users = FXCollections.observableArrayList();
  private User currentUser = null;
  private boolean isUpdating = false;
  
  @Override
  public void refresh() {
    loadInitialData();
    searchField.clear(); 
    handleClear();
  }

  @FXML
  public void initialize() {
    this.uiController = ICore.getInstance().getUIController();

    loadInitialData();
    configureUserTableView();
    setupButtonStates();
  }

  private void loadInitialData() {
    users.setAll(userDAO.findAll());
    userTableView.setItems(users);
  }

  @FXML
  private void handleUpdateSelected(){
    User selectedUser = userTableView.getSelectionModel().getSelectedItem();
    if(selectedUser != null){
      handleUpdate(selectedUser);
    }else{
      uiController.showAlert("No Selection", "Please select an User to Update.");
    }
  }
  
  @FXML
  private void handleDeleteSelected(){
    User selectedUser = userTableView.getSelectionModel().getSelectedItem();
    if(selectedUser != null){
      handleDelete(selectedUser);
    }else{
      uiController.showAlert("No Selection", "Please select an User to Delete.");
    }
  }

  @FXML
  private void handleSave(){
    String name = formNameField.getText().trim();
    String email = formEmailField.getText().trim();
    if(name.isEmpty() || email.isEmpty()) {
      this.uiController.showAlert("Validation Error", "All fields must be filled.");
      return;
    } 
    
    if(isUpdating && currentUser != null) {
      User updatedUser = new User(currentUser.getUserId(), name, email, currentUser.getRegisteredAt());
      int userIndex = users.indexOf(currentUser);
      if (userIndex == -1) {
        this.uiController.showAlert("Error", "User not found for update.");
        return;
      }
      uiController.showConfirmation(
        "Update User", 
        "Are you sure you want to update the user " + currentUser.getName() + "?", 
        () -> {
          try {
            userDAO.update(updatedUser);
            users.set(userIndex, updatedUser);
            uiController.showAlert("Success", "User updated successfully!");
          } catch (Exception e) {
            uiController.showAlert("Error", "Failed to update user: " + e.getMessage());
          }
        }
      );
    } else {
      User newUser = new User(name, email);
      uiController.showConfirmation(
        "Create User", 
        "Are you sure you want to create the user " + newUser.getName() + "?", 
        () -> {
          try {
            userDAO.save(newUser);
            users.add(newUser);
            uiController.showAlert("Success", "User created successfully!");
          } catch (Exception e) {
            uiController.showAlert("Error", "Failed to create user: " + e.getMessage());
          }
        }
      );
    }
    handleClear();
  }

  @FXML
  private void handleSearch() {
    String field = searchField.getText().toLowerCase().trim();

    if (field.isEmpty()) {
      users.setAll(userDAO.findAll());
      return;
    } 
    String searchType = searchTypeToggleGroup.getSelectedToggle().getUserData().toString();

    List<User> searchResults = userDAO.findAll(searchType, field);
    if (searchResults != null && !searchResults.isEmpty()) {
      users.setAll(searchResults);
    } else {
      users.clear();
    }
  }

  @FXML
  private void handleClear(){
    currentUser = null;
    isUpdating = false;
    formNameField.clear();
    formEmailField.clear();
    saveButton.setText("Create User");
    userTableView.getSelectionModel().clearSelection();
  }

  private void handleDelete(User user) {
    boolean hasLoans = userDAO.hasLoans(user);

    if(hasLoans){
      String warningMessage = String.format("""
        \u26a0\ufe0f WARNING: User '%s' has loan records in the system!
        
        Deleting this user will:
        \u2022 Delete ALL loan records for this user
        \u2022 Return any currently borrowed books (increment available copies)
        \u2022 This action CANNOT be undone
        
        Are you sure you want to proceed?""",
        user.getName()
      );

      uiController.showConfirmation(
        "Delete User with Loans", 
        warningMessage,
        () -> deleteUser(user)
      );
    }else{
      uiController.showConfirmation(
        "Delete User", 
        "Are you sure you want to delete the user " + user.getName() + "?", 
        () -> deleteUser(user)
        );
    }
  }

  private void deleteUser(User user){
    try {
      userDAO.delete(user);
      users.remove(user);
      uiController.showAlert("Success", "User deleted successfully!");
    } catch (Exception e) {
      uiController.showAlert("Error", "Failed to delete user: " + e.getMessage());
    }
  }

  private void handleUpdate(User user) {
    currentUser = user;
    isUpdating = true;
    formNameField.setText(user.getName());
    formEmailField.setText(user.getEmail());
    saveButton.setText("Update User");
  }

  private void configureUserTableView() {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    userTableView.setPlaceholder(new Label("No users found"));

    TableColumn<User, Integer> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

    TableColumn<User, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<User, String> emailCol = new TableColumn<>("Email");
    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

    TableColumn<User, LocalDateTime> registeredCol = new TableColumn<>("Registered At");
    registeredCol.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));
    registeredCol.setCellFactory(column -> new javafx.scene.control.TableCell<User, LocalDateTime>() {
      @Override
      protected void updateItem(LocalDateTime date, boolean empty) {
        super.updateItem(date, empty);
        setText(empty || date == null ? "" : date.format(dateFormatter));
      }
    });

    userTableView.getColumns().addAll(
      Arrays.asList(idCol, nameCol, emailCol, registeredCol));
    userTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private void setupButtonStates(){
    updateButton.setDisable(true);
    deleteButton.setDisable(true);

    userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      boolean hasSelection = newSelection != null;
      updateButton.setDisable(!hasSelection);
      deleteButton.setDisable(!hasSelection);
    });
  }
}