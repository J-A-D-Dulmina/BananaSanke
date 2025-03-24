<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);

// Log errors to a file
ini_set('log_errors', 1);
ini_set('error_log', dirname(__FILE__) . '/error.log');

// Define secure access constant
define('SECURE_ACCESS', true);

try {
    // Include configuration from outside public_html
    $configPath = dirname($_SERVER['DOCUMENT_ROOT']) . '/config.php';
    if (!file_exists($configPath)) {
        throw new Exception('Configuration file not found at: ' . $configPath);
    }
    require_once $configPath;

    // Include JWT implementation from secure directory
    $jwtPath = dirname($_SERVER['DOCUMENT_ROOT']) . '/includes/JWT.php';
    if (!file_exists($jwtPath)) {
        throw new Exception('JWT implementation not found at: ' . $jwtPath);
    }
    require_once $jwtPath;

    // Create user_sessions table if it doesn't exist
    try {
        $db = new PDO(
            'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4',
            DB_USER,
            DB_PASS,
            [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4"
            ]
        );

        // Create user_sessions table
        $db->exec('
            CREATE TABLE IF NOT EXISTS user_sessions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                token VARCHAR(255) NOT NULL,
                is_active TINYINT(1) DEFAULT 1,
                created_at DATETIME NOT NULL,
                expires_at DATETIME NOT NULL,
                logged_out_at DATETIME DEFAULT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                UNIQUE KEY unique_active_session (user_id, is_active)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        ');

        // Clean up expired sessions
        $db->exec('
            UPDATE user_sessions 
            SET is_active = 0, logged_out_at = NOW() 
            WHERE expires_at < NOW() AND is_active = 1
        ');

    } catch (PDOException $e) {
        error_log('Database connection error: ' . $e->getMessage());
        throw new Exception('Database connection failed');
    }

    // Set security headers
    header('Content-Type: application/json');
    header('Access-Control-Allow-Origin: *');  // Temporarily allow all origins for testing
    header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization');

    // Handle preflight OPTIONS request
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        http_response_code(200);
        exit();
    }

    // Database connection
    try {
        $db = new PDO(
            'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4',
            DB_USER,
            DB_PASS,
            [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4"
            ]
        );
    } catch (PDOException $e) {
        error_log('Database connection error: ' . $e->getMessage());
        throw new Exception('Database connection failed');
    }

    // Get POST data from multiple sources
    $postData = [];
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        // Try to get POST data
        $postData = $_POST;
        
        // Try to get JSON data
        $rawInput = file_get_contents('php://input');
        error_log('Raw input received: ' . $rawInput);  // Log raw input
        
        if (!empty($rawInput)) {
            $jsonData = json_decode($rawInput, true);
            if (json_last_error() === JSON_ERROR_NONE && is_array($jsonData)) {
                $postData = array_merge($postData, $jsonData);
            } else {
                error_log('JSON decode error: ' . json_last_error_msg());
                // Try to parse URL-encoded data
                parse_str($rawInput, $parsedData);
                if (!empty($parsedData)) {
                    $postData = array_merge($postData, $parsedData);
                }
            }
        }
        
        error_log('Processed POST data: ' . print_r($postData, true));
    }

    $action = isset($_GET['action']) ? $_GET['action'] : '';
    error_log('Requested action: ' . $action);

    // Handle different actions
    switch ($action) {
        case 'register_user':
            if (empty($postData)) {
                throw new Exception('No data received for registration');
            }

            if (!isset($postData['username']) || !isset($postData['email']) || !isset($postData['password'])) {
                throw new Exception('All fields are required. Received: ' . 
                    'username=' . (isset($postData['username']) ? 'yes' : 'no') . ', ' .
                    'email=' . (isset($postData['email']) ? 'yes' : 'no') . ', ' .
                    'password=' . (isset($postData['password']) ? 'yes' : 'no'));
            }

            // Validate input
            $username = trim($postData['username']);
            $email = trim($postData['email']);
            $password = trim($postData['password']);

            if (strlen($username) < 3 || strlen($username) > 50) {
                throw new Exception('Username must be between 3 and 50 characters');
            }

            if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
                throw new Exception('Invalid email format');
            }

            if (strlen($password) < 6) {
                throw new Exception('Password must be at least 6 characters');
            }

            // Check if email exists
            $stmt = $db->prepare('SELECT id FROM users WHERE email = ?');
            $stmt->execute([$email]);
            if ($stmt->fetch()) {
                throw new Exception('Email already registered');
            }

            // Check if username exists
            $stmt = $db->prepare('SELECT id FROM users WHERE username = ?');
            $stmt->execute([$username]);
            if ($stmt->fetch()) {
                throw new Exception('Username already taken');
            }

            // Insert new user
            $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
            $stmt = $db->prepare('INSERT INTO users (username, email, password) VALUES (?, ?, ?)');
            $stmt->execute([$username, $email, $hashedPassword]);
            
            $userId = $db->lastInsertId();

            echo json_encode([
                'status' => 'success',
                'message' => 'Registration successful',
                'user_id' => $userId
            ]);
            break;

        case 'login_user':
            if (!isset($postData['email']) || !isset($postData['password'])) {
                http_response_code(400);
                echo json_encode(['status' => 'error', 'message' => 'Email and password are required']);
                exit();
            }
    
            try {
                // Start transaction
                $db->beginTransaction();
                
                // Check for existing active session
                $stmt = $db->prepare('
                    SELECT u.id, u.username, u.email, u.password, s.token 
                    FROM users u 
                    LEFT JOIN user_sessions s ON u.id = s.user_id 
                    WHERE u.email = ? AND s.is_active = 1
                ');
                $stmt->execute([$postData['email']]);
                $existingSession = $stmt->fetch(PDO::FETCH_ASSOC);
                
                if ($existingSession) {
                    $db->rollBack();
                    http_response_code(401);
                    echo json_encode([
                        'status' => 'error', 
                        'message' => 'User is already logged in on another device'
                    ]);
                    exit();
                }
                
                // Get user data
                $stmt = $db->prepare('SELECT id, username, email, password FROM users WHERE email = ?');
                $stmt->execute([$postData['email']]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);

                if (!$user || !password_verify($postData['password'], $user['password'])) {
                    $db->rollBack();
                    http_response_code(401);
                    echo json_encode(['status' => 'error', 'message' => 'Invalid email or password']);
                    exit();
                }

                // Generate JWT token
                $payload = [
                    'user_id' => $user['id'],
                    'email' => $user['email'],
                    'exp' => time() + (60 * 60) // Token expires in 1 hour
                ];

                $token = JWT::encode($payload, JWT_SECRET);
                
                // Store session in database
                $stmt = $db->prepare('
                    INSERT INTO user_sessions (user_id, token, is_active, created_at, expires_at) 
                    VALUES (?, ?, 1, NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR))
                ');
                $stmt->execute([$user['id'], $token]);

                // Commit transaction
                $db->commit();

                echo json_encode([
                    'status' => 'success',
                    'auth_token' => $token,
                    'username' => $user['username'],
                    'user' => [
                        'id' => $user['id'],
                        'username' => $user['username'],
                        'email' => $user['email']
                    ]
                ]);
            } catch (Exception $e) {
                $db->rollBack();
                http_response_code(500);
                echo json_encode(['status' => 'error', 'message' => 'Login failed']);
                if (DEBUG_MODE) {
                    error_log($e->getMessage());
                }
            }
            break;

        case 'logout_user':
            try {
                // Get the token from Authorization header
                $headers = getallheaders();
                $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : $headers['authorization'];
                
                if (empty($authHeader) || !preg_match('/Bearer\s+(.*)$/i', $authHeader, $matches)) {
                    throw new Exception('Invalid token format');
                }
                
                $token = $matches[1];
                
                // Verify token and get user data
                $user = verifyToken();
                
                // Start transaction
                $db->beginTransaction();
                
                // Deactivate the session with the current token
                $stmt = $db->prepare('
                    UPDATE user_sessions 
                    SET is_active = 0, logged_out_at = NOW() 
                    WHERE user_id = ? AND token = ?
                ');
                $stmt->execute([$user->user_id, $token]);
                
                // If no session was found with the current token, try to deactivate any active session
                if ($stmt->rowCount() === 0) {
                    $stmt = $db->prepare('
                        UPDATE user_sessions 
                        SET is_active = 0, logged_out_at = NOW() 
                        WHERE user_id = ? AND is_active = 1
                        LIMIT 1
                    ');
                    $stmt->execute([$user->user_id]);
                }
                
                // Commit transaction
                $db->commit();
                
                echo json_encode(['status' => 'success', 'message' => 'Logged out successfully']);
            } catch (Exception $e) {
                if ($db->inTransaction()) {
                    $db->rollBack();
                }
                http_response_code(500);
                echo json_encode(['status' => 'error', 'message' => 'Logout failed: ' . $e->getMessage()]);
                if (DEBUG_MODE) {
                    error_log($e->getMessage());
                }
            }
            break;

        case 'update_score':
            try {
                // Log the incoming request
                error_log('Updating score - Raw request data: ' . print_r($postData, true));
                
                // Verify token and get user data
                $user = verifyToken();
                error_log('User verified from token: ' . print_r($user, true));

                if (!isset($postData['score'])) {
                    throw new Exception('Score field is missing');
                }

                // Ensure score is numeric and convert to integer
                if (!is_numeric($postData['score'])) {
                    throw new Exception('Score must be a number, received: ' . gettype($postData['score']));
                }

                $score = intval($postData['score']);
                error_log('Processing score update - User ID: ' . $user->user_id . ', New score: ' . $score);

                if ($score < 0) {
                    throw new Exception('Score cannot be negative');
                }

                // Get current high score
                $stmt = $db->prepare('SELECT high_score FROM users WHERE id = ?');
                $stmt->execute([$user->user_id]);
                $currentScore = $stmt->fetch(PDO::FETCH_ASSOC);
                error_log('Current high score: ' . ($currentScore ? $currentScore['high_score'] : 'none'));

                // Update only if new score is higher
                if (!$currentScore || $score > $currentScore['high_score']) {
                    $stmt = $db->prepare('UPDATE users SET high_score = ? WHERE id = ?');
                    $stmt->execute([$score, $user->user_id]);
                    $affected = $stmt->rowCount();
                    error_log('Score updated. Affected rows: ' . $affected);

                    echo json_encode([
                        'status' => 'success',
                        'message' => 'High score updated successfully',
                        'new_high_score' => $score,
                        'previous_high_score' => $currentScore ? $currentScore['high_score'] : 0
                    ]);
                } else {
                    echo json_encode([
                        'status' => 'success',
                        'message' => 'Score not high enough to update high score',
                        'current_score' => $score,
                        'high_score' => $currentScore['high_score']
                    ]);
                }
            } catch (Exception $e) {
                error_log('Error updating score: ' . $e->getMessage());
                http_response_code(500);
                echo json_encode([
                    'status' => 'error',
                    'message' => $e->getMessage(),
                    'debug' => DEBUG_MODE ? [
                        'file' => $e->getFile(),
                        'line' => $e->getLine(),
                        'error' => $e->getMessage()
                    ] : null
                ]);
            }
            break;

        case 'get_leaderboard':
            try {
                // Verify token and get user data
                $user = verifyToken();
                
                // Start transaction
                $db->beginTransaction();
                
                try {
                    // First get total number of players with scores
                    $countStmt = $db->prepare('SELECT COUNT(*) as total FROM users WHERE high_score > 0');
                    $countStmt->execute();
                    $totalPlayers = (int)$countStmt->fetch(PDO::FETCH_ASSOC)['total'];

                    // Get leaderboard data with rank calculation
                    $stmt = $db->prepare('
                        SELECT 
                            u1.id,
                            u1.username,
                            u1.high_score as score,
                            u1.created_at,
                            (SELECT COUNT(*) 
                             FROM users u2 
                             WHERE u2.high_score > u1.high_score 
                             OR (u2.high_score = u1.high_score AND u2.created_at < u1.created_at)
                            ) + 1 as `rank`
                        FROM users u1
                        WHERE u1.high_score > 0
                        ORDER BY u1.high_score DESC, u1.created_at ASC
                        LIMIT 20
                    ');
                    
                    if (!$stmt->execute()) {
                        throw new Exception('Failed to execute leaderboard query');
                    }
                    
                    $scores = $stmt->fetchAll(PDO::FETCH_ASSOC);
                    
                    if ($scores === false) {
                        throw new Exception('Failed to fetch leaderboard data');
                    }
                    
                    // Get current user's rank and score
                    $userRank = null;
                    $userScore = 0;
                    
                    if ($user && $user->user_id) {
                        $rankStmt = $db->prepare('
                            SELECT 
                                high_score,
                                created_at,
                                (SELECT COUNT(*) 
                                 FROM users u2 
                                 WHERE u2.high_score > u1.high_score 
                                 OR (u2.high_score = u1.high_score AND u2.created_at < u1.created_at)
                                ) + 1 as `rank`
                            FROM users u1
                            WHERE id = ?
                        ');
                        
                        if (!$rankStmt->execute([$user->user_id])) {
                            throw new Exception('Failed to fetch user rank');
                        }
                        
                        $userInfo = $rankStmt->fetch(PDO::FETCH_ASSOC);
                        if ($userInfo) {
                            $userRank = (int)$userInfo['rank'];
                            $userScore = (int)$userInfo['high_score'];
                        }
                    }
                    
                    // Commit transaction
                    $db->commit();
                    
                    // Format and send response
                    echo json_encode([
                        'status' => 'success',
                        'scores' => array_map(function($score) {
                            return [
                                'id' => (int)$score['id'],
                                'username' => $score['username'],
                                'score' => (int)$score['score'],
                                'rank' => (int)$score['rank'],
                                'created_at' => $score['created_at']
                            ];
                        }, $scores),
                        'user_rank' => $userRank,
                        'user_score' => $userScore,
                        'total_players' => $totalPlayers
                    ]);
                    
                } catch (Exception $e) {
                    // Rollback transaction on error
                    $db->rollBack();
                    throw $e;
                }
                
            } catch (Exception $e) {
                error_log('Error in get_leaderboard: ' . $e->getMessage());
                http_response_code(500);
                echo json_encode([
                    'status' => 'error',
                    'message' => 'Failed to fetch leaderboard: ' . $e->getMessage()
                ]);
            }
            break;

        case 'get_best_score':
            try {
                // Verify token and get user data
                $user = verifyToken();
                error_log('User ID from token: ' . $user->user_id); // Debug log
                
                // Get user's best score
                $stmt = $db->prepare('SELECT high_score, username FROM users WHERE id = ?');
                $stmt->execute([$user->user_id]);
                $result = $stmt->fetch(PDO::FETCH_ASSOC);
                
                error_log('Query result: ' . print_r($result, true)); // Debug log
                
                if ($result) {
            echo json_encode([
                        'status' => 'success',
                        'best_score' => (int)($result['high_score'] ?? 0),
                        'username' => $result['username']
            ]);
        } else {
                    error_log('No user found with ID: ' . $user->user_id);
            echo json_encode([
                        'status' => 'error',
                        'message' => 'User not found'
                    ]);
                }
            } catch (Exception $e) {
                error_log('Error in get_best_score: ' . $e->getMessage());
                error_log('Stack trace: ' . $e->getTraceAsString());
                http_response_code(500);
                echo json_encode([
                    'status' => 'error',
                    'message' => 'Failed to fetch best score: ' . $e->getMessage(),
                    'debug' => DEBUG_MODE ? [
                        'error' => $e->getMessage(),
                        'file' => $e->getFile(),
                        'line' => $e->getLine(),
                        'trace' => $e->getTraceAsString()
                    ] : null
                ]);
            }
            break;

        case 'update_username':
            try {
                // Verify token and get user data
                $user = verifyToken();
                
                if (!isset($postData['new_username'])) {
                    throw new Exception('New username is required');
                }

                $newUsername = trim($postData['new_username']);
                
                // Validate username
                if (strlen($newUsername) < 3 || strlen($newUsername) > 50) {
                    throw new Exception('Username must be between 3 and 50 characters');
                }

                // Check if username is already taken
                $stmt = $db->prepare('SELECT id FROM users WHERE username = ? AND id != ?');
                $stmt->execute([$newUsername, $user->user_id]);
                if ($stmt->fetch()) {
                    throw new Exception('Username already taken');
                }

                // Update username
                $stmt = $db->prepare('UPDATE users SET username = ? WHERE id = ?');
                $stmt->execute([$newUsername, $user->user_id]);

                if ($stmt->rowCount() > 0) {
        echo json_encode([
                        'status' => 'success',
                        'message' => 'Username updated successfully',
                        'new_username' => $newUsername
        ]);
    } else {
                    throw new Exception('Failed to update username');
                }
            } catch (Exception $e) {
                error_log('Error updating username: ' . $e->getMessage());
                http_response_code(500);
        echo json_encode([
                    'status' => 'error',
                    'message' => $e->getMessage()
                ]);
            }
            break;

        case 'update_password':
            try {
                // Verify token and get user data
                $tokenData = verifyToken();
                $userId = $tokenData->user_id;
                
                // Get request data
                $oldPassword = $_POST['old_password'] ?? '';
                $newPassword = $_POST['new_password'] ?? '';
                
                // Validate input
                if (empty($oldPassword) || empty($newPassword)) {
                    throw new Exception('Old and new passwords are required');
                }
                
                if (strlen($newPassword) < 6) {
                    throw new Exception('New password must be at least 6 characters long');
                }
                
                // Verify old password
                $stmt = $db->prepare('SELECT password FROM users WHERE id = ?');
                $stmt->execute([$userId]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);
                
                if (!$user || !password_verify($oldPassword, $user['password'])) {
                    throw new Exception('Current password is incorrect');
                }
                
                // Update password
                $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);
                $stmt = $db->prepare('UPDATE users SET password = ? WHERE id = ?');
                $stmt->execute([$hashedPassword, $userId]);
                
                echo json_encode(['status' => 'success', 'message' => 'Password updated successfully']);
                
            } catch (Exception $e) {
                http_response_code(400);
                echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
            }
            break;

        case 'request_password_reset':
            try {
                if (!isset($postData['username']) || !isset($postData['email'])) {
                    throw new Exception('Username and email are required');
                }

                $username = trim($postData['username']);
                $email = trim($postData['email']);

                // Check if user exists and email matches
                $stmt = $db->prepare('SELECT id FROM users WHERE username = ? AND email = ?');
                $stmt->execute([$username, $email]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);
                
                if (!$user) {
                    throw new Exception('Invalid username or email combination');
                }

                // Generate reset token
                $resetToken = str_pad(rand(0, 999999), 6, '0', STR_PAD_LEFT);
                $expiryTime = date('Y-m-d H:i:s', strtotime('+10 minutes'));

                // Store token in database
                $stmt = $db->prepare('
                    UPDATE users 
                    SET reset_token = ?, 
                        reset_token_expiry = ?
                    WHERE username = ?
                ');
                $stmt->execute([$resetToken, $expiryTime, $username]);

                // Send email
                require_once __DIR__ . '/utils/EmailUtils.php';
                if (EmailUtils::sendPasswordResetEmail($email, $username, $resetToken)) {
                    echo json_encode([
                        'status' => 'success',
                        'message' => 'Password reset instructions sent to your email'
                    ]);
                } else {
                    throw new Exception('Failed to send reset email');
                }
            } catch (Exception $e) {
                http_response_code(400);
                echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
            }
            break;

        case 'verify_reset_token':
            try {
                if (!isset($postData['username']) || !isset($postData['token']) || !isset($postData['new_password'])) {
                    throw new Exception('Username, token, and new password are required');
                }

                $username = trim($postData['username']);
                $token = trim($postData['token']);
                $newPassword = trim($postData['new_password']);

                // Verify token and check expiry
                $stmt = $db->prepare('
                    SELECT id, reset_token, reset_token_expiry 
                    FROM users 
                    WHERE username = ?
                ');
                $stmt->execute([$username]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);

                if (!$user) {
                    throw new Exception('User not found');
                }

                if (!$user['reset_token'] || !$user['reset_token_expiry']) {
                    throw new Exception('No active reset token found');
                }

                if (strtotime($user['reset_token_expiry']) < time()) {
                    throw new Exception('Reset token has expired');
                }

                if ($user['reset_token'] !== $token) {
                    throw new Exception('Invalid reset token');
                }

                // Update password and clear token
                $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);
                $stmt = $db->prepare('
                    UPDATE users 
                    SET password = ?,
                        reset_token = NULL,
                        reset_token_expiry = NULL
                    WHERE username = ?
                ');
                $stmt->execute([$hashedPassword, $username]);

                echo json_encode([
                    'status' => 'success',
                    'message' => 'Password reset successful'
                ]);
            } catch (Exception $e) {
                http_response_code(400);
                echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
            }
            break;

        case 'clear_reset_token':
            try {
                if (!isset($postData['username'])) {
                    throw new Exception('Username is required');
                }

                $username = trim($postData['username']);

                // Clear the last_token_sent timestamp
                $stmt = $db->prepare('
                    UPDATE users 
                    SET last_token_sent = NULL
                    WHERE username = ?
                ');
                $stmt->execute([$username]);

                echo json_encode([
                    'status' => 'success',
                    'message' => 'Reset token cleared'
                ]);
            } catch (Exception $e) {
                http_response_code(400);
                echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
            }
            break;

        default:
            http_response_code(400);
            echo json_encode(['status' => 'error', 'message' => 'Invalid action']);
            break;
    }

} catch (Exception $e) {
    error_log('Error in api.php: ' . $e->getMessage());
    http_response_code(500);
        echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'debug' => DEBUG_MODE ? [
            'file' => $e->getFile(),
            'line' => $e->getLine(),
            'trace' => $e->getTraceAsString()
        ] : null
    ]);
}

/**
 * Verify JWT token from Authorization header
 */
function verifyToken() {
    $headers = getallheaders();
    error_log('Received headers: ' . print_r($headers, true));

    if (!isset($headers['Authorization']) && !isset($headers['authorization'])) {
        error_log('No Authorization header found');
        throw new Exception('No token provided');
    }

    // Try both cases of the Authorization header
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : $headers['authorization'];
    error_log('Authorization header: ' . $authHeader);

    if (empty($authHeader) || !preg_match('/Bearer\s+(.*)$/i', $authHeader, $matches)) {
        error_log('Invalid Authorization header format');
        throw new Exception('Invalid token format');
    }

    $token = $matches[1];
    error_log('Extracted token: ' . substr($token, 0, 10) . '...');

    try {
        $decoded = JWT::decode($token, JWT_SECRET);
        error_log('Token decoded successfully: ' . print_r($decoded, true));
        
        // Verify token expiration
        if (isset($decoded->exp) && $decoded->exp < time()) {
            error_log('Token has expired. Expiration: ' . date('Y-m-d H:i:s', $decoded->exp));
            throw new Exception('Token has expired');
        }
        
        return $decoded;
    } catch (Exception $e) {
        error_log('Token verification failed: ' . $e->getMessage());
        throw new Exception('Invalid token: ' . $e->getMessage());
    }
}
?> 
