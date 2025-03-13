<?php
// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);

// Define secure access constant
define('SECURE_ACCESS', true);

// Include configuration
$configPath = dirname($_SERVER['DOCUMENT_ROOT']) . '/config.php';
require_once $configPath;

try {
    // Connect to database
    $db = new PDO(
        'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4',
        DB_USER,
        DB_PASS,
        [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
    );

    // Check if users table exists
    $stmt = $db->query("SHOW TABLES LIKE 'users'");
    if (!$stmt->fetch()) {
        // Create users table
        $db->exec("CREATE TABLE users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            email VARCHAR(100) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL,
            high_score INT DEFAULT 0,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            INDEX idx_username (username),
            INDEX idx_email (email),
            INDEX idx_high_score (high_score)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
        
        echo "Users table created successfully\n";
    } else {
        echo "Users table already exists\n";
        
        // Check table structure
        $stmt = $db->query("DESCRIBE users");
        $columns = $stmt->fetchAll(PDO::FETCH_COLUMN);
        echo "Current columns: " . implode(', ', $columns) . "\n";
        
        // Check for missing columns
        $requiredColumns = ['id', 'username', 'email', 'password', 'high_score', 'created_at', 'updated_at'];
        $missingColumns = array_diff($requiredColumns, $columns);
        
        if (!empty($missingColumns)) {
            echo "Missing columns: " . implode(', ', $missingColumns) . "\n";
            
            // Add missing columns
            foreach ($missingColumns as $column) {
                switch ($column) {
                    case 'high_score':
                        $db->exec("ALTER TABLE users ADD COLUMN high_score INT DEFAULT 0");
                        break;
                    case 'created_at':
                        $db->exec("ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
                        break;
                    case 'updated_at':
                        $db->exec("ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
                        break;
                }
            }
            echo "Added missing columns\n";
        }
    }

    // Check indexes
    $stmt = $db->query("SHOW INDEX FROM users");
    $indexes = $stmt->fetchAll(PDO::FETCH_COLUMN, 2);
    echo "Current indexes: " . implode(', ', $indexes) . "\n";

    $requiredIndexes = ['username', 'email', 'high_score'];
    $missingIndexes = array_diff($requiredIndexes, $indexes);

    if (!empty($missingIndexes)) {
        echo "Missing indexes: " . implode(', ', $missingIndexes) . "\n";
        
        // Add missing indexes
        foreach ($missingIndexes as $index) {
            $db->exec("CREATE INDEX idx_{$index} ON users ({$index})");
        }
        echo "Added missing indexes\n";
    }

    echo "Database check completed successfully\n";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
    echo "File: " . $e->getFile() . "\n";
    echo "Line: " . $e->getLine() . "\n";
} 