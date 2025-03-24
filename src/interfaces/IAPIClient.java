package interfaces;

/**
 * Interface for API client operations.
 * Defines methods for communicating with the backend API.
 */
public interface IAPIClient {
    /**
     * Sends a GET request with authentication.
     */
    String sendAuthenticatedGetRequest(String apiUrl);

    /**
     * Sends a POST request with authentication.
     */
    String sendAuthenticatedPostRequest(String apiUrl, String postData);

    /**
     * Registers a new user.
     */
    String registerUser(String username, String email, String password);

    /**
     * Logs in a user.
     */
    String loginUser(String email, String password);

    /**
     * Logs out the current user.
     */
    String logoutUser();

    /**
     * Resets a user's password.
     */
    String resetPassword(String email, String newPassword);

    /**
     * Updates the current user's username.
     */
    String updateUsername(String newUsername);

    /**
     * Updates the current user's password.
     */
    String updatePassword(String oldPassword, String newPassword);

    /**
     * Gets the current user's email.
     */
    String getUserEmail();

    /**
     * Gets a list of all users.
     */
    String getUsers();

    /**
     * Updates the current user's high score.
     */
    String updateHighScore(int score);

    /**
     * Gets the current user's best score.
     */
    String getBestScore();

    /**
     * Gets the leaderboard.
     */
    String getLeaderboard();

    /**
     * Requests a password reset.
     */
    String requestPasswordReset(String username, String email);

    /**
     * Verifies a reset token.
     */
    String verifyResetToken(String username, String token, String newPassword);

    /**
     * Clears a reset token.
     */
    String clearResetToken(String username);
} 