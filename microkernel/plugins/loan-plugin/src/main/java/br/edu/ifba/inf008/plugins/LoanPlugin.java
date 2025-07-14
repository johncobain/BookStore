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
    System.out.println("🔌 LoanPlugin...");

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Loans");

      Runnable openLoansInterface = () -> {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
          new Label("Loan Plugin Working!"),
          new Label("This plugin was loaded by the microkernel!"),
          new Label("Date/Time: " + java.time.LocalDateTime.now())
      );

      uiController.createTab("📋 Loan Management", content);

      System.out.println("Loan Plugin executed - new tab created!");
      };

      menuItem.setOnAction(e -> openLoansInterface.run());

      uiController.addPluginCard(
        "loan-plugin",
        "📋",
        "Loan Management",
        "Track book loans.",
        openLoansInterface
      );

      System.out.println("✅ LoanPlugin initialized successfully!");
      return true;

    } catch (Exception e) {
      System.err.println("❌ Error initializing LoanPlugin: " + e.getMessage());
      return false;
    }
  }
}
