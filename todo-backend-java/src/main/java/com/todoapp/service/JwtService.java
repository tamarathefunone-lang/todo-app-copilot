package com.todoapp.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Service for JWT token operations
 */
public class JwtService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    private static final String ISSUER = "todo-app";
    private static final String USER_ID_CLAIM = "userId";
    private static final String EMAIL_CLAIM = "email";
    private static final String FIRST_NAME_CLAIM = "firstName";
    private static final String LAST_NAME_CLAIM = "lastName";
    
    // Token expiration time in seconds (24 hours)
    private static final long TOKEN_EXPIRATION_SECONDS = 24 * 60 * 60;
    
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService() {
        String secretKey = System.getenv("JWT_SECRET_KEY");
        if (secretKey == null || secretKey.trim().isEmpty()) {
            // For development/testing - use a default key
            // In production, this should always come from environment variable or AWS Secrets Manager
            secretKey = "default-secret-key-for-development-only-change-in-production";
            logger.warn("Using default JWT secret key. This should be changed in production!");
        }
        
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    /**
     * Generate JWT token for user
     */
    public String generateToken(String userId, String email, String firstName, String lastName) {
        try {
            Instant now = Instant.now();
            Instant expiration = now.plus(TOKEN_EXPIRATION_SECONDS, ChronoUnit.SECONDS);
            
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(userId)
                    .withClaim(USER_ID_CLAIM, userId)
                    .withClaim(EMAIL_CLAIM, email)
                    .withClaim(FIRST_NAME_CLAIM, firstName)
                    .withClaim(LAST_NAME_CLAIM, lastName)
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(expiration))
                    .sign(algorithm);
            
            logger.info("JWT token generated for user: {}", userId);
            return token;
        } catch (JWTCreationException e) {
            logger.error("Error creating JWT token for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Validate and decode JWT token
     */
    public DecodedJWT validateToken(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            logger.info("JWT token validated successfully for user: {}", decodedJWT.getSubject());
            return decodedJWT;
        } catch (JWTVerificationException e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Extract user ID from token
     */
    public String getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return decodedJWT.getClaim(USER_ID_CLAIM).asString();
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract user ID from token", e);
        }
    }

    /**
     * Extract email from token
     */
    public String getEmailFromToken(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return decodedJWT.getClaim(EMAIL_CLAIM).asString();
        } catch (Exception e) {
            logger.error("Error extracting email from token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract email from token", e);
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Date expirationDate = decodedJWT.getExpiresAt();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage(), e);
            return true; // Consider expired if we can't parse it
        }
    }

    /**
     * Get token expiration time in seconds
     */
    public long getTokenExpirationSeconds() {
        return TOKEN_EXPIRATION_SECONDS;
    }

    /**
     * Extract all user info from token
     */
    public UserInfo getUserInfoFromToken(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return new UserInfo(
                    decodedJWT.getClaim(USER_ID_CLAIM).asString(),
                    decodedJWT.getClaim(EMAIL_CLAIM).asString(),
                    decodedJWT.getClaim(FIRST_NAME_CLAIM).asString(),
                    decodedJWT.getClaim(LAST_NAME_CLAIM).asString()
            );
        } catch (Exception e) {
            logger.error("Error extracting user info from token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract user info from token", e);
        }
    }

    /**
     * User information extracted from JWT token
     */
    public static class UserInfo {
        private final String userId;
        private final String email;
        private final String firstName;
        private final String lastName;

        public UserInfo(String userId, String email, String firstName, String lastName) {
            this.userId = userId;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "userId='" + userId + '\'' +
                    ", email='" + email + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }
}
