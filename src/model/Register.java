package model;

import api.APIClient;
import factory.ComponentFactory;
import interfaces.IAPIClient;
import interfaces.IAuthenticationService;
import service.AuthenticationService;

public class Register {
    private static final IAuthenticationService authService = new AuthenticationService();
    
    public static String registerUser(String username, String email, String password) {
        return authService.registerUser(username, email, password);
    }
}
