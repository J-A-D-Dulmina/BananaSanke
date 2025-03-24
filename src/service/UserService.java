package service;

import interfaces.IAPIClient;
import interfaces.ISessionManager;
import interfaces.IUserService;
import org.json.JSONObject;

/**
 * Implementation of the IUserService interface.
 * Handles user-related operations.
 */
public class UserService implements IUserService {
    private final IAPIClient apiClient;
    private final ISessionManager sessionManager;
    
    /**
     * Constructs a UserService with the specified dependencies.
     * @param apiClient The API client to use for server communication
     * @param sessionManager The session manager to use for session data
     */
    public UserService(IAPIClient apiClient, ISessionManager sessionManager) {
        this.apiClient = apiClient;
        this.sessionManager = sessionManager;
    }
    
    @Override
    public JSONObject updateUsername(String newUsername) throws Exception {
        String response = apiClient.updateUsername(newUsername);
        JSONObject jsonResponse = new JSONObject(response);
        
        if (jsonResponse.getString("status").equals("success")) {
            sessionManager.setUsername(newUsername);
        }
        
        return jsonResponse;
    }
    
    @Override
    public JSONObject updatePassword(String oldPassword, String newPassword) throws Exception {
        String response = apiClient.updatePassword(oldPassword, newPassword);
        return new JSONObject(response);
    }
    
    @Override
    public JSONObject fetchBestScore() {
        try {
            String response = apiClient.getBestScore();
            return new JSONObject(response);
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch best score: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public void logout() {
        try {
            apiClient.logoutUser();
            sessionManager.logout();
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }
    
    @Override
    public String getUsername() {
        return sessionManager.getUsername();
    }
    
    @Override
    public String getEmail() {
        return sessionManager.getEmail();
    }
} 