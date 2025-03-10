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

    private static final String BASE_URL = "https://deshandulmina.info/api.php";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String ACCEPT_TYPE = "application/json";

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
            if (authToken != null) {
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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("Request failed. Response Code: " + responseCode);
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
        String apiUrl = BASE_URL + "?action=register_user";
        String postData = "username=" + username + "&email=" + email + "&password=" + password;

        String response = sendHttpRequest(apiUrl, "POST", postData, false);
        System.out.println("API Response (Register): " + response);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.optString("message", "Unexpected error occurred");
        } catch (JSONException e) {
            return "Error processing registration request";
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
        String apiUrl = BASE_URL + "?action=login_user";
        String postData = "email=" + email + "&password=" + password;

        String response = sendHttpRequest(apiUrl, "POST", postData, false);
        System.out.println("API Response (Login): " + response);

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

}
