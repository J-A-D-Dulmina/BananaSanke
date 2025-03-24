package model;

import interfaces.IAuthenticationService;
import service.AuthenticationService;

public class Login {
    private static final IAuthenticationService authService = new AuthenticationService();
    
    public static String authenticate(String email, String password) {
        return authService.authenticate(email, password);
    }
}
