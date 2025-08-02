package br.edu.ifba.inf008.interfaces;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public interface IUIController
{
    public abstract MenuItem createMenuItem(String menuText, String menuItemText);
    public abstract boolean createTab(String tabText, Node contents);
    public abstract boolean createRefreshableTab(String tabText, Node contents, IRefreshable controller);
    public abstract void showAlert(String title, String message);
    public abstract void showConfirmation(String title, String message, Runnable onConfirm);

    public abstract void addPluginCard(String pluginName, ImageView icon, String title, String description, Runnable action);
    public abstract void removePluginCard(String pluginName);
}
