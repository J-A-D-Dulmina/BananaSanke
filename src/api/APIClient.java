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
                // Set content length for POST requests
                byte[] postDataBytes = postData.getBytes("UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                
                // Write POST data
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postDataBytes);
                    os.flush();
                }
            }

            // Get response code and message
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();
            
            // Read the response
            InputStream inputStream = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
            
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
            }

            // If empty response, return error JSON with HTTP status info
            if (response.length() == 0) {
                return String.format(
                    "{\"status\":\"error\", \"message\":\"Empty response from server (HTTP %d: %s)\", \"http_code\":%d}",
                    responseCode,
                    responseMessage,
                    responseCode
                );
            }

            // Try to parse the response as JSON
            try {
                new JSONObject(response.toString());
                return response.toString();
            } catch (JSONException e) {
                // If response is not JSON, wrap it in a JSON error object
                System.err.println("Non-JSON response from server: " + response.toString());
                return String.format(
                    "{\"status\":\"error\", \"message\":\"Invalid JSON response from server (HTTP %d: %s): %s\", \"http_code\":%d}",
                    responseCode,
                    responseMessage,
                    response.toString().replace("\"", "'").replace("\n", " "),
                    responseCode
                );
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return String.format(
                "{\"status\":\"error\", \"message\":\"Network error: %s\", \"error_type\":\"network\"}",
                e.getMessage().replace("\"", "'")
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
            // Client-side validation
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
            
            // Build POST data with proper encoding
            StringBuilder postData = new StringBuilder();
            postData.append("username=").append(URLEncoder.encode(username.trim(), "UTF-8"));
            postData.append("&email=").append(URLEncoder.encode(email.trim(), "UTF-8"));
            postData.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

            // Log request details (remove in production)
            System.out.println("Sending registration request to: " + apiUrl);
            System.out.println("POST data (encoded): " + postData.toString());

            String response = sendHttpRequest(apiUrl, "POST", postData.toString(), false);
            System.out.println("Raw server response: " + response);
            
            // Validate JSON response
            try {
                JSONObject jsonResponse = new JSONObject(response);
                System.out.println("Response status: " + jsonResponse.optString("status"));
                
                // Log debug information if available
                if (jsonResponse.has("debug")) {
                    System.out.println("Debug info: " + jsonResponse.getJSONObject("debug").toString(2));
                }
                
                return response;
            } catch (JSONException e) {
                System.err.println("Invalid JSON response from server: " + response);
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Error processing registration request: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String loginUser(String email, String password) {
        try {
            String apiUrl = BASE_URL + "?action=login_user";
            
            // Build POST data with proper encoding
            StringBuilder postData = new StringBuilder();
            postData.append("email=").append(URLEncoder.encode(email, "UTF-8"));
            postData.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

            String response = sendHttpRequest(apiUrl, "POST", postData.toString(), false);
            
            // Validate JSON response
            try {
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.optString("status").equals("success")) {
                    String authToken = jsonResponse.optString("auth_token");
                    if (authToken == null || authToken.isEmpty()) {
                        return "{\"status\":\"error\", \"message\":\"Server did not provide authentication token\"}";
                    }
                    
                    // Store the auth token
                    SessionManager.setAuthToken(authToken);
                    
                    // Verify the token was stored
                    String storedToken = SessionManager.getAuthToken();
                    if (storedToken == null || storedToken.isEmpty()) {
                        return "{\"status\":\"error\", \"message\":\"Failed to store authentication token\"}";
                    }
                } else if (jsonResponse.optString("status").equals("error") && 
                          jsonResponse.optString("message").contains("already logged in")) {
                    // Handle case where user is already logged in
                    return "{\"status\":\"error\", \"message\":\"You are already logged in on another device. Please logout first.\"}";
                }
                
                return response;
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"Error processing login request: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String logoutUser() {
        try {
            String apiUrl = BASE_URL + "?action=logout_user";
            
            // Get the current auth token before clearing the session
            String authToken = SessionManager.getAuthToken();
            if (authToken == null || authToken.isEmpty()) {
                // If no token exists, just clear local session and return success
                SessionManager.logout();
                return "{\"status\":\"success\",\"message\":\"Logged out successfully\"}";
            }
            
            // Send the logout request with the current token
            String response = sendHttpRequest(apiUrl, "GET", null, true);
            
            // Extract JSON part from response if it contains PHP warnings
            String jsonResponse = response;
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}") + 1;
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                jsonResponse = response.substring(jsonStart, jsonEnd);
            }
            
            // Try to parse the response
            try {
                JSONObject jsonObj = new JSONObject(jsonResponse);
                if (jsonObj.optString("status").equals("success")) {
                    // Only clear local session if server confirms success
                    SessionManager.logout();
                    return jsonResponse;
                } else {
                    // If server reports error, log it but still clear local session
                    System.err.println("Server reported logout error: " + jsonObj.optString("message"));
                    SessionManager.logout();
                    return "{\"status\":\"success\",\"message\":\"Logged out successfully\"}";
                }
            } catch (JSONException e) {
                // If response is not valid JSON, log it but still clear local session
                System.err.println("Invalid JSON response from server: " + jsonResponse);
                SessionManager.logout();
                return "{\"status\":\"success\",\"message\":\"Logged out successfully\"}";
            }
        } catch (Exception e) {
            // Log the error but still clear local session
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
            SessionManager.logout();
            return "{\"status\":\"success\",\"message\":\"Logged out successfully\"}";
        }
    }

    public static String resetPassword(String email, String newPassword) {
        try {
            String apiUrl = BASE_URL + "?action=reset_password";
            
            // Build POST data with proper encoding
            StringBuilder postData = new StringBuilder();
            postData.append("email=").append(URLEncoder.encode(email, "UTF-8"));
            postData.append("&new_password=").append(URLEncoder.encode(newPassword, "UTF-8"));

            // Log request details (remove in production)
            System.out.println("Sending password reset request to: " + apiUrl);
            System.out.println("POST data (encoded): " + postData.toString());

            String response = sendHttpRequest(apiUrl, "POST", postData.toString(), false);
            System.out.println("Raw server response: " + response);
            
            // Validate JSON response
            try {
                JSONObject jsonResponse = new JSONObject(response);
        return response;
            } catch (JSONException e) {
                System.err.println("Invalid JSON response from server: " + response);
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Error resetting password: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String updateUsername(String newUsername) {
        try {
            String apiUrl = BASE_URL + "?action=update_username";
            
            // Build POST data with proper encoding
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
            
            // Build POST data with proper encoding
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
            
            // Log request details (remove in production)
            System.out.println("Sending get user email request to: " + apiUrl);

            String response = sendAuthenticatedGetRequest(apiUrl);
            System.out.println("Raw server response: " + response);
            
            // Validate JSON response
            try {
                JSONObject jsonResponse = new JSONObject(response);
                return response;
            } catch (JSONException e) {
                System.err.println("Invalid JSON response from server: " + response);
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Error getting user email: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String getUsers() {
        try {
            String apiUrl = BASE_URL + "?action=get_users";
            
            // Log request details (remove in production)
            System.out.println("Sending get users request to: " + apiUrl);

            String response = sendAuthenticatedGetRequest(apiUrl);
            System.out.println("Raw server response: " + response);
            
            // Validate JSON response
            try {
                JSONObject jsonResponse = new JSONObject(response);
                return response;
            } catch (JSONException e) {
                System.err.println("Invalid JSON response from server: " + response);
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Error getting users list: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String updateHighScore(int score) {
        try {
            String apiUrl = BASE_URL + "?action=update_score";
            
            // Build POST data with proper encoding
            StringBuilder postData = new StringBuilder();
            postData.append("score=").append(URLEncoder.encode(String.valueOf(score), "UTF-8"));

            // Log request details
            System.out.println("Sending score update request to: " + apiUrl);
            System.out.println("Score being sent: " + score);

            String response = sendHttpRequest(apiUrl, "POST", postData.toString(), true);
            System.out.println("Score update response: " + response);
            
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Error updating score: " + 
                   e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static String getBestScore() {
        try {
            String apiUrl = BASE_URL + "?action=get_best_score";
            
            // Log request details
            System.out.println("Sending get best score request to: " + apiUrl);
            
            // Ensure we have an auth token
            String authToken = SessionManager.getAuthToken();
            System.out.println("Auth token present: " + (authToken != null && !authToken.isEmpty()));
            
            if (authToken == null || authToken.isEmpty()) {
                System.err.println("No auth token available for getBestScore request");
                return "{\"status\":\"error\", \"message\":\"Not authenticated\"}";
            }
            
            String response = sendAuthenticatedGetRequest(apiUrl);
            System.out.println("Raw best score response: " + response);
            
            // Validate JSON response
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getString("status").equals("success")) {
                    int bestScore = jsonResponse.getInt("best_score");
                    System.out.println("Successfully retrieved best score: " + bestScore);
                    return response;
                } else {
                    String errorMessage = jsonResponse.optString("message", "Unknown error");
                    System.err.println("Error getting best score: " + errorMessage);
                    if (jsonResponse.has("debug")) {
                        System.err.println("Debug info: " + jsonResponse.getJSONObject("debug").toString(2));
                    }
                    return response;
                }
            } catch (JSONException e) {
                System.err.println("Invalid JSON response from server: " + response);
                return "{\"status\":\"error\", \"message\":\"Server returned invalid response: " + 
                       response.replace("\"", "'").replace("\n", " ") + "\"}";
            }
        } catch (Exception e) {
            System.err.println("Error in getBestScore: " + e.getMessage());
            e.printStackTrace();
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
}
