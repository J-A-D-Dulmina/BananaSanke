package api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import model.SessionManager; // For storing authentication tokens

/**
 * APIClient handles HTTP requests for interacting with the backend API.
 * Supports authentication via session token.
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
     * Sends a GET request to the given API URL with authentication.
     *
     * @param apiUrl The full API URL for the GET request.
     * @return The API response as a String.
     */
    public static String sendAuthenticatedGetRequest(String apiUrl) {
        return sendHttpRequest(apiUrl, "GET", null, true);
    }

    /**
     * Sends a POST request to the given API URL with authentication.
     *
     * @param apiUrl   The full API URL for the POST request.
     * @param postData The request payload as a URL-encoded string.
     * @return The API response as a String.
     */
    public static String sendAuthenticatedPostRequest(String apiUrl, String postData) {
        return sendHttpRequest(apiUrl, "POST", postData, true);
    }

    /**
     * Handles the core HTTP request logic for both GET and POST methods.
     *
     * @param apiUrl       The API endpoint URL.
     * @param method       The HTTP method (GET or POST).
     * @param postData     The request body for POST requests.
     * @param authRequired Whether authentication is required.
     * @return The response from the API as a String.
     */
    private static String sendHttpRequest(String apiUrl, String method, String postData, boolean authRequired) {
        StringBuilder response = new StringBuilder();

        try {
            HttpURLConnection conn = setupHttpConnection(apiUrl, method, authRequired);

            if ("POST".equals(method) && postData != null) {
                // Only add auth token if it's not already in the postData
                if (authRequired && !postData.contains("auth_token=")) {
                    String authToken = SessionManager.getAuthToken();
                    if (authToken != null && !authToken.isEmpty()) {
                        String encodedToken = java.net.URLEncoder.encode(authToken, "UTF-8");
                        postData += (postData.isEmpty() ? "" : "&") + "auth_token=" + encodedToken;
                    }
                }
                writePostData(conn, postData);
            }

            response.append(readResponse(conn));
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Network error occurred\"}";
        }

        return response.toString();
    }

    /**
     * Sets up an HttpURLConnection object with necessary headers.
     *
     * @param apiUrl       The API endpoint URL.
     * @param method       The HTTP method (GET or POST).
     * @param authRequired Whether authentication is required.
     * @return Configured HttpURLConnection instance.
     * @throws IOException If an I/O error occurs.
     */
    private static HttpURLConnection setupHttpConnection(String apiUrl, String method, boolean authRequired) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", ACCEPT_TYPE);

        if (authRequired) {
            String authToken = SessionManager.getAuthToken();
            if (authToken != null && !authToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
            }
        }

        if ("POST".equals(method)) {
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        }

        return conn;
    }

    /**
     * Writes POST request data to the connection output stream.
     *
     * @param conn     The HttpURLConnection instance.
     * @param postData The request body as a URL-encoded string.
     * @throws IOException If an I/O error occurs.
     */
    private static void writePostData(HttpURLConnection conn, String postData) throws IOException {
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.writeBytes(postData);
            out.flush();
        }
    }

    /**
     * Reads the API response from the connection input stream.
     *
     * @param conn The HttpURLConnection instance.
     * @return The response as a String.
     * @throws IOException If an I/O error occurs.
     */
    private static String readResponse(HttpURLConnection conn) throws IOException {
        StringBuilder response = new StringBuilder();
        
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        InputStream inputStream;
        if (responseCode >= 400) {
            inputStream = conn.getErrorStream();
        } else {
            inputStream = conn.getInputStream();
        }

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("Request failed. Response: " + response.toString());
        }

        return response.toString();
    }

    /**
     * Registers a new user.
     *
     * @param username The desired username.
     * @param email    The user's email.
     * @param password The user's password.
     * @return The API response message.
     */
    public static String registerUser(String username, String email, String password) {
        try {
            String apiUrl = BASE_URL + "?action=register_user";
            String postData = String.format("username=%s&email=%s&password=%s",
                java.net.URLEncoder.encode(username, "UTF-8"),
                java.net.URLEncoder.encode(email, "UTF-8"),
                java.net.URLEncoder.encode(password, "UTF-8"));

            String response = sendHttpRequest(apiUrl, "POST", postData, false);
            System.out.println("Server Response: " + response);

            try {
                JSONObject jsonResponse = new JSONObject(response);
                return jsonResponse.optString("message", "Unexpected error occurred");
            } catch (JSONException e) {
                return "Error processing registration request";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Error encoding request parameters";
        }
    }

    /**
     * Logs in a user and stores authentication token.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return The full API response as JSON.
     */
    public static String loginUser(String email, String password) {
        try {
            String apiUrl = BASE_URL + "?action=login_user";
            String postData = String.format("email=%s&password=%s",
                java.net.URLEncoder.encode(email, "UTF-8"),
                java.net.URLEncoder.encode(password, "UTF-8"));

            String response = sendHttpRequest(apiUrl, "POST", postData, false);
            System.out.println("Server Response: " + response);

            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.optString("status").equals("success")) {
                    String authToken = jsonResponse.optString("auth_token");
                    SessionManager.setAuthToken(authToken);
                }
            } catch (JSONException e) {
                return "{\"status\":\"error\", \"message\":\"Error processing login request\"}";
            }

            return response;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Error encoding request parameters\"}";
        }
    }

    /**
     * Logs out a user and clears authentication token.
     *
     * @return The API response message.
     */
    public static String logoutUser() {
        String apiUrl = BASE_URL + "?action=logout_user";
        String authToken = SessionManager.getAuthToken();

        if (authToken == null) {
            return "{\"status\":\"error\", \"message\":\"No active session\"}";
        }

        String postData = "auth_token=" + authToken;
        String response = sendHttpRequest(apiUrl, "POST", postData, true);
        System.out.println("API Response (Logout): " + response);

        SessionManager.logout(); // Fixed: Now using the correct method
        return response;
    }

    /**
     * Updates the user's high score if the new score is higher than the current one.
     *
     * @param newScore The new score to potentially update as high score
     * @param callback The callback to handle the response
     */
    public static void updateHighScore(int newScore, HighScoreCallback callback) {
        new Thread(() -> {
            try {
                String token = SessionManager.getAuthToken();
                if (token == null || token.isEmpty()) {
                    callback.onFailure("User not authenticated");
                    return;
                }

                String apiUrl = BASE_URL + "?action=update_high_score";
                String postData = String.format("score=%s&auth_token=%s",
                    java.net.URLEncoder.encode(String.valueOf(newScore), "UTF-8"),
                    java.net.URLEncoder.encode(token, "UTF-8"));
                
                String response = sendHttpRequest(apiUrl, "POST", postData, true);
                System.out.println("Server Response: " + response);
                
                if (response.contains("<br")) {
                    response = response.replaceAll("^<br[^>]*>.*?<br[^>]*>", "").trim();
                }
                
                JSONObject jsonResponse = new JSONObject(response);
                String status = jsonResponse.optString("status");
                String message = jsonResponse.optString("message", "Unknown error occurred");
                
                if ("success".equals(status)) {
                    int highScore = jsonResponse.optInt("new_high_score", newScore);
                    callback.onNewHighScore(highScore);
                } else if ("not_higher".equals(status)) {
                    callback.onScoreNotHigher(message);
                } else {
                    callback.onFailure(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onFailure("Error processing server response: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Error updating high score: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Gets the current user's high score.
     * 
     * @param callback The callback to handle the response
     */
    public static void getCurrentHighScore(HighScoreCallback callback) {
        new Thread(() -> {
            try {
                String token = SessionManager.getAuthToken();
                if (token == null || token.isEmpty()) {
                    callback.onFailure("User not authenticated");
                    return;
                }

                String apiUrl = BASE_URL + "?action=get_high_score";
                String response = sendAuthenticatedGetRequest(apiUrl);
                System.out.println("Server Response: " + response);
                
                if (response.contains("<br")) {
                    response = response.replaceAll("^<br[^>]*>.*?<br[^>]*>", "").trim();
                }
                
                JSONObject jsonResponse = new JSONObject(response);
                String status = jsonResponse.optString("status");
                
                if ("success".equals(status)) {
                    int highScore = jsonResponse.optInt("high_score", 0);
                    callback.onNewHighScore(highScore);
                } else {
                    String message = jsonResponse.optString("message", "Unknown error occurred");
                    callback.onFailure(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onFailure("Error processing server response: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Error getting high score: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Gets the user's email from the server.
     *
     * @return The API response containing the user's email.
     */
    public static String getUserEmail() {
        try {
            String apiUrl = BASE_URL + "?action=get_user_email";
            String response = sendAuthenticatedGetRequest(apiUrl);
            
            // Check if response is empty or not JSON
            if (response == null || response.isEmpty() || !response.trim().startsWith("{")) {
                System.err.println("Invalid response format from server: " + response);
                return null;
            }
            
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getString("status").equals("success")) {
                String email = jsonResponse.getString("email");
                SessionManager.setEmail(email); // Cache the email
                return email;
            } else {
                System.err.println("Failed to get email: " + jsonResponse.optString("message"));
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error getting user email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the user's username.
     *
     * @param newUsername The new username to set
     * @return The API response as a JSONObject
     */
    public static JSONObject updateUsername(String newUsername) {
        try {
            String apiUrl = BASE_URL + "?action=update_username";
            String postData = String.format("new_username=%s",
                java.net.URLEncoder.encode(newUsername, "UTF-8"));

            String response = sendAuthenticatedPostRequest(apiUrl, postData);
            
            // Handle HTML error responses
            if (response.contains("<br") || response.contains("<html")) {
                String errorMessage = response.replaceAll("<[^>]*>", "")
                    .replaceAll("\\s+", " ")
                    .trim();
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Server Error: " + errorMessage);
                return errorResponse;
            }
            
            // Parse and return JSON response
            return new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to update username: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Updates the user's password.
     *
     * @param oldPassword The user's current password
     * @param newPassword The new password to set
     * @return The API response as a JSONObject
     */
    public static JSONObject updatePassword(String oldPassword, String newPassword) {
        try {
            String apiUrl = BASE_URL + "?action=update_password";
            String postData = String.format("old_password=%s&new_password=%s",
                java.net.URLEncoder.encode(oldPassword, "UTF-8"),
                java.net.URLEncoder.encode(newPassword, "UTF-8"));

            String response = sendAuthenticatedPostRequest(apiUrl, postData);
            
            // Handle HTML error responses
            if (response.contains("<br") || response.contains("<html")) {
                String errorMessage = response.replaceAll("<[^>]*>", "")
                    .replaceAll("\\s+", " ")
                    .trim();
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Server Error: " + errorMessage);
                return errorResponse;
            }
            
            // Parse and return JSON response
            return new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to update password: " + e.getMessage());
            return errorResponse;
        }
    }

    public interface HighScoreCallback {
        void onNewHighScore(int newHighScore);
        void onScoreNotHigher(String message);
        void onFailure(String error);
    }

}
