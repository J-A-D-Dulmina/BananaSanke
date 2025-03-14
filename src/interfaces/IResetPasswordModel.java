package interfaces;

/**
 * Interface for the ResetPasswordModel that manages password reset data and logic
 */
public interface IResetPasswordModel {
    
    /**
     * Gets the username
     * 
     * @return the username
     */
    String getUsername();
    
    /**
     * Gets the email
     * 
     * @return the email
     */
    String getEmail();
    
    /**
     * Gets the reset token
     * 
     * @return the reset token
     */
    String getResetToken();
    
    /**
     * Sets the reset token
     * 
     * @param resetToken the reset token to set
     */
    void setResetToken(String resetToken);
    
    /**
     * Gets the new password
     * 
     * @return the new password
     */
    String getNewPassword();
    
    /**
     * Sets the new password
     * 
     * @param newPassword the new password to set
     */
    void setNewPassword(String newPassword);
    
    /**
     * Gets the confirm password
     * 
     * @return the confirm password
     */
    String getConfirmPassword();
    
    /**
     * Sets the confirm password
     * 
     * @param confirmPassword the confirm password to set
     */
    void setConfirmPassword(String confirmPassword);
    
    /**
     * Checks if the token is valid
     * 
     * @return true if the token is valid
     */
    boolean isTokenValid();
    
    /**
     * Sets whether the token is valid
     * 
     * @param tokenValid true if the token is valid
     */
    void setTokenValid(boolean tokenValid);
    
    /**
     * Checks if the password is valid
     * 
     * @return true if the password is valid
     */
    boolean isPasswordValid();
    
    /**
     * Sets whether the password is valid
     * 
     * @param passwordValid true if the password is valid
     */
    void setPasswordValid(boolean passwordValid);
    
    /**
     * Gets the cooldown seconds
     * 
     * @return the cooldown seconds
     */
    int getCooldownSeconds();
    
    /**
     * Sets the cooldown seconds
     * 
     * @param cooldownSeconds the cooldown seconds to set
     */
    void setCooldownSeconds(int cooldownSeconds);
    
    /**
     * Checks if resending the token is enabled
     * 
     * @return true if resending is enabled
     */
    boolean isResendEnabled();
    
    /**
     * Sets whether resending the token is enabled
     * 
     * @param resendEnabled true if resending is enabled
     */
    void setResendEnabled(boolean resendEnabled);
    
    /**
     * Decrements the cooldown seconds
     */
    void decrementCooldown();
    
    /**
     * Resets the cooldown
     */
    void resetCooldown();
    
    /**
     * Validates the passwords
     * 
     * @return true if the passwords are valid
     */
    boolean validatePasswords();
    
    /**
     * Validates the token
     * 
     * @return true if the token is valid
     */
    boolean validateToken();
} 