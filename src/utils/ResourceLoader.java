package utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for loading resources in both development and packaged environments.
 * This solves the common problem of resources not being found when running as an executable.
 */
public class ResourceLoader {
    
    /**
     * Gets a resource as an InputStream, working in both development and packaged environments.
     * @param path The resource path (e.g., "resources/heart_icon.gif")
     * @return An InputStream for the resource, or null if not found
     */
    public static InputStream getResourceAsStream(String path) {
        // Try multiple methods to find the resource
        InputStream stream = null;
        
        // Method 1: Direct class loader resource (works in JAR/EXE)
        stream = ResourceLoader.class.getClassLoader().getResourceAsStream(path);
        
        // Method 2: Try without "resources/" prefix if it starts with that
        if (stream == null && path.startsWith("resources/")) {
            stream = ResourceLoader.class.getClassLoader().getResourceAsStream(path.substring(10));
        }
        
        // Method 3: Try as absolute file path (works in development)
        if (stream == null) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    return Files.newInputStream(file.toPath());
                }
            } catch (Exception e) {
                // Silently try next method
            }
        }
        
        // Method 4: Try relative to current directory
        if (stream == null) {
            try {
                Path currentDir = Paths.get("").toAbsolutePath();
                Path resourcePath = currentDir.resolve(path);
                if (Files.exists(resourcePath)) {
                    return Files.newInputStream(resourcePath);
                }
            } catch (Exception e) {
                // Silently try next method
            }
        }
        
        // Method 5: Try in parent directory (common in EXE scenarios)
        if (stream == null) {
            try {
                Path currentDir = Paths.get("").toAbsolutePath();
                Path parentDir = currentDir.getParent();
                if (parentDir != null) {
                    Path resourcePath = parentDir.resolve(path);
                    if (Files.exists(resourcePath)) {
                        return Files.newInputStream(resourcePath);
                    }
                }
            } catch (Exception e) {
                // Last attempt failed
            }
        }
        
        // If we get here and stream is still null, log it
        if (stream == null) {
            System.err.println("WARNING: Could not find resource: " + path);
        }
        
        return stream;
    }
    
    /**
     * Gets a resource as a URL, working in both development and packaged environments.
     * @param path The resource path (e.g., "resources/heart_icon.gif")
     * @return A URL for the resource, or null if not found
     */
    public static URL getResource(String path) {
        // Try multiple methods to find the resource
        URL url = null;
        
        // Method 1: Direct class loader resource (works in JAR/EXE)
        url = ResourceLoader.class.getClassLoader().getResource(path);
        
        // Method 2: Try without "resources/" prefix if it starts with that
        if (url == null && path.startsWith("resources/")) {
            url = ResourceLoader.class.getClassLoader().getResource(path.substring(10));
        }
        
        // Method 3: Try as absolute file path (works in development)
        if (url == null) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    return file.toURI().toURL();
                }
            } catch (Exception e) {
                // Silently try next method
            }
        }
        
        // Method 4: Try relative to current directory
        if (url == null) {
            try {
                Path currentDir = Paths.get("").toAbsolutePath();
                Path resourcePath = currentDir.resolve(path);
                if (Files.exists(resourcePath)) {
                    return resourcePath.toUri().toURL();
                }
            } catch (Exception e) {
                // Silently try next method
            }
        }
        
        // Method 5: Try in parent directory (common in EXE scenarios)
        if (url == null) {
            try {
                Path currentDir = Paths.get("").toAbsolutePath();
                Path parentDir = currentDir.getParent();
                if (parentDir != null) {
                    Path resourcePath = parentDir.resolve(path);
                    if (Files.exists(resourcePath)) {
                        return resourcePath.toUri().toURL();
                    }
                }
            } catch (Exception e) {
                // Last attempt failed
            }
        }
        
        // If we get here and url is still null, log it
        if (url == null) {
            System.err.println("WARNING: Could not find resource: " + path);
        }
        
        return url;
    }
    
    /**
     * Utility method to get a File object for a resource.
     * @param path The resource path
     * @return A File object, or null if the resource can't be found as a file
     */
    public static File getResourceAsFile(String path) {
        try {
            URL url = getResource(path);
            if (url != null) {
                if (url.getProtocol().equals("file")) {
                    return new File(url.toURI());
                }
            }
            
            // Try direct file access
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        } catch (Exception e) {
            System.err.println("Error getting resource as file: " + path);
            e.printStackTrace();
        }
        
        return null;
    }
} 