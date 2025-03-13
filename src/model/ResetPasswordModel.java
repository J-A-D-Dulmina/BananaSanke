package model;

public class ResetPasswordModel {
    private String username;
    private String email;
    private String resetToken;
    private String newPassword;
    private String confirmPassword;
    private boolean isTokenValid;
    private boolean isPasswordValid;
    private int cooldownSeconds;
    private boolean isResendEnabled;

    public ResetPasswordModel(String username, String email) {
        this.username = username;
        this.email = email;
        this.cooldownSeconds = 30;
        this.isResendEnabled = false;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isTokenValid() {
        return isTokenValid;
    }

    public void setTokenValid(boolean tokenValid) {
        isTokenValid = tokenValid;
    }

    public boolean isPasswordValid() {
        return isPasswordValid;
    }

    public void setPasswordValid(boolean passwordValid) {
        isPasswordValid = passwordValid;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public boolean isResendEnabled() {
        return isResendEnabled;
    }

    public void setResendEnabled(boolean resendEnabled) {
        isResendEnabled = resendEnabled;
    }

    public void decrementCooldown() {
        if (cooldownSeconds > 0) {
            cooldownSeconds--;
            isResendEnabled = false;
        } else {
            isResendEnabled = true;
        }
    }

    public void resetCooldown() {
        cooldownSeconds = 30;
        isResendEnabled = false;
    }

    public boolean validatePasswords() {
        return newPassword != null && 
               confirmPassword != null && 
               newPassword.equals(confirmPassword) && 
               newPassword.length() >= 6;
    }

    public boolean validateToken() {
        return resetToken != null && !resetToken.isEmpty() && !resetToken.equals("Enter reset token from email");
    }
} 