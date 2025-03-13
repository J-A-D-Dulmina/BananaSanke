<?php
/**
 * Simple JWT implementation for PHP
 * This class provides basic JWT functionality without external dependencies
 */
class JWT {
    /**
     * Encode a payload into a JWT string
     */
    public static function encode($payload, $key, $alg = 'HS256') {
        // Create token header
        $header = json_encode([
            'typ' => 'JWT',
            'alg' => $alg
        ]);

        // Encode Header
        $base64UrlHeader = self::base64UrlEncode($header);

        // Encode Payload
        $base64UrlPayload = self::base64UrlEncode(json_encode($payload));

        // Create Signature
        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $key, true);
        $base64UrlSignature = self::base64UrlEncode($signature);

        // Create JWT
        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }

    /**
     * Decode a JWT string into a PHP object
     */
    public static function decode($jwt, $key) {
        // Split the token
        $tokenParts = explode('.', $jwt);
        if (count($tokenParts) != 3) {
            throw new Exception('Invalid token format');
        }

        list($base64UrlHeader, $base64UrlPayload, $base64UrlSignature) = $tokenParts;

        // Decode the header
        $header = json_decode(self::base64UrlDecode($base64UrlHeader));
        if (!$header) {
            throw new Exception('Invalid header encoding');
        }

        // Decode the payload
        $payload = json_decode(self::base64UrlDecode($base64UrlPayload));
        if (!$payload) {
            throw new Exception('Invalid payload encoding');
        }

        // Verify signature
        $signature = self::base64UrlDecode($base64UrlSignature);
        $expectedSignature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $key, true);
        
        if (!hash_equals($signature, $expectedSignature)) {
            throw new Exception('Signature verification failed');
        }

        // Check if token has expired
        if (isset($payload->exp) && time() >= $payload->exp) {
            throw new Exception('Token has expired');
        }

        return $payload;
    }

    /**
     * Encode data to Base64URL
     */
    private static function base64UrlEncode($data) {
        $base64 = base64_encode($data);
        return str_replace(['+', '/', '='], ['-', '_', ''], $base64);
    }

    /**
     * Decode data from Base64URL
     */
    private static function base64UrlDecode($data) {
        $base64 = str_replace(['-', '_'], ['+', '/'], $data);
        $pad = strlen($base64) % 4;
        if ($pad) {
            $base64 .= str_repeat('=', 4 - $pad);
        }
        return base64_decode($base64);
    }
} 