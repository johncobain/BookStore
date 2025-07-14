package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class BookPlugin implements IPlugin {
  @Override
  public boolean init(){
    System.out.println("🔌 BookPlugin...");

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Books");

      Runnable openBooksInterface = () -> {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
          new Label("Book Plugin Working!"),
          new Label("This plugin was loaded by the microkernel!"),
          new Label("Date/Time: " + java.time.LocalDateTime.now())
        );

        uiController.createTab("📚 Book Management", content);

        System.out.println("Book Plugin executed - new tab created!");
      };

      menuItem.setOnAction(e -> openBooksInterface.run());

       uiController.addPluginCard(
        "book-plugin",
        "📚",
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
