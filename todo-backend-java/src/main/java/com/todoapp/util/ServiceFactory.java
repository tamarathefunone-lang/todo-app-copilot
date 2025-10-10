package com.todoapp.util;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import com.todoapp.repository.UserRepository;
import com.todoapp.repository.TaskRepository;
import com.todoapp.service.AuthService;
import com.todoapp.service.JwtService;

/**
 * Factory for creating service instances
 */
public class ServiceFactory {
    
    private static DynamoDbEnhancedClient dynamoDbClient;
    private static UserRepository userRepository;
    private static TaskRepository taskRepository;
    private static JwtService jwtService;
    private static AuthService authService;

    /**
     * Get DynamoDB Enhanced Client
     */
    public static synchronized DynamoDbEnhancedClient getDynamoDbClient() {
        if (dynamoDbClient == null) {
            // Get region from environment variable or use default
            String regionName = System.getenv("AWS_REGION");
            if (regionName == null || regionName.trim().isEmpty()) {
                regionName = "us-east-1"; // Default region
            }
            
            Region region = Region.of(regionName);
            
            DynamoDbClient client = DynamoDbClient.builder()
                    .region(region)
                    .build();
            
            dynamoDbClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(client)
                    .build();
        }
        return dynamoDbClient;
    }

    /**
     * Get User Repository
     */
    public static synchronized UserRepository getUserRepository() {
        if (userRepository == null) {
            userRepository = new UserRepository(getDynamoDbClient());
        }
        return userRepository;
    }

    /**
     * Get Task Repository
     */
    public static synchronized TaskRepository getTaskRepository() {
        if (taskRepository == null) {
            taskRepository = new TaskRepository(getDynamoDbClient());
        }
        return taskRepository;
    }

    /**
     * Get JWT Service
     */
    public static synchronized JwtService getJwtService() {
        if (jwtService == null) {
            jwtService = new JwtService();
        }
        return jwtService;
    }

    /**
     * Get Auth Service
     */
    public static synchronized AuthService getAuthService() {
        if (authService == null) {
            authService = new AuthService(getUserRepository(), getJwtService());
        }
        return authService;
    }

    /**
     * Reset all instances (for testing)
     */
    public static synchronized void reset() {
        dynamoDbClient = null;
        userRepository = null;
        taskRepository = null;
        jwtService = null;
        authService = null;
    }
}
