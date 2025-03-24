package model;

/**
 * Manages user session data with static methods.
 * For interface implementation, use SessionManagerImpl.
 */
public class SessionManager {
    
    // Session data
    private static String authToken = null;
    private static String username = null;
    private static String email = null;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SessionManager() {
        // Private constructor
    }
    
    /**
     * Gets the authentication token.
     */
    public static String getAuthToken() {
        return authToken;
    }
    
    /**
     * Gets the username.
     */
    public static String getUsername() {
        return username;
    }
    
    /**
     * Gets the email.
     */
    public static String getEmail() {
        return email;
    }
    
    /**
     * Sets the authentication token.
     */
    public static void setAuthToken(String token) {
        authToken = token;
    }
    
    /**
     * Sets the username.
     */
    public static void setUsername(String name) {
        username = name;
    }
    
    /**
     * Sets the email.
     */
    public static void setEmail(String userEmail) {
        email = userEmail;
    }
    
    /**
     * Logs out the user by clearing all session data.
     */
    public static void logout() {
        authToken = null;
        username = null;
        email = null;
    }
}
