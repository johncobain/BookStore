package br.edu.ifba.inf008.interfaces;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public interface IUIController
{
    public abstract MenuItem createMenuItem(String menuText, String menuItemText);
    public abstract boolean createTab(String tabText, Node contents);
    public abstract void showAlert(String title, String message);
    public abstract void showConfirmation(String title, String message, Runnable onConfirm);

    void addPluginCard(String pluginName, String icon, String title, String description, Runnable action);
    void removePluginCard(String pluginName);
}
