package interfaces;

import org.json.JSONObject;

/**
 * Interface for user-related operations.
 */
public interface IUserService {
    /**
     * Updates the current user's username.
     * @param newUsername The new username to set
     * @return JSON response from the server
     * @throws Exception If an error occurs during the update
     */
    JSONObject updateUsername(String newUsername) throws Exception;
    
    /**
     * Updates the current user's password.
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return JSON response from the server
     * @throws Exception If an error occurs during the update
     */
    JSONObject updatePassword(String oldPassword, String newPassword) throws Exception;
    
    /**
     * Fetches the current user's best score.
     * @return JSON response containing the best score
     */
    JSONObject fetchBestScore();
    
    /**
     * Logs out the current user.
     */
    void logout();
    
    /**
     * Gets the current username.
     * @return The username
     */
    String getUsername();
    
    /**
     * Gets the current user's email.
     * @return The email
     */
    String getEmail();
} 