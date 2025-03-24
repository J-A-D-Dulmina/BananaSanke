package utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Utility class for loading and resizing images.
 */
public class ImageLoader {

    /**
     * Loads an image from the given path.
     * @param path File path of the image.
     * @return ImageIcon containing the loaded image.
     */
    public static ImageIcon loadImage(String path) {
        ImageIcon icon = null;
        
        // Debug output
        System.out.println("Attempting to load image: " + path);
        
        // Method 1: Using ResourceLoader
        URL imageUrl = ResourceLoader.getResource(path);
        if (imageUrl != null) {
            System.out.println("Successfully found resource via ResourceLoader: " + imageUrl);
            icon = new ImageIcon(imageUrl);
            if (icon.getIconWidth() > 0) {
                return icon;
            }
        } else {
            System.out.println("ResourceLoader could not find: " + path);
        }
        
        // Method 2: Direct file access
        try {
            File file = new File(path);
            if (file.exists() && file.canRead()) {
                System.out.println("Loading via direct file access: " + file.getAbsolutePath());
                icon = new ImageIcon(file.getAbsolutePath());
                if (icon.getIconWidth() > 0) {
                    return icon;
                }
            } else {
                System.out.println("Direct file access failed for: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Error in direct file access: " + e.getMessage());
        }
        
        // Method 3: Class resource
        try {
            URL url = ImageLoader.class.getResource("/" + path);
            if (url != null) {
                System.out.println("Found as class resource: " + url);
                icon = new ImageIcon(url);
                if (icon.getIconWidth() > 0) {
                    return icon;
                }
            } else {
                System.out.println("Not found as class resource: /" + path);
            }
        } catch (Exception e) {
            System.out.println("Error in class resource loading: " + e.getMessage());
        }
        
        // Method 4: Absolute path from project root
        try {
            String projectRoot = System.getProperty("user.dir");
            File file = new File(projectRoot, path);
            if (file.exists() && file.canRead()) {
                System.out.println("Loading from project root: " + file.getAbsolutePath());
                icon = new ImageIcon(file.getAbsolutePath());
                if (icon.getIconWidth() > 0) {
                    return icon;
                }
            } else {
                System.out.println("Project root path failed: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Error in project root path: " + e.getMessage());
        }
        
        System.out.println("Error: Image not found -> " + path);
        return null;
    }

    /**
     * Loads and resizes an image while maintaining aspect ratio.
     * Supports GIFs.
     * @param path File path of the image.
     * @param width Desired width of the image.
     * @param height Desired height of the image.
     * @return Resized ImageIcon or null if the image is not found.
     */
    public static ImageIcon loadImage(String path, int width, int height) {
        ImageIcon icon = loadImage(path);
        if (icon != null && icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}
