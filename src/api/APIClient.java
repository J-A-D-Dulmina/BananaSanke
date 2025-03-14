package api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import model.SessionManager; // For static methods
import model.SessionManagerImpl; // For interface implementation
import interfaces.ISessionManager;
import interfaces.IAPIClient;

/**
 * APIClient handles HTTP requests for interacting with the backend API.
 * Supports authentication via JWT tokens.
 */
public class APIClient implements IAPIClient {

    public static final String BASE_URL = "https://deshandulmina.info/api.php";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String ACCEPT_TYPE = "application/json";
    private static APIClient instance;
    private final ISessionManager sessionManager;

    private APIClient() {
        this.sessionManager = SessionManagerImpl.getInstance();
    }

    public static APIClient getInstance() {
        if (instance == null) {
            instance = new APIClient();
        }
        return instance;
    }

    /**
     * Sends a GET request with authentication.
     */
    @Override
    public String sendAuthenticatedGetRequest(String apiUrl) {
        return sendHttpRequest(apiUrl, "GET", null, true);
    }

    /**
     * Sends a POST request with authentication.
     */
    @Override
    public String sendAuthenticatedPostRequest(String apiUrl, String postData) {
        return sendHttpRequest(apiUrl, "POST", postData, true);
    }

    // For backward compatibility - these static methods delegate to the singleton instance
    public static String sendAuthenticatedGetRequestStatic(String apiUrl) {
        return getInstance().sendAuthenticatedGetRequest(apiUrl);
    }

    public static String sendAuthenticatedPostRequestStatic(String apiUrl, String postData) {
        return getInstance().sendAuthenticatedPostRequest(apiUrl, postData);
    }

    /**
     * Sends an HTTP request.
     */
    private String sendHttpRequest(String apiUrl, String method, String postData, boolean authRequired) {
        StringBuilder response = new StringBuilder();
        HttpURLConnection conn = null;

        try {
            conn = setupHttpConnection(apiUrl, method, authRequired);

            if ("POST".equals(method) && postData != null) {
                byte[] postDataBytes = postData.getBytes("UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postDataBytes);
                    os.flush();
                }
            }

            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            
            InputStream inputStream = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
            
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
            }

            if (response.length() == 0) {
                return String.format(
                    "{\"status\":\"error\", \"message\":\"Empty response from server (HTTP %d: %s)\", \"http_code\":%d}",
                    responseCode,
                    responseMessage,
                    responseCode
                );
            }

            try {
                new JSONObject(response.toString());
                return response.toString();
            } catch (JSONException e) {
                return String.format(
                    "{\"status\":\"error\", \"message\":\"Invalid JSON response from server\", \"http_code\":%d}",
                    responseCode
                );
            }
            
        } catch (Exception e) {
            return String.format(
                "{\"status\":\"error\", \"message\":\"%s\"}",
                e.getMessage().replace("\"", "\\\"")
            );
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private HttpURLConnection setupHttpConnection(String apiUrl, String method, boolean authRequired) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        conn.setRequestProperty("Accept", ACCEPT_TYPE);
        conn.setDoOutput("POST".equals(method));
        conn.setDoInput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        
        if (authRequired) {
            String authToken = sessionManager.getAuthToken();
            if (authToken != null && !authToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
            }
        }
        
        return conn;
    }

    /**
     * Registers a new user.
     */
    @Override
    public String registerUser(String username, String email, String password) {
        try {
            String postData = String.format(
                "username=%s&email=%s&password=%s",
                URLEncoder.encode(username, "UTF-8"),
                URLEncoder.encode(email, "UTF-8"),
                URLEncoder.encode(password, "UTF-8")
            );
            
            return sendHttpRequest(BASE_URL + "?action=register_user", "POST", postData, false);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String registerUserStatic(String username, String email, String password) {
        return getInstance().registerUser(username, email, password);
    }

    /**
     * Logs in a user.
     */
    @Override
    public String loginUser(String email, String password) {
        try {
            String postData = String.format(
                "email=%s&password=%s",
                URLEncoder.encode(email, "UTF-8"),
                URLEncoder.encode(password, "UTF-8")
            );
            
            return sendHttpRequest(BASE_URL + "?action=login_user", "POST", postData, false);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String loginUserStatic(String email, String password) {
        return getInstance().loginUser(email, password);
    }

    /**
     * Logs out the current user.
     */
    @Override
    public String logoutUser() {
        try {
            return sendAuthenticatedGetRequest(BASE_URL + "?action=logout_user");
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String logoutUserStatic() {
        return getInstance().logoutUser();
    }

    /**
     * Resets a user's password.
     */
    @Override
    public String resetPassword(String email, String newPassword) {
        try {
            String postData = String.format(
                "email=%s&new_password=%s",
                URLEncoder.encode(email, "UTF-8"),
                URLEncoder.encode(newPassword, "UTF-8")
            );
            
            return sendHttpRequest(BASE_URL + "?action=reset_password", "POST", postData, false);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String resetPasswordStatic(String email, String newPassword) {
        return getInstance().resetPassword(email, newPassword);
    }

    /**
     * Updates the current user's username.
     */
    @Override
    public String updateUsername(String newUsername) {
        try {
            String postData = String.format("new_username=%s", URLEncoder.encode(newUsername, "UTF-8"));
            return sendAuthenticatedPostRequest(BASE_URL + "?action=update_username", postData);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String updateUsernameStatic(String newUsername) {
        return getInstance().updateUsername(newUsername);
    }

    /**
     * Updates the current user's password.
     */
    @Override
    public String updatePassword(String oldPassword, String newPassword) {
        try {
            String postData = String.format(
                "old_password=%s&new_password=%s",
                URLEncoder.encode(oldPassword, "UTF-8"),
                URLEncoder.encode(newPassword, "UTF-8")
            );
            
            return sendAuthenticatedPostRequest(BASE_URL + "?action=update_password", postData);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String updatePasswordStatic(String oldPassword, String newPassword) {
        return getInstance().updatePassword(oldPassword, newPassword);
    }

    /**
     * Gets the current user's email.
     */
    @Override
    public String getUserEmail() {
        try {
            return sendAuthenticatedGetRequest(BASE_URL + "?action=get_user_email");
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String getUserEmailStatic() {
        return getInstance().getUserEmail();
    }

    /**
     * Gets a list of all users.
     */
    @Override
    public String getUsers() {
        try {
            return sendAuthenticatedGetRequest(BASE_URL + "?action=get_users");
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String getUsersStatic() {
        return getInstance().getUsers();
    }

    /**
     * Updates the current user's high score.
     */
    @Override
    public String updateHighScore(int score) {
        try {
            String postData = String.format("score=%d", score);
            return sendAuthenticatedPostRequest(BASE_URL + "?action=update_score", postData);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String updateHighScoreStatic(int score) {
        return getInstance().updateHighScore(score);
    }

    /**
     * Gets the current user's best score.
     */
    @Override
    public String getBestScore() {
        try {
            return sendAuthenticatedGetRequest(BASE_URL + "?action=get_best_score");
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String getBestScoreStatic() {
        return getInstance().getBestScore();
    }

    /**
     * Gets the leaderboard.
     */
    @Override
    public String getLeaderboard() {
        try {
            return sendAuthenticatedGetRequest(BASE_URL + "?action=get_leaderboard");
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String getLeaderboardStatic() {
        return getInstance().getLeaderboard();
    }

    /**
     * Requests a password reset.
     */
    @Override
    public String requestPasswordReset(String username, String email) {
        try {
            String postData = String.format(
                "username=%s&email=%s",
                URLEncoder.encode(username, "UTF-8"),
                URLEncoder.encode(email, "UTF-8")
            );
            
            return sendHttpRequest(BASE_URL + "?action=request_password_reset", "POST", postData, false);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String requestPasswordResetStatic(String username, String email) {
        return getInstance().requestPasswordReset(username, email);
    }

    /**
     * Verifies a reset token.
     */
    @Override
    public String verifyResetToken(String username, String token, String newPassword) {
        try {
            String postData = String.format(
                "username=%s&token=%s&new_password=%s",
                URLEncoder.encode(username, "UTF-8"),
                URLEncoder.encode(token, "UTF-8"),
                URLEncoder.encode(newPassword, "UTF-8")
            );
            
            return sendHttpRequest(BASE_URL + "?action=verify_reset_token", "POST", postData, false);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String verifyResetTokenStatic(String username, String token, String newPassword) {
        return getInstance().verifyResetToken(username, token, newPassword);
    }

    /**
     * Clears a reset token.
     */
    @Override
    public String clearResetToken(String username) {
        try {
            String postData = String.format("username=%s", URLEncoder.encode(username, "UTF-8"));
            return sendHttpRequest(BASE_URL + "?action=clear_reset_token", "POST", postData, false);
        } catch (Exception e) {
            return String.format("{\"status\":\"error\", \"message\":\"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    // Static method for backward compatibility
    public static String clearResetTokenStatic(String username) {
        return getInstance().clearResetToken(username);
    }
}
