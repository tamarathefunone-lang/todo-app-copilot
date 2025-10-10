package com.todoapp.repository;

import com.todoapp.model.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for User operations with DynamoDB
 */
public class UserRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final String TABLE_NAME = "Users";
    
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<User> userTable;

    public UserRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.userTable = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(User.class));
    }

    /**
     * Save a user to DynamoDB
     */
    public User save(User user) {
        try {
            logger.info("Saving user with ID: {}", user.getUserId());
            userTable.putItem(user);
            logger.info("User saved successfully: {}", user.getUserId());
            return user;
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(String userId) {
        try {
            logger.info("Finding user by ID: {}", userId);
            Key key = Key.builder().partitionValue(userId).build();
            User user = userTable.getItem(key);
            
            if (user != null && user.isActive()) {
                logger.info("User found: {}", userId);
                return Optional.of(user);
            } else {
                logger.info("User not found or inactive: {}", userId);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error finding user by ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to find user by ID", e);
        }
    }

    /**
     * Find user by email using scan operation
     * Note: This is not efficient for large datasets. Consider using GSI for production.
     */
    public Optional<User> findByEmail(String email) {
        try {
            logger.info("Finding user by email: {}", email);
            
            // Using scan with filter expression
            Expression filterExpression = Expression.builder()
                .expression("email = :email AND isActive = :active")
                .putExpressionValue(":email", AttributeValue.builder().s(email).build())
                .putExpressionValue(":active", AttributeValue.builder().bool(true).build())
                .build();
            
            List<User> users = userTable.scan(builder -> builder
                .filterExpression(filterExpression)
            ).items().stream().collect(Collectors.toList());

            if (!users.isEmpty()) {
                logger.info("User found by email: {}", email);
                return Optional.of(users.get(0));
            } else {
                logger.info("User not found by email: {}", email);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to find user by email", e);
        }
    }

    /**
     * Update user
     */
    public User update(User user) {
        try {
            logger.info("Updating user: {}", user.getUserId());
            user.updateTimestamp();
            userTable.putItem(user);
            logger.info("User updated successfully: {}", user.getUserId());
            return user;
        } catch (Exception e) {
            logger.error("Error updating user {}: {}", user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    /**
     * Soft delete user by marking as inactive
     */
    public void delete(String userId) {
        try {
            logger.info("Soft deleting user: {}", userId);
            Optional<User> userOpt = findById(userId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setActive(false);
                user.updateTimestamp();
                userTable.putItem(user);
                logger.info("User soft deleted successfully: {}", userId);
            } else {
                logger.warn("User not found for deletion: {}", userId);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        try {
            return findByEmail(email).isPresent();
        } catch (Exception e) {
            logger.error("Error checking if email exists {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to check email existence", e);
        }
    }

    /**
     * Get all active users (for admin purposes)
     */
    public List<User> findAllActive() {
        try {
            logger.info("Finding all active users");
            
            Expression filterExpression = Expression.builder()
                .expression("isActive = :active")
                .putExpressionValue(":active", AttributeValue.builder().bool(true).build())
                .build();
            
            return userTable.scan(builder -> builder
                .filterExpression(filterExpression)
            ).items().stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding all active users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find all active users", e);
        }
    }
}
