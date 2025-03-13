package utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtils {
    // Remove email configuration since it's now handled by the server
    // We don't need SMTP settings in the Java client anymore
    
    public static boolean sendPasswordResetEmail(String toEmail, String username, String resetToken) {
        try {
            // The actual email sending is now handled by the server through the API
            // This method now just returns true since the API handles the email sending
            return true;
        } catch (Exception e) {
            System.err.println("Error in sendPasswordResetEmail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

} 