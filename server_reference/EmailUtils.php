<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/src/Exception.php';
require 'PHPMailer/src/PHPMailer.php';
require 'PHPMailer/src/SMTP.php';

class EmailUtils {
    private static $FROM_EMAIL = "deshandulmina30840@gmail.com";
    private static $EMAIL_PASSWORD = "cefv hfij ypcb sktd";
    private static $SMTP_HOST = "smtp.gmail.com";
    private static $SMTP_PORT = 587;

    public static function sendPasswordResetEmail($toEmail, $username, $resetToken) {
        try {
            $mail = new PHPMailer(true);

            // Server settings
            $mail->SMTPDebug = SMTP::DEBUG_SERVER;
            $mail->isSMTP();
            $mail->Host = self::$SMTP_HOST;
            $mail->SMTPAuth = true;
            $mail->Username = self::$FROM_EMAIL;
            $mail->Password = self::$EMAIL_PASSWORD;
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
            $mail->Port = self::$SMTP_PORT;

            // Recipients
            $mail->setFrom(self::$FROM_EMAIL, 'Banana Snake Game');
            $mail->addAddress($toEmail);
            $mail->addReplyTo(self::$FROM_EMAIL);

            // Content
            $mail->isHTML(true);
            $mail->Subject = "Password Reset - Banana Snake Game";
            $mail->Body = "
            <html>
            <body>
                <h2 style='color: #333;'>Password Reset Request</h2>
                <p>Dear {$username},</p>
                <p>You have requested to reset your password.</p>
                <p>Your reset token is: <strong style='background-color: #f0f0f0; padding: 5px;'>{$resetToken}</strong></p>
                <p>Please use this token to reset your password. This token will expire in 1 hour.</p>
                <p>If you did not request this password reset, please ignore this email.</p>
                <br>
                <p>Best regards,<br>Banana Snake Game Team</p>
            </body>
            </html>";

            $mail->send();
            error_log("Password reset email sent successfully to: " . $toEmail);
            return true;
        } catch (Exception $e) {
            error_log("Error sending password reset email: " . $mail->ErrorInfo);
            return false;
        }
    }
}
?>