package br.edu.ifba.inf008.plugins;

import java.io.IOException;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.plugins.report.ui.ReportManagementController;
import br.edu.ifba.inf008.shell.persistence.JPAUtil;
import static br.edu.ifba.inf008.shell.util.IconHelper.createIconView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public class ReportPlugin implements IPlugin {
  @Override
  public boolean init(){
    try {
      JPAUtil.warmUp();
    } catch (Exception e) {
      System.err.println("Warning: Could not initialize database connection: " + e.getMessage());
    }

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Reports");

      Runnable openReportsInterface = () -> {
        try {
          ClassLoader classLoader = getClass().getClassLoader();
          FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/report/ui/report-management.fxml")
          );

          loader.setClassLoader(classLoader);
          Node content = loader.load();
          
          ReportManagementController controller = loader.getController();
          uiController.createRefreshableTab("Report Management", content, controller);

        } catch (IOException e) {
          System.err.println("Error loading Report Management Interface: " + e.getMessage());
          uiController.showAlert("Error", "Failed to open Report Management Interface: " + e.getMessage());
        }
      };

      menuItem.setOnAction(e -> openReportsInterface.run());
      ImageView logo = createIconView(
        this.getClass(),
        "/br/edu/ifba/inf008/plugins/report/ui/icons/logo.png"
      );
      uiController.addPluginCard(
        "report-plugin",
        logo,
        "Reports & Analytics",
        "Generate detailed reports.",
        openReportsInterface
      );
      return true;

    } catch (Exception e) {
      System.err.println("Error initializing ReportPlugin: " + e.getMessage());
      return false;
    }
  }
}
