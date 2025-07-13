package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class LoanPlugin implements IPlugin {
  @Override
  public boolean init(){
    System.out.println(">>>>>>>>> Loan Plugin has been loaded <<<<<<<<<");

    IUIController uiControler = ICore.getInstance().getUIController();

    MenuItem menuItem = uiControler.createMenuItem("Loan", "Execute Loan Plugin");

    menuItem.setOnAction(event -> {
      VBox content = new VBox(10);
      content.setPadding(new Insets(20));
      content.getChildren().addAll(
        new Label("Loan Plugin Working!"),
        new Label("This plugin was loaded by the microkernel!"),
        new Label("Date/Time: " + java.time.LocalDateTime.now())
      );

      uiControler.createTab("Loan", content);

      System.out.println("Loan Plugin executed - new tab created!");
    });
    return true;
  }
}
