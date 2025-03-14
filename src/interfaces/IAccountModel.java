package interfaces;

import org.json.JSONObject;

/**
 * Interface defining the operations for the account model.
 */
public interface IAccountModel {
    /**
     * Fetches the best score from the server.
     * @return JSONObject containing the response from the server.
     */
    JSONObject fetchBestScore();
    
    /**
     * Gets the username.
     * @return The username.
     */
    String getUsername();
    
    /**
     * Gets the email.
     * @return The email.
     */
    String getEmail();
    
    /**
     * Gets the best score.
     * @return The best score.
     */
    int getBestScore();
    
    /**
     * Updates the username.
     * @param newUsername The new username to set.
     * @return JSONObject containing the response from the server.
     * @throws Exception If an error occurs during the update.
     */
    JSONObject updateUsername(String newUsername) throws Exception;
    
    /**
     * Updates the password.
     * @param oldPassword The current password.
     * @param newPassword The new password.
     * @return JSONObject containing the response from the server.
     * @throws Exception If an error occurs during the update.
     */
    JSONObject updatePassword(String oldPassword, String newPassword) throws Exception;
    
    /**
     * Logs out the user.
     */
    void logout();
} 