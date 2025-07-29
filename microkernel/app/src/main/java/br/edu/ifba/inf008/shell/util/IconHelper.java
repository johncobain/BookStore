package br.edu.ifba.inf008.shell.util;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconHelper {
    
    public static Image loadIcon(String iconPath) {
        return loadIcon(IconHelper.class, iconPath);
    }
    
    public static Image loadIcon(Class<?> contextClass, String iconPath) {
        try {
            InputStream iconStream = contextClass.getResourceAsStream(iconPath);
            if (iconStream != null) {
                return new Image(iconStream);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading icon: " + iconPath + " - " + e.getMessage());
        }
        return null;
    }

    public static ImageView createIconView(String iconPath, double size) {
        Image icon = loadIcon(iconPath);
        if (icon != null) {
            ImageView iconView = new ImageView(icon);
            iconView.setFitHeight(size);
            iconView.setFitWidth(size);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            return iconView;
        }
        return new ImageView();
    }

    public static ImageView createIconView(Class<?> contextClass, String iconPath, double size) {
        Image icon = loadIcon(contextClass, iconPath);
        if (icon != null) {
            ImageView iconView = new ImageView(icon);
            iconView.setFitHeight(size);
            iconView.setFitWidth(size);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            return iconView;
        }
        return new ImageView();
    }

    public static ImageView createIconView(String iconPath) {
        return createIconView(iconPath, 64.0);
    }

    public static ImageView createIconView(Class<?> contextClass, String iconPath) {
        return createIconView(contextClass, iconPath, 64.0);
    }
}