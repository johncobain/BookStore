package br.edu.ifba.inf008.plugins;

import java.io.IOException;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.shell.persistence.JPAUtil;
import static br.edu.ifba.inf008.shell.util.IconHelper.createIconView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public class BookPlugin implements IPlugin {
  @Override
  public boolean init(){
    System.out.println("🔌 BookPlugin...");
    try {
      System.out.println("🔗 Initializing database connection...");
      JPAUtil.warmUp();
      System.out.println("✅ Database connection initialized successfully!");
    } catch (Exception e) {
      System.err.println("⚠️  Warning: Could not initialize database connection: " + e.getMessage());
    }

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Books");

      Runnable openBooksInterface = () -> {
        System.out.println("🎯 Oppening Books Interface...");
        try {
          ClassLoader classLoader = getClass().getClassLoader();
          FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/book/ui/book-management.fxml")
          );
          
          loader.setClassLoader(classLoader);
          Node content = loader.load();
          uiController.createTab("Book Management", content);

          System.out.println("✅ Interface loaded successfully!");
        } catch (IOException e) {
          System.err.println("❌ Error opening Books interface: " + e.getMessage());
          uiController.showAlert("Error", "Failed to open Books Interface: " + e.getMessage());
        }

      };

      menuItem.setOnAction(e -> openBooksInterface.run());
      ImageView logo = createIconView(
        this.getClass(),
        "/br/edu/ifba/inf008/plugins/book/ui/icons/logo.png"
      );
      System.out.println("Logo loaded: " + (logo.getImage() != null));
      uiController.addPluginCard(
        "book-plugin",
        logo,
        "Book Management",
        "Manage your books.",
        openBooksInterface
      );


      System.out.println("✅ BookPlugin initialized successfuly!");
      return true;

    } catch (Exception e) {
      System.err.println("❌ Error initializing BookPlugin: " + e.getMessage());
      return false;
    }
  }
}
