package com.todoapp.lambda.auth;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todoapp.dto.AuthResponse;
import com.todoapp.dto.LoginRequest;
import com.todoapp.service.AuthService;
import com.todoapp.util.LambdaUtils;
import com.todoapp.util.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lambda function for user login
 */
public class LoginHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    
    private final AuthService authService;

    public LoginHandler() {
        this.authService = ServiceFactory.getAuthService();
    }

    // Constructor for testing
    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        logger.info("Processing login request");
        
        return LambdaUtils.handleRequest(request, this::processLogin);
    }

    private APIGatewayProxyResponseEvent processLogin(APIGatewayProxyRequestEvent request) {
        try {
            // Parse request body
            LoginRequest loginRequest = LambdaUtils.parseRequestBody(request, LoginRequest.class);
            
            logger.info("Logging in user with email: {}", loginRequest.getEmail());
            
            // Authenticate user
            AuthResponse authResponse = authService.login(loginRequest);
            
            logger.info("User logged in successfully: {}", authResponse.getUserId());
            
            return LambdaUtils.createSuccessResponse(authResponse, "Login successful");
            
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage(), e);
            
            // Handle specific error cases
            if (e.getMessage().contains("Invalid email or password") || 
                e.getMessage().contains("Account is inactive")) {
                return LambdaUtils.createErrorResponse(e.getMessage(), 401);
            } else if (e.getMessage().contains("Validation failed")) {
                return LambdaUtils.createErrorResponse(e.getMessage(), 400);
            } else {
                return LambdaUtils.createErrorResponse("Login failed", 500);
            }
        }
    }
}
