package interfaces;

/**
 * Interface for authentication services
 */
public interface IAuthenticationService {
    
    /**
     * Authenticates a user with email and password
     * 
     * @param email the user's email
     * @param password the user's password
     * @return the response from the authentication service
     */
    String authenticate(String email, String password);
    
    /**
     * Registers a new user
     * 
     * @param username the new user's username
     * @param email the new user's email
     * @param password the new user's password
     * @return the response from the registration service
     */
    String registerUser(String username, String email, String password);
} 