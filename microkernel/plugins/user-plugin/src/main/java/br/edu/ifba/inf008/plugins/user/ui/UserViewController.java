package br.edu.ifba.inf008.plugins.user.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UserViewController implements Initializable {

    @FXML
    private TextField nameField;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🎮 UserViewController initialized");
        statusLabel.setText("Interface ready");
    }
    
    @FXML
    private void handleSaveUser(ActionEvent event) {
        String name = nameField.getText();
        if (name != null && !name.trim().isEmpty()) {
            statusLabel.setText("User saved: " + name);
            nameField.clear();
            System.out.println("👤 User saved: " + name);
        } else {
            statusLabel.setText("Please enter a name");
        }
    }
}
