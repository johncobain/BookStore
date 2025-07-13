package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class UserPlugin implements IPlugin {
  @Override
  public boolean init(){
    System.out.println(">>>>>>>>> User Plugin has been loaded <<<<<<<<<");

    IUIController uiControler = ICore.getInstance().getUIController();

    MenuItem menuItem = uiControler.createMenuItem("User", "Execute User Plugin");

    menuItem.setOnAction(event -> {
      VBox content = new VBox(10);
      content.setPadding(new Insets(20));
      content.getChildren().addAll(
        new Label("User Plugin Working!"),
        new Label("This plugin was loaded by the microkernel!"),
        new Label("Date/Time: " + java.time.LocalDateTime.now())
      );

      uiControler.createTab("User", content);

      System.out.println("User Plugin executed - new tab created!");
    });
    return true;
  }
}
