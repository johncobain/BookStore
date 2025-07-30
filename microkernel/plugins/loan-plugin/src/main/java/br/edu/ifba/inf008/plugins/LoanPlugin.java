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

public class LoanPlugin implements IPlugin {
  @Override
  public boolean init(){
    try {
      JPAUtil.warmUp();
    } catch (Exception e) {
      System.err.println("Warning: Could not initialize database connection: " + e.getMessage());
    }

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Loans");

      Runnable openLoansInterface = () -> {
        try {
          ClassLoader classLoader = getClass().getClassLoader();
          FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/loan/ui/loan-management.fxml")
          );

          loader.setClassLoader(classLoader);
          Node content = loader.load();            
          uiController.createTab("Loan Management", content);

        } catch (IOException e) {
          System.err.println("Error loading Loans Interface: " + e.getMessage());
          uiController.showAlert("Error", "Failed to open Loans Interface: " + e.getMessage());
        }

      };

      menuItem.setOnAction(e -> openLoansInterface.run());
      ImageView logo = createIconView(
        this.getClass(),
        "/br/edu/ifba/inf008/plugins/loan/ui/icons/logo.png"
      );
      uiController.addPluginCard(
        "loan-plugin",
        logo,
        "Loan Management",
        "Track book loans.",
        openLoansInterface
      );
      return true;

    } catch (Exception e) {
      System.err.println("Error initializing LoanPlugin: " + e.getMessage());
      return false;
    }
  }
}
