<?php
// Prevent direct access to this file
if (!defined('SECURE_ACCESS')) {
    http_response_code(403);
    die('Direct access not permitted');
}

// Database configuration
define('DB_HOST', 'localhost');
define('DB_NAME', 'Your actual database name');  // Your actual database name
define('DB_USER', 'Your actual database username');            // Your actual database username
define('DB_PASS', 'our actual database password');             // Your actual database password

// JWT configuration
define('JWT_SECRET', 'your_very_secret_key');  // Change this to a secure random string

// Error reporting
define('DEBUG_MODE', false);  // Set to false in production

// Security settings
define('ALLOWED_ORIGINS', ['Add your domains']);  // Add your domains
define('TOKEN_EXPIRY', 3600);  // 1 hour in seconds
define('MAX_LOGIN_ATTEMPTS', 5);
define('LOGIN_TIMEOUT', 300);  // 5 minutes in seconds

// Rate limiting
define('RATE_LIMIT_REQUESTS', 100);    // Maximum requests per window
define('RATE_LIMIT_WINDOW', 3600);     // Time window in seconds (1 hour)

// Session security
define('SESSION_LIFETIME', 3600);      // 1 hour in seconds
define('REQUIRE_HTTPS', true);         // Require HTTPS for all requests
define('COOKIE_SECURE', true);         // Only send cookies over HTTPS
define('COOKIE_HTTPONLY', true);       // Prevent JavaScript access to cookies

// Password requirements
define('MIN_PASSWORD_LENGTH', 8);
define('PASSWORD_REQUIRE_SPECIAL', true);
define('PASSWORD_REQUIRE_NUMBERS', true);
define('PASSWORD_REQUIRE_UPPERCASE', true);

// API settings
define('API_VERSION', '1.0');
define('MAX_REQUEST_SIZE', 1048576);  // 1MB in bytes