package com.todoapp.lambda.auth;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.RegisterRequest;
import com.todoapp.service.AuthService;
import com.todoapp.util.LambdaUtils;
import com.todoapp.util.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lambda function for user registration
 */
public class RegisterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);
    
    private final AuthService authService;

    public RegisterHandler() {
        this.authService = ServiceFactory.getAuthService();
    }

    // Constructor for testing
    public RegisterHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        logger.info("Processing register request");
        
        return LambdaUtils.handleRequest(request, this::processRegister);
    }

    private APIGatewayProxyResponseEvent processRegister(APIGatewayProxyRequestEvent request) {
        try {
            // Parse request body
            RegisterRequest registerRequest = LambdaUtils.parseRequestBody(request, RegisterRequest.class);
            
            logger.info("Registering user with email: {}", registerRequest.getEmail());
            
            // Register user
            AuthResponse authResponse = authService.register(registerRequest);
            
            logger.info("User registered successfully: {}", authResponse.getUserId());
            
            return LambdaUtils.createSuccessResponse(authResponse, "User registered successfully");
            
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            
            // Handle specific error cases
            if (e.getMessage().contains("Email already registered")) {
                return LambdaUtils.createErrorResponse("Email already registered", 409);
            } else if (e.getMessage().contains("Validation failed")) {
                return LambdaUtils.createErrorResponse(e.getMessage(), 400);
            } else {
                return LambdaUtils.createErrorResponse("Registration failed", 500);
            }
        }
    }
}
