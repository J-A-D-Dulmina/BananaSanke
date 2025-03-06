package model;

import api.APIClient;

public class Register {
    public static String registerUser(String username, String email, String password) {
        return APIClient.registerUser(username, email, password);
    }
}
