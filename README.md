# BananaSanke - Server-Side

This branch contains the server-side code for the BananaSanke game. The server-side is hosted on a WordPress hosting environment, and the structure of the code is organized as follows:

## Directory Structure
- includes/ Core PHP includes and functionality
- public_html/ Public-facing web files
- config.php Configuration file

## If you are using your own hosting for this game, make sure to update the `config.php` and `EmailUtils.php` file with the correct configuration for your hosting environment.
## Also update the client side  `APIClient.java` file

### Instructions for Hosting

2. **Update the `APIClient.java` File**:
- This includes API url to the server-side.

```java
public static final String BASE_URL = "https://deshandulmina.info/api.php"; //Replace this with ur API URL
```


2. **Update the `config.php` File**:
   - This includes database credentials, server paths, and any other necessary settings.
   
Add the following configuration to your `config.php` file:

```php
// Database configuration
define('DB_HOST', 'localhost');
define('DB_NAME', 'Your actual database name');  // Your actual database name
define('DB_USER', 'Your actual database username');            // Your actual database username
define('DB_PASS', 'our actual database password');             // Your actual database password
```

3. **PHP Mailer**:
   ## PHP Mailer Configuration

The server-side uses **PHP Mailer** to send emails, such as password reset codes and account-related notifications. Follow the steps below to configure PHP Mailer for your hosting environment:

### Step 1: Install PHP Mailer
1. Download the latest version of PHP Mailer from [GitHub](https://github.com/PHPMailer/PHPMailer).
2. Place the PHP Mailer files in the `includes/` directory or a suitable location in your project.

```php
// Email Configuration
define('MAIL_HOST', 'smtp.example.com'); // Replace with your SMTP host
define('MAIL_USERNAME', 'your-email@example.com'); // Replace with your email address
define('MAIL_PASSWORD', 'your-email-password'); // Replace with your email password
define('MAIL_PORT', 587); // Replace with your SMTP port (e.g., 587 for TLS, 465 for SSL)
define('MAIL_FROM', 'your-email@example.com'); // Replace with the "From" email address
define('MAIL_FROM_NAME', 'BananaSanke Support'); // Replace with the sender's name


---

### Notes
- The `includes/` directory contains all the core PHP files and functionality required for the game.
- The `public_html/` directory contains the public-facing files, such as the login page, leaderboard, and game-related web pages.
- The `config.php` file is critical for setting up the server-side environment. Ensure it is properly configured before deploying the game.

---

Enjoy hosting and managing the server-side of BananaSanke!
