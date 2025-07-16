package br.edu.ifba.inf008.plugins.user.ui;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.user.persistence.UserDAO;
import br.edu.ifba.inf008.shell.model.User;
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

public class UserManagementController {
    UserDAO userDAO = new UserDAO();

    @FXML private TextField searchField;
    @FXML private ToggleGroup searchTypeToggleGroup;
    @FXML private TextField formNameField;
    @FXML private TextField formEmailField;
    @FXML private Button saveButton;
    @FXML private ListView<User> userListView;
    
    private IUIController uiController;
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private User currentUser = null;
    private boolean isUpdating = false;
    

    @FXML
    public void initialize() {
        this.uiController = ICore.getInstance().getUIController();

        loadInitialData();
        userListView.setItems(users);

        configureUserCellFactory();
    }

    private void loadInitialData() {
        users.addAll(userDAO.findAll());
    }

    private void configureUserCellFactory() { //TODO: When clicked on cell Show info
        userListView.setCellFactory(lv -> new ListCell<User>() {
            private final HBox hbox = new HBox(10);
            private final Label label = new Label();
            private final Button deleteButton = new Button("🗑️");
            private final Button updateButton = new Button("✏️");
            private final Button infoButton = new Button("ℹ️");
            private final Pane spacer = new Pane();

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(label, spacer, deleteButton, updateButton, infoButton);
                
                deleteButton.getStyleClass().add("button-danger");
                updateButton.getStyleClass().add("button-info");
            }

            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(user.getName() + " (" + user.getEmail() + ")");
                    setGraphic(hbox);

                    deleteButton.setOnAction(event -> handleDelete(getItem()));
                    updateButton.setOnAction(event -> handleUpdate(getItem()));
                    infoButton.setOnAction(event -> handleInfo(getItem()));
                }
            }
        });
    }

    @FXML
    private void handleSave(){
        String name = formNameField.getText();
        String email = formEmailField.getText();
        if(name.isEmpty() || email.isEmpty()) {
            this.uiController.showAlert("Validation Error", "Name and Email cannot be empty.");
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
                        userListView.setItems(users);
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
                        userListView.setItems(users);
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
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            userListView.setItems(users);
        } else{
            ObservableList<User> filteredUsers = users.filtered(
                user -> {
                    if (searchTypeToggleGroup.getSelectedToggle().getUserData().equals("email")) {
                        return user.getEmail().toLowerCase().contains(query);
                    } else {
                        return user.getName().toLowerCase().contains(query);
                    }
                }
            );
            userListView.setItems(filteredUsers);
        }
    }

    @FXML
    private void handleClear(){
        currentUser = null;
        isUpdating = false;
        formNameField.clear();
        formEmailField.clear();
        saveButton.setText("Create User");
    }

    private void handleDelete(User user) {
        uiController.showConfirmation(
            "Delete User", 
            "Are you sure you want to delete the user " + user.getName() + "?", 
            () ->{
                try {
                    userDAO.delete(user);
                    users.remove(user);
                    userListView.setItems(users);
                    uiController.showAlert("Success", "User deleted successfully!");
                } catch (Exception e) {
                    uiController.showAlert("Error", "Failed to delete user: " + e.getMessage());
                }
            }
        );
    }

    private void handleUpdate(User user) {
        currentUser = user;
        isUpdating = true;
        formNameField.setText(user.getName());
        formEmailField.setText(user.getEmail());
        saveButton.setText("Update User");
    }

    private void handleInfo(User user) {
        uiController.showAlert("User info", "Id: " + user.getUserId() + "\nName: " + user.getName() + "\nEmail: " + user.getEmail() +
            "\nRegistered At: " + user.getRegisteredAt());
    }
}
