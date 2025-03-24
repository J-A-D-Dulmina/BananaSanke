package service;

import interfaces.IAuthenticationService;
import interfaces.IAPIClient;
import factory.ComponentFactory;

/**
 * Implementation of the authentication service
 */
public class AuthenticationService implements IAuthenticationService {
    
    private final IAPIClient apiClient;
    
    /**
     * Constructs a new AuthenticationService
     */
    public AuthenticationService() {
        this.apiClient = ComponentFactory.getAPIClient();
    }
    
    @Override
    public String authenticate(String email, String password) {
        return apiClient.loginUser(email, password);
    }
    
    @Override
    public String registerUser(String username, String email, String password) {
        return apiClient.registerUser(username, email, password);
    }
} 