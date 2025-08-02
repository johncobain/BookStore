package br.edu.ifba.inf008.plugins;

import java.io.IOException;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.user.ui.UserManagementController;
import br.edu.ifba.inf008.shell.persistence.JPAUtil;
import static br.edu.ifba.inf008.shell.util.IconHelper.createIconView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public class UserPlugin implements IPlugin {
  @Override
  public boolean init(){
    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Users");

      Runnable openUsersInterface = () -> {
        try {
          ClassLoader classLoader = getClass().getClassLoader();
          FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/user/ui/user-management.fxml")
          );

          loader.setClassLoader(classLoader);
          Node content = loader.load();
          
          UserManagementController controller = loader.getController();
          uiController.createRefreshableTab("User Management", content, controller);

        } catch(IOException e) {
          System.err.println("Error opening Users Interface: " + e.getMessage());
          uiController.showAlert("Error", "Failed to open Users Interface: " + e.getMessage());
        }
      };

      menuItem.setOnAction(e -> openUsersInterface.run());
      ImageView logo = createIconView(
        this.getClass(),
        "/br/edu/ifba/inf008/plugins/user/ui/icons/logo.png"
      );
      uiController.addPluginCard(
        "user-plugin",
        logo,
        "User Management",
        "Manage library users.",
        openUsersInterface
      );
      return true;

    } catch (Exception e) {
      System.err.println("Error initializing UserPlugin: " + e.getMessage());
      return false;
    }
  }
}