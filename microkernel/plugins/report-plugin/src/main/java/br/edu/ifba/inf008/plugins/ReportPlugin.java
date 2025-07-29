package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import static br.edu.ifba.inf008.shell.util.IconHelper.createIconView;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ReportPlugin implements IPlugin {
  @Override
  public boolean init(){
    System.out.println("🔌 ReportPlugin...");

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Reports");

      Runnable openReportsInterface = () -> {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
          new Label("Report Plugin Working!"),
          new Label("This plugin was loaded by the microkernel!"),
          new Label("Date/Time: " + java.time.LocalDateTime.now())
        );

        uiController.createTab("Reports & Analytics", content);

        System.out.println("Report Plugin executed - new tab created!");
      };

      menuItem.setOnAction(e -> openReportsInterface.run());
      ImageView logo = createIconView(
        this.getClass(),
        "/br/edu/ifba/inf008/plugins/report/ui/icons/logo.png"
      );
      System.out.println("Logo loaded: " + (logo.getImage() != null));
      uiController.addPluginCard(
        "report-plugin",
        logo,
        "Reports & Analytics",
        "Generate detailed reports.",
        openReportsInterface
      );
      System.out.println("✅ ReportPlugin initialized successfuly!");
      return true;

    } catch (Exception e) {
      System.err.println("❌ Error initializing ReportPlugin: " + e.getMessage());
      return false;
    }
  }
}
