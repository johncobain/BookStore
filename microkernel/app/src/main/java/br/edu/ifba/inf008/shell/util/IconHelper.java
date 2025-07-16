package br.edu.ifba.inf008.shell.util;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconHelper {
    
    private static final String ICONS_PATH = "/icons/";
    
    public static Image loadIcon(String iconName) {
        try {
            InputStream iconStream = IconHelper.class.getResourceAsStream(ICONS_PATH + iconName);
            if (iconStream != null) {
                return new Image(iconStream);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading icon: " + iconName + " - " + e.getMessage());
        }
        return null;
    }
    
    public static ImageView createIconView(String iconName, double size) {
        Image icon = loadIcon(iconName);
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
    
    public static ImageView createIconView(String iconName) {
        return createIconView(iconName, 16.0);
    }
}