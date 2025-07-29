package br.edu.ifba.inf008.shell;

import java.util.HashMap;
import java.util.Map;

import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.shell.util.IconHelper;
import static br.edu.ifba.inf008.shell.util.IconHelper.createIconView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UIController extends Application implements IUIController{
    private MenuBar menuBar;
    private TabPane tabPane;
    private static UIController uiController;

    private FlowPane cardsContainer;
    private VBox welcomeContent;

    private final Map<String, VBox> pluginCards = new HashMap<>();

    public UIController() {
    }

    @Override
    public void init() {
        uiController = this;
    }

    public static UIController getInstance() {
        return uiController;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BookStore Blackbird");

        Image appIcon = IconHelper.loadIcon("/icons/bookStore-icon.png");
        if (appIcon != null) {
            primaryStage.getIcons().add(appIcon);
        } else {
            System.out.println("⚠️ Application icon not found, using default");
        }

        menuBar = new MenuBar();
        VBox vBox = new VBox(menuBar);
        vBox.getStyleClass().add("main-container");


        tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);

        VBox.setVgrow(tabPane, Priority.ALWAYS);

        createWelcomeTab();

        vBox.getChildren().addAll(tabPane);

        Scene scene = new Scene(vBox, 1280, 720);

        try{
            String cssPath = getClass().getResource("/css/main-style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar CSS: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        Core.getInstance().getPluginController().init();
    }

    private void createWelcomeTab(){
        welcomeContent = new VBox(10);
        welcomeContent.getStyleClass().add("welcome-content");
        welcomeContent.setAlignment(Pos.TOP_CENTER);
        welcomeContent.setPadding(new Insets(15, 15, 15, 15));
        welcomeContent.setFillWidth(true);

        VBox headerSection = createHeaderSection();
        VBox cardsSection = createCardsSection();
        VBox footerSection = createFooterSection();

        welcomeContent.getChildren().addAll(headerSection, cardsSection, footerSection);

        Tab welcomeTab = new Tab("Home");
        welcomeTab.setContent(welcomeContent);
        welcomeTab.setClosable(false);
        tabPane.getTabs().add(welcomeTab);
    }

    private VBox createHeaderSection(){
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);

        Label title = new Label("📚 Welcome to BookStore Blackbird!");
        title.getStyleClass().add("welcome-title");

        Label subtitle = new Label("Your one-stop solution for managing books and users.");
        subtitle.getStyleClass().add("welcome-subtitle");

        ImageView logoIcon = IconHelper.createIconView("/icons/bookStore-icon-removebg.png", 64);

        Label description = new Label("Select a module below to start managing your bookstore");
        description.getStyleClass().add("welcome-description");

        if(logoIcon.getImage() != null) {
            logoIcon.getStyleClass().add("welcome-header-icon");
            header.getChildren().addAll(title, subtitle, logoIcon, description);
        }else{
            header.getChildren().addAll(title, subtitle, description);
        }

        return header;
    }
    private VBox createCardsSection(){
        VBox section = new VBox(20);
        section.setAlignment(Pos.CENTER);
        section.setMaxHeight(Double.MAX_VALUE);

        Label sectionTitle = new Label("📦 Available Modules");
        sectionTitle.getStyleClass().add("welcome-section-title");

        cardsContainer = new FlowPane();
        cardsContainer.setHgap(20);
        cardsContainer.setVgap(20);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(20));
        cardsContainer.getStyleClass().add("welcome-cards");
        cardsContainer.setMaxWidth(Double.MAX_VALUE);

        cardsContainer.setPrefWrapLength(1000);

        addDefaultCard();

        section.getChildren().addAll(sectionTitle, cardsContainer);
        return section;
    }
    private VBox createFooterSection(){
        VBox footer = new VBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));

        Hyperlink footerText = new Hyperlink("Built with ❤️ by Andrey Gomes");
        footerText.setOnAction(e -> {
            getHostServices().showDocument("https://github.com/johncobain");
        });
        footerText.getStyleClass().add("welcome-footer-text");

        footer.getChildren().add(footerText);
        return footer;
    }

    private void addDefaultCard() {
        ImageView logo = createIconView("/icons/system.png");
        VBox defaultCard = createWelcomeCard(
            logo,
            "System Initializing",
            "Loading plugins...",
            null
        );
        defaultCard.setId("default-card");
        cardsContainer.getChildren().add(defaultCard);
    }
    
    @Override
    public void addPluginCard(String pluginName, ImageView icon, String title, String description, Runnable action){
        cardsContainer.getChildren().removeIf(node -> "default-card".equals(node.getId()));

        VBox card = createWelcomeCard(icon, title, description, action);
        card.setId("plugin-card-" + pluginName);

        cardsContainer.getChildren().add(card);
        pluginCards.put(pluginName, card);

        System.out.println("✅ Plugin card added successfully: " + pluginName);
    }

    @Override
    public void removePluginCard(String pluginName){
        VBox card = pluginCards.remove(pluginName);
        if (card != null) {
            cardsContainer.getChildren().remove(card);
        }

        if(cardsContainer.getChildren().isEmpty()){
            addDefaultCard();
        }
    }   

    private VBox createWelcomeCard(ImageView icon, String title, String description, Runnable action){
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);
        card.setPrefHeight(200);
        card.getStyleClass().add("welcome-card");

        Label iconLabel = new Label();
        iconLabel.setGraphic(icon);
        iconLabel.getStyleClass().add("welcome-card-icon");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("welcome-card-title");
        titleLabel.setWrapText(true);

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("welcome-card-description");
        descLabel.setWrapText(true);

        card.setOnMouseEntered(e ->{
            card.getStyleClass().add("welcome-card-hover");
        });

        card.setOnMouseExited(e ->{
            card.getStyleClass().remove("welcome-card-hover");
        });

        if(action != null){
            card.setOnMouseClicked(e -> action.run());
        }

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);
        return card;
    }    

    @Override
    public MenuItem createMenuItem(String menuText, String menuItemText) {
        Menu newMenu = null;
        for (Menu menu : menuBar.getMenus()) {
            if (menu.getText().equals(menuText)) {
                newMenu = menu;
                break;
            }
        }
        if (newMenu == null) {
            newMenu = new Menu(menuText);
            menuBar.getMenus().add(newMenu);
        }

        MenuItem menuItem = new MenuItem(menuItemText);
        newMenu.getItems().add(menuItem);

        return menuItem;
    }

    @Override
    public boolean createTab(String tabText, Node contents) {
        Tab tab = new Tab();
        tab.setText(tabText);
        tab.setContent(contents);
        tabPane.getTabs().add(tab);

        tabPane.getSelectionModel().select(tab);

        return true;
    }

    @Override
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void showConfirmation(String title, String message, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                onConfirm.run();
            }
        });
    }
}
