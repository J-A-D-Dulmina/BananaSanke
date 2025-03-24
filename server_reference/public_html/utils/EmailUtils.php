<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

// Require PHPMailer files
require_once __DIR__ . '/PHPMailer/src/Exception.php';
require_once __DIR__ . '/PHPMailer/src/PHPMailer.php';
require_once __DIR__ . '/PHPMailer/src/SMTP.php';

class EmailUtils {
    private const FROM_EMAIL = "deshandulmina30840@gmail.com";
    private const EMAIL_PASSWORD = "cefv hfij ypcb sktd";
    private const SMTP_HOST = "smtp.gmail.com";
    private const SMTP_PORT = 587;

    public static function sendPasswordResetEmail($toEmail, $username, $resetToken) {
        try {
            error_log("Starting password reset email process for user: " . $username);
            error_log("Recipient email: " . $toEmail);
            
            // Remove token formatting to ensure exact match during verification
            $subject = "Password Reset Request - Banana Snake Game";
            $message = "
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .token-box { 
                            background-color: #fff; 
                            border: 2px solid #4CAF50; 
                            padding: 15px; 
                            margin: 20px 0; 
                            text-align: center;
                            font-family: monospace;
                            font-size: 16px;
                            letter-spacing: 1px;
                            border-radius: 5px;
                        }
                        .footer { text-align: center; padding: 20px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class='container'>
                        <div class='header'>
                            <h2>Password Reset Request</h2>
                        </div>
                        <div class='content'>
                            <p>Hello {$username},</p>
                            <p>We received a request to reset your password for your Banana Snake Game account.</p>
                            <p>Your password reset token is:</p>
                            <div class='token-box'>
                                {$resetToken}
                            </div>
                            <p>Please enter this token in the password reset form to set a new password.</p>
                            <p>This token will expire in 10 minutes.</p>
                            <p>If you didn't request this password reset, please ignore this email.</p>
                        </div>
                        <div class='footer'>
                            <p>This is an automated message, please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
            ";

            $mail = new PHPMailer(true);

            try {
                // Server settings
                $mail->SMTPDebug = SMTP::DEBUG_SERVER;
                $mail->Debugoutput = function($str, $level) {
                    error_log("PHPMailer Debug: " . $str);
                };
                
                $mail->isSMTP();
                $mail->Host = self::SMTP_HOST;
                $mail->SMTPAuth = true;
                $mail->Username = self::FROM_EMAIL;
                $mail->Password = self::EMAIL_PASSWORD;
                $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
                $mail->Port = self::SMTP_PORT;

                // Recipients
                $mail->setFrom(self::FROM_EMAIL, 'Banana Snake Game');
                $mail->addAddress($toEmail);
                $mail->addReplyTo(self::FROM_EMAIL);

                // Content
                $mail->isHTML(true);
                $mail->Subject = $subject;
                $mail->Body = $message;

                error_log("Attempting to send email...");
                $mail->send();
                error_log("Password reset email sent successfully to: " . $toEmail);
                return true;
            } catch (Exception $e) {
                error_log("Error sending password reset email: " . $e->getMessage());
                error_log("Error details: " . $e->getTraceAsString());
                error_log("PHPMailer error info: " . $mail->ErrorInfo);
                return false;
            }
        } catch (Exception $e) {
            error_log("Error sending password reset email: " . $e->getMessage());
            return false;
        }
    }
}
?>