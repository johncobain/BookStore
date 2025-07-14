package br.edu.ifba.inf008.plugins;

import java.io.IOException;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public class UserPlugin implements IPlugin {
  @Override
  public boolean init(){
    System.out.println("🔌 UserPlugin...");

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Users");

      Runnable openUsersInterface = () -> {
        System.out.println("🎯 Oppening Users Interface...");
        try {
          ClassLoader classLoader = getClass().getClassLoader();
          FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/user/ui/user-management.fxml")
          );

          loader.setClassLoader(classLoader);
          Node content = loader.load();
          uiController.createTab("👥 User Management", content);

          System.out.println("✅ Interface loaded successfully!");
        } catch(IOException e) {
          System.err.println("❌ Error opening Users Interface: " + e.getMessage());
          e.printStackTrace();
          uiController.showAlert("Error", "Failed to open Users Interface: " + e.getMessage());
        }
      };

      menuItem.setOnAction(e -> openUsersInterface.run());

      uiController.addPluginCard(
        "user-plugin", 
        "👥", 
        "User Management",
        "Manage library users.",
        openUsersInterface
      );

      System.out.println("✅ UserPlugin initialized successfully!");
      return true;

    } catch (Exception e) {
      System.err.println("❌ Error initializing UserPlugin: " + e.getMessage());
      return false;
    }
  }
}