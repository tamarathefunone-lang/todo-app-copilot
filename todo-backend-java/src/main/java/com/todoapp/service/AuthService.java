package com.todoapp.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.LoginRequest;
import com.todoapp.dto.RegisterRequest;
import com.todoapp.model.User;
import com.todoapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for user authentication operations
 */
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    // BCrypt cost factor (higher is more secure but slower)
    private static final int BCRYPT_COST = 12;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        try {
            logger.info("Attempting to register user with email: {}", request.getEmail());
            
            // Check if user already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Registration failed - email already exists: {}", request.getEmail());
                throw new RuntimeException("Email already registered");
            }
            
            // Generate user ID
            String userId = UUID.randomUUID().toString();
            
            // Hash password
            String passwordHash = hashPassword(request.getPassword());
            
            // Create user
            User user = new User(
                    userId,
                    request.getEmail(),
                    passwordHash,
                    request.getFirstName(),
                    request.getLastName()
            );
            
            // Save user
            userRepository.save(user);
            
            // Generate JWT token
            String token = jwtService.generateToken(
                    user.getUserId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
            
            logger.info("User registered successfully: {}", userId);
            
            return new AuthResponse(
                    token,
                    user.getUserId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    jwtService.getTokenExpirationSeconds()
            );
            
        } catch (Exception e) {
            logger.error("Registration failed for email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Authenticate user login
     */
    public AuthResponse login(LoginRequest request) {
        try {
            logger.info("Attempting to login user with email: {}", request.getEmail());
            
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            
            if (!userOpt.isPresent()) {
                logger.warn("Login failed - user not found: {}", request.getEmail());
                throw new RuntimeException("Invalid email or password");
            }
            
            User user = userOpt.get();
            
            // Verify password
            if (!verifyPassword(request.getPassword(), user.getPasswordHash())) {
                logger.warn("Login failed - invalid password for user: {}", request.getEmail());
                throw new RuntimeException("Invalid email or password");
            }
            
            // Check if user is active
            if (!user.isActive()) {
                logger.warn("Login failed - user account is inactive: {}", request.getEmail());
                throw new RuntimeException("Account is inactive");
            }
            
            // Generate JWT token
            String token = jwtService.generateToken(
                    user.getUserId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
            
            // Update last login timestamp
            user.updateTimestamp();
            userRepository.update(user);
            
            logger.info("User logged in successfully: {}", user.getUserId());
            
            return new AuthResponse(
                    token,
                    user.getUserId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    jwtService.getTokenExpirationSeconds()
            );
            
        } catch (Exception e) {
            logger.error("Login failed for email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validate JWT token and return user info
     */
    public JwtService.UserInfo validateToken(String token) {
        try {
            logger.info("Validating JWT token");
            
            // Remove Bearer prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // Validate token and extract user info
            JwtService.UserInfo userInfo = jwtService.getUserInfoFromToken(token);
            
            // Verify user still exists and is active
            Optional<User> userOpt = userRepository.findById(userInfo.getUserId());
            if (!userOpt.isPresent() || !userOpt.get().isActive()) {
                logger.warn("Token validation failed - user not found or inactive: {}", userInfo.getUserId());
                throw new RuntimeException("Invalid token - user not found or inactive");
            }
            
            logger.info("Token validated successfully for user: {}", userInfo.getUserId());
            return userInfo;
            
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token: " + e.getMessage(), e);
        }
    }

    /**
     * Change user password
     */
    public void changePassword(String userId, String oldPassword, String newPassword) {
        try {
            logger.info("Attempting to change password for user: {}", userId);
            
            // Find user
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                logger.warn("Password change failed - user not found: {}", userId);
                throw new RuntimeException("User not found");
            }
            
            User user = userOpt.get();
            
            // Verify old password
            if (!verifyPassword(oldPassword, user.getPasswordHash())) {
                logger.warn("Password change failed - invalid old password for user: {}", userId);
                throw new RuntimeException("Invalid old password");
            }
            
            // Hash new password
            String newPasswordHash = hashPassword(newPassword);
            
            // Update user
            user.setPasswordHash(newPasswordHash);
            user.updateTimestamp();
            userRepository.update(user);
            
            logger.info("Password changed successfully for user: {}", userId);
            
        } catch (Exception e) {
            logger.error("Password change failed for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Password change failed: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivate user account
     */
    public void deactivateAccount(String userId) {
        try {
            logger.info("Attempting to deactivate account for user: {}", userId);
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                logger.warn("Account deactivation failed - user not found: {}", userId);
                throw new RuntimeException("User not found");
            }
            
            User user = userOpt.get();
            user.setActive(false);
            user.updateTimestamp();
            userRepository.update(user);
            
            logger.info("Account deactivated successfully for user: {}", userId);
            
        } catch (Exception e) {
            logger.error("Account deactivation failed for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Account deactivation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Hash password using BCrypt
     */
    private String hashPassword(String password) {
        try {
            return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
        } catch (Exception e) {
            logger.error("Error hashing password: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * Verify password against hash using BCrypt
     */
    private boolean verifyPassword(String password, String hash) {
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
            return result.verified;
        } catch (Exception e) {
            logger.error("Error verifying password: {}", e.getMessage(), e);
            return false;
        }
    }
}
