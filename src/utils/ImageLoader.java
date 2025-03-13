package utils;

import javax.swing.*;
import java.awt.*;

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
        return new ImageIcon(path);
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
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() <= 0) {
            System.out.println("Error: Image not found -> " + path);
            return null; // Prevents crashes if image is missing
        }
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon(img);
    }
}
