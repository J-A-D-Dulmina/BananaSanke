package api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import model.SessionManager; // For storing authentication tokens

/**
 * APIClient handles HTTP requests for interacting with the backend API.
 * Supports authentication via JWT tokens.
 */
public class APIClient {

    public static final String BASE_URL = "https://deshandulmina.info/api.php";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String ACCEPT_TYPE = "application/json";
    private static APIClient instance;

    private APIClient() {}

    public static APIClient getInstance() {
        if (instance == null) {
            instance = new APIClient();
        }
        return instance;
    }

    /**
     * Sends a GET request with authentication.
     */
    public static String sendAuthenticatedGetRequest(String apiUrl) {
        return sendHttpRequest(apiUrl, "GET", null, true);
    }

    /**
     * Sends a POST request with authentication.
     */
    public static String sendAuthenticatedPostRequest(String apiUrl, String postData) {
        return sendHttpRequest(apiUrl, "POST", postData, true);
    }

    /**
     * Sends an HTTP request.
     */
    private static String sendHttpRequest(String apiUrl, String method, String postData, boolean authRequired) {
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
            
        } catch (IOException e) {
            return String.format(
                "{\"status\":\"error\", \"message\":\"Network error\", \"error_type\":\"network\"}"
            );
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Sets up an HTTP connection.
     */
    private static HttpURLConnection setupHttpConnection(String apiUrl, String method, boolean authRequired) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        conn.setRequestProperty("Accept", ACCEPT_TYPE);
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("User-Agent", "BananaSnake-Game/1.0");

        // Configure connection for POST requests
        if ("POST".equals(method)) {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
        }

        if (authRequired) {
            String authToken = SessionManager.getAuthToken();
            if (authToken != null && !authToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
            }
        }

        return conn;
    }

    /**
     * Writes POST data.
     */
    private static void writePostData(HttpURLConnection conn, String postData) throws IOException {
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.writeBytes(postData);
            out.flush();
        }
    }

    // ==============================
    // User Management API Calls
    // ==============================

    public static String registerUser(String username, String email, String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return "{\"status\":\"error\", \"message\":\"Username is required\"}";
            }
            if (username.length() < 3 || username.length() > 50) {
                return "{\"status\":\"error\", \"message\":\"Username must be between 3 and 50 characters\"}";
            }
            if (email == null || email.trim().isEmpty()) {
                return "{\"status\":\"error\", \"message\":\"Email is required\"}";
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "{\"status\":\"error\", \"message\":\"Invalid email format\"}";
            }
            if (password == null || password.trim().isEmpty()) {
                return "{\"status\":\"error\", \"message\":\"Password is required\"}";
            }
            if (password.length() < 6) {
                return "{\"status\":\"error\", \"message\":\"Password must be at least 6 characters long\"}";
            }

            String apiUrl = BASE_URL + "?action=register_user";
            
            StringBuilder postData = new StringBuilder();
            postData.append("username=").append(URLEncoder.encode(username.trim(), "UTF-8"));
            postData.append("&email=").append(URLEncoder.encode(email.trim(), "UTF-8"));
            postData.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

            String response = sendHttpRequest(apiUrl, "POST", postData.toString(), false);
            
            try {
                new JSONObject(response);
                return response;
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error processing registration request\"}";
        }
    }

    public static String loginUser(String email, String password) {
        try {
            String apiUrl = BASE_URL + "?action=login_user";
            
            StringBuilder postData = new StringBuilder();
            postData.append("email=").append(URLEncoder.encode(email, "UTF-8"));
            postData.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

            String response = sendHttpRequest(apiUrl, "POST", postData.toString(), false);
            
            try {
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.optString("status").equals("success")) {
                    String authToken = jsonResponse.optString("auth_token");
                    if (authToken == null || authToken.isEmpty()) {
                        return "{\"status\":\"error\", \"message\":\"Server did not provide authentication token\"}";
                    }
                    
                    SessionManager.setAuthToken(authToken);
                    
                    String storedToken = SessionManager.getAuthToken();
                    if (storedToken == null || storedToken.isEmpty()) {
                        return "{\"status\":\"error\", \"message\":\"Failed to store authentication token\"}";
                    }
                } else if (jsonResponse.optString("status").equals("error") && 
                          jsonResponse.optString("message").contains("already logged in")) {
                    return "{\"status\":\"error\", \"message\":\"You are already logged in on another device. Please logout first.\"}";
                }
                
                return response;
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error processing login request\"}";
        }
    }

    public static String logoutUser() {
        try {
            String apiUrl = BASE_URL + "?action=logout";
            String response = sendAuthenticatedPostRequest(apiUrl, "");
            
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (!jsonResponse.optString("status").equals("success")) {
                    return "{\"status\":\"error\", \"message\":\"Logout failed: " + 
                           jsonResponse.optString("message") + "\"}";
                }
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response\"}";
            }
            
            return response;
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error logging out: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String resetPassword(String email, String newPassword) {
        try {
            String apiUrl = BASE_URL + "?action=reset_password";
            
            StringBuilder postData = new StringBuilder();
            postData.append("email=").append(URLEncoder.encode(email, "UTF-8"));
            postData.append("&new_password=").append(URLEncoder.encode(newPassword, "UTF-8"));

            String response = sendHttpRequest(apiUrl, "POST", postData.toString(), false);
            
            try {
                JSONObject jsonResponse = new JSONObject(response);
                return response;
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error resetting password: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String updateUsername(String newUsername) {
        try {
            String apiUrl = BASE_URL + "?action=update_username";
            
            StringBuilder postData = new StringBuilder();
            postData.append("new_username=").append(URLEncoder.encode(newUsername, "UTF-8"));

            return sendAuthenticatedPostRequest(apiUrl, postData.toString());
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error updating username: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String updatePassword(String oldPassword, String newPassword) {
        try {
            String apiUrl = BASE_URL + "?action=update_password";
            
            StringBuilder postData = new StringBuilder();
            postData.append("old_password=").append(URLEncoder.encode(oldPassword, "UTF-8"));
            postData.append("&new_password=").append(URLEncoder.encode(newPassword, "UTF-8"));

            return sendAuthenticatedPostRequest(apiUrl, postData.toString());
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error updating password: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String getUserEmail() {
        try {
            String apiUrl = BASE_URL + "?action=get_user_email";
            String response = sendAuthenticatedGetRequest(apiUrl);
            
            try {
                JSONObject jsonResponse = new JSONObject(response);
                return response;
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error getting user email: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String getUsers() {
        try {
            String apiUrl = BASE_URL + "?action=get_users";
            String response = sendAuthenticatedGetRequest(apiUrl);
            
            try {
                JSONObject jsonResponse = new JSONObject(response);
                return response;
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error getting users list: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String updateHighScore(int score) {
        try {
            String apiUrl = BASE_URL + "?action=update_score";
            StringBuilder postData = new StringBuilder();
            postData.append("score=").append(URLEncoder.encode(String.valueOf(score), "UTF-8"));
            return sendHttpRequest(apiUrl, "POST", postData.toString(), true);
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error updating score: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String getBestScore() {
        try {
            String apiUrl = BASE_URL + "?action=get_best_score";
            String authToken = SessionManager.getAuthToken();
            
            if (authToken == null || authToken.isEmpty()) {
                return "{\"status\":\"error\", \"message\":\"Not authenticated\"}";
            }
            
            String response = sendAuthenticatedGetRequest(apiUrl);
            
            try {
                JSONObject jsonResponse = new JSONObject(response);
                return response;
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error fetching best score: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String getLeaderboard() {
        try {
            String apiUrl = BASE_URL + "?action=get_leaderboard";
            return sendAuthenticatedGetRequest(apiUrl);
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error fetching leaderboard: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String requestPasswordReset(String username, String email) {
        try {
            String apiUrl = BASE_URL + "?action=request_password_reset";
            StringBuilder postData = new StringBuilder();
            postData.append("username=").append(URLEncoder.encode(username, "UTF-8"));
            postData.append("&email=").append(URLEncoder.encode(email, "UTF-8"));
            return sendHttpRequest(apiUrl, "POST", postData.toString(), false);
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error requesting password reset: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String verifyResetToken(String username, String token, String newPassword) {
        try {
            String apiUrl = BASE_URL + "?action=verify_reset_token";
            StringBuilder postData = new StringBuilder();
            postData.append("username=").append(URLEncoder.encode(username, "UTF-8"));
            postData.append("&token=").append(URLEncoder.encode(token, "UTF-8"));
            postData.append("&new_password=").append(URLEncoder.encode(newPassword, "UTF-8"));
            return sendHttpRequest(apiUrl, "POST", postData.toString(), false);
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error verifying reset token: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String clearResetToken(String username) {
        try {
            String apiUrl = BASE_URL + "?action=clear_reset_token";
            StringBuilder postData = new StringBuilder();
            postData.append("username=").append(URLEncoder.encode(username, "UTF-8"));
            return sendHttpRequest(apiUrl, "POST", postData.toString(), false);
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error clearing reset token: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }
}
