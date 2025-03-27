# BananaSanke - Server-Side

This branch contains the server-side code for the BananaSanke game. The server-side is hosted on a WordPress hosting environment, and the structure of the code is organized as follows:

## Directory Structure
- includes/ Core PHP includes and functionality
- public_html/ Public-facing web files
- config.php Configuration file


### Instructions for Hosting
1. **Update the `config.php` File**:
   - If you are using your own hosting for this game, make sure to update the `config.php` file with the correct configuration for your hosting environment.
   - This includes database credentials, server paths, and any other necessary settings.

2. **PHP Mailer**:
   - Ensure that the PHP Mailer library is updated to the latest version for secure and reliable email functionality.
   - The PHP Mailer is used for sending emails, such as password reset codes and account-related notifications.

---

### Notes
- The `includes/` directory contains all the core PHP files and functionality required for the game.
- The `public_html/` directory contains the public-facing files, such as the login page, leaderboard, and game-related web pages.
- The `config.php` file is critical for setting up the server-side environment. Ensure it is properly configured before deploying the game.

---

Enjoy hosting and managing the server-side of BananaSanke!
