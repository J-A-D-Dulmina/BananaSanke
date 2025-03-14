package model;

import api.APIClient;

public class Login {
    public static String authenticate(String email, String password) {
        return APIClient.loginUser(email, password);
    }
}
