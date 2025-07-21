package br.edu.ifba.inf008.plugins;

import java.io.IOException;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.shell.persistence.JPAUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public class LoanPlugin implements IPlugin {
  @Override
  public boolean init(){
    System.out.println("🔌 LoanPlugin...");
    try {
      System.out.println("🔗 Initializing database connection...");
      JPAUtil.warmUp();
      System.out.println("✅ Database connection initialized successfully!");
    } catch (Exception e) {
      System.err.println("⚠️  Warning: Could not initialize database connection: " + e.getMessage());
    }

    try{
      IUIController uiController = ICore.getInstance().getUIController();

      MenuItem menuItem = uiController.createMenuItem("Management", "Loans");

      Runnable openLoansInterface = () -> {
        System.out.println("🎯 Opening Loans Interface...");
        try {
          ClassLoader classLoader = getClass().getClassLoader();
          FXMLLoader loader = new FXMLLoader(
            classLoader.getResource("br/edu/ifba/inf008/plugins/loan/ui/loan-management.fxml")
          );

          loader.setClassLoader(classLoader);
          Node content = loader.load();            
          uiController.createTab("📋 Loan Management", content);
    
          System.out.println("✅ Interface loaded successfully!");
        } catch (IOException e) {
          System.err.println("❌ Error loading Loans Interface: " + e.getMessage());
          uiController.showAlert("Error", "Failed to open Loans Interface: " + e.getMessage());
        }

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
