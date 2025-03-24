package interfaces;

/**
 * Interface for the ResetPasswordController that manages password reset functionality
 */
public interface IResetPasswordController {
    
    /**
     * Resets the password with the provided token and new password
     * 
     * @param token the reset token
     * @param newPassword the new password
     * @param confirmPassword the confirmation of the new password
     */
    void resetPassword(String token, String newPassword, String confirmPassword);
    
    /**
     * Resends the reset token to the user's email
     */
    void resendToken();
    
    /**
     * Clears the reset token for the user
     */
    void clearResetToken();
} 