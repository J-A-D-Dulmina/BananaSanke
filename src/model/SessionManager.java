package model;

/**
 * Manages session authentication token and username for API requests.
 */
public class SessionManager {
    private static String authToken;
    private static String username;

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

    /**
     * Clears the stored authentication token and username (used on logout).
     */
    public static void logout() {
        authToken = null;
        username = null;
    }
}
