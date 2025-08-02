package br.edu.ifba.inf008.plugins;

import java.io.IOException;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.book.ui.BookManagementController;
import static br.edu.ifba.inf008.shell.util.IconHelper.createIconView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public class BookPlugin implements IPlugin {
  @Override
  public boolean init(){
    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Books");

      Runnable openBooksInterface = () -> {
        try {
          ClassLoader classLoader = getClass().getClassLoader();
          FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/book/ui/book-management.fxml")
          );
          
          loader.setClassLoader(classLoader);
          Node content = loader.load();

          BookManagementController controller = loader.getController();
          uiController.createRefreshableTab("Book Management", content, controller);

        } catch (IOException e) {
          System.err.println("Error opening Books interface: " + e.getMessage());
          uiController.showAlert("Error", "Failed to open Books Interface: " + e.getMessage());
        }

      };

      menuItem.setOnAction(e -> openBooksInterface.run());
      ImageView logo = createIconView(
        this.getClass(),
        "/br/edu/ifba/inf008/plugins/book/ui/icons/logo.png"
      );
      uiController.addPluginCard(
        "book-plugin",
        logo,
        "Book Management",
        "Manage your books.",
        openBooksInterface
      );
      return true;

    } catch (Exception e) {
      System.err.println("Error initializing BookPlugin: " + e.getMessage());
      return false;
    }
  }
}
