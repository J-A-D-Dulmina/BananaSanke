# Server Reference Code

This directory contains reference code for the server-side implementation of the BananaSnake game API.

## Purpose
- Provides a reference for the server-side API endpoints and their functionality
- Documents the expected request/response formats
- Helps with debugging client-server interactions

## API Endpoints

### Authentication
- `login_user`: User login with email and password
- `logout_user`: User logout with session token
- `register_user`: New user registration

### Game Features
- `get_top_scores`: Retrieve top 20 scores with usernames and dates
- `update_high_score`: Update user's high score
- `get_high_score`: Get current user's high score

### User Management
- `get_users`: Get list of users (protected)
- `reset_password`: Reset user password

## Important Notes
- This is a reference implementation only
- Contains sensitive information (database credentials) - DO NOT deploy as is
- Used for development and debugging purposes
- Actual server implementation may vary

## Database Schema
The API expects the following tables:
- `users`: Stores user information and authentication
- `scores`: Stores game scores with timestamps 