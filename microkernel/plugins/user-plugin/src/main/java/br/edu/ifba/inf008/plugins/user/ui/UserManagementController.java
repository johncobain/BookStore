package br.edu.ifba.inf008.plugins.user.ui;

import java.sql.Date;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
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

public class UserManagementController {
    public record User(Integer id,String name, String email, Date registeredAt){}; //TODO: TEMP

    @FXML private TextField searchField;
    @FXML private ToggleGroup searchTypeToggleGroup;
    @FXML private CheckBox includeInactiveCheckBox;
    @FXML private TextField formNameField;
    @FXML private TextField formEmailField;
    @FXML private Button saveButton;
    @FXML private ListView<User> userListView;
    
    private IUIController uiController;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private User currentUser = null;
    private boolean isUpdating = false;
    

    @FXML
    public void initialize() {
        this.uiController = ICore.getInstance().getUIController();

        loadInitialData(); //TODO: TEMP
        userListView.setItems(users);

        configureUserCellFactory();
    }

    private void loadInitialData() {
        users.addAll(
            new User(1, "John Doe", "john.doe@example.com", new Date(System.currentTimeMillis())),
            new User(2, "Jane Smith", "jane.smith@example.com", new Date(System.currentTimeMillis())),
            new User(3, "Alice Johnson", "alice.johnson@example.com", new Date(System.currentTimeMillis())),
            new User(4, "Bob Brown", "bob.brown@example.com", new Date(System.currentTimeMillis()))
        );
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
                    label.setText(user.name() + " (" + user.email() + ")");
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
            User updatedUser = new User(currentUser.id(), name, email, currentUser.registeredAt());
            int userIndex = users.indexOf(currentUser);
            if (userIndex == -1) {
                this.uiController.showAlert("Error", "User not found for update.");
                return;
            }
            uiController.showConfirmation( //TODO: replace with actual DAO
                "Update User", 
                "Are you sure you want to update the user " + currentUser.name() + "?", 
                () -> {
                    users.set(userIndex, updatedUser);
                    userListView.setItems(users);
                    uiController.showAlert("Success", "User updated successfully!");
                }
            );
        } else {
            User newUser = new User(1, name, email, new Date(System.currentTimeMillis()));
            uiController.showConfirmation(
                "Create User", 
                "Are you sure you want to create the user " + newUser.name() + "?", 
                () -> {
                    users.add(newUser);
                    userListView.setItems(users);
                    uiController.showAlert("Success", "User created successfully!");
                }
            );
        }
        handleClear();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();
        boolean includeInactive = includeInactiveCheckBox.isSelected();

        if (query.isEmpty()) {
            userListView.setItems(users);
        } else{
            ObservableList<User> filteredUsers = users.filtered(
                user -> {
                    if (searchTypeToggleGroup.getSelectedToggle().getUserData().equals("email")) {
                        return user.email().toLowerCase().contains(query);
                    } else {
                        return user.name().toLowerCase().contains(query);
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
            "Are you sure you want to delete the user " + user.name() + "?", 
            () ->{
                users.remove(user);
                userListView.setItems(users);
            }
        );
    }

    private void handleUpdate(User user) {
        currentUser = user;
        System.out.println("User Id: " + user.id());
        isUpdating = true;
        formNameField.setText(user.name());
        formEmailField.setText(user.email());
        saveButton.setText("Update User");
    }

    private void handleInfo(User user) {
        uiController.showAlert("User info", "Id: " + user.id() + "\nName: " + user.name() + "\nEmail: " + user.email() +
            "\nRegistered At: " + user.registeredAt());
    }
}
