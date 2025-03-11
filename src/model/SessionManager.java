package model;

/**
 * Manages session authentication token and username for API requests.
 */
public class SessionManager {
    private static String authToken = null;
    private static String username = null;
    private static String email = null;
    private static boolean emailFetchFailed = false;

    /**
     * Stores the authentication token.
     * @param token The authentication token received from the API.
     */
    public static void setAuthToken(String token) {
        authToken = token;
    }

    /**
     * Retrieves the stored authentication token.
     * @return The stored authentication token, or null if not set.
     */
    public static String getAuthToken() {
        return authToken;
    }

    /**
     * Stores the logged-in username.
     * @param name The username received from the API.
     */
    public static void setUsername(String name) {
        username = name;
    }

    /**
     * Retrieves the stored username.
     * @return The stored username, or null if not set.
     */
    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        if (email == null && !emailFetchFailed) {
            // Try to fetch email from server if not cached
            email = api.APIClient.getUserEmail();
            if (email == null) {
                emailFetchFailed = true; // Mark as failed to avoid repeated failing calls
            }
        }
        return email;
    }

    public static void setEmail(String userEmail) {
        email = userEmail;
        emailFetchFailed = false; // Reset the flag when email is manually set
    }

    /**
     * Clears the stored authentication token and username (used on logout).
     */
    public static void logout() {
        authToken = null;
        username = null;
        email = null;
        emailFetchFailed = false; // Reset the flag on logout
    }
}
