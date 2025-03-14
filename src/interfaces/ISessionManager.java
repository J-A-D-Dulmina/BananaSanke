package interfaces;

/**
 * Interface defining the operations for the session manager.
 */
public interface ISessionManager {
    /**
     * Sets the authentication token.
     * @param token The authentication token to set.
     */
    void setAuthToken(String token);
    
    /**
     * Gets the current authentication token.
     * @return The current authentication token.
     */
    String getAuthToken();
    
    /**
     * Sets the username.
     * @param name The username to set.
     */
    void setUsername(String name);
    
    /**
     * Gets the current username.
     * @return The current username.
     */
    String getUsername();
    
    /**
     * Gets the user's email.
     * @return The user's email.
     */
    String getEmail();
    
    /**
     * Sets the user's email.
     * @param userEmail The email to set.
     */
    void setEmail(String userEmail);
    
    /**
     * Logs out the user by clearing all session data.
     */
    void logout();
} 