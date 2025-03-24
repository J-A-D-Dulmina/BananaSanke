package model;

import interfaces.ISessionManager;

/**
 * Implementation of ISessionManager that delegates to SessionManager static methods.
 */
public class SessionManagerImpl implements ISessionManager {
    
    private static SessionManagerImpl instance = null;
    
    private SessionManagerImpl() {
        // Private constructor
    }
    
    public static synchronized SessionManagerImpl getInstance() {
        if (instance == null) {
            instance = new SessionManagerImpl();
        }
        return instance;
    }
    
    @Override
    public void setAuthToken(String token) {
        SessionManager.setAuthToken(token);
    }
    
    @Override
    public String getAuthToken() {
        return SessionManager.getAuthToken();
    }
    
    @Override
    public void setUsername(String name) {
        SessionManager.setUsername(name);
    }
    
    @Override
    public String getUsername() {
        return SessionManager.getUsername();
    }
    
    @Override
    public String getEmail() {
        return SessionManager.getEmail();
    }
    
    @Override
    public void setEmail(String userEmail) {
        SessionManager.setEmail(userEmail);
    }
    
    @Override
    public void logout() {
        SessionManager.logout();
    }
} 