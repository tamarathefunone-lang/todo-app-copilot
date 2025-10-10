package com.todoapp.lambda.auth;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent.RequestContext;
import com.todoapp.service.AuthService;
import com.todoapp.service.JwtService;
import com.todoapp.util.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda authorizer for JWT token validation
 */
public class AuthorizerHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, Map<String, Object>> {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorizerHandler.class);
    
    private final AuthService authService;

    public AuthorizerHandler() {
        this.authService = ServiceFactory.getAuthService();
    }

    // Constructor for testing
    public AuthorizerHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Map<String, Object> handleRequest(APIGatewayCustomAuthorizerEvent event, Context context) {
        logger.info("Processing authorization request for method: {} on resource: {}", 
                event.getHttpMethod(), event.getResource());
        
        try {
            // Extract token from authorization header
            String token = extractToken(event);
            
            if (token == null || token.trim().isEmpty()) {
                logger.warn("No authorization token provided");
                throw new RuntimeException("Unauthorized");
            }
            
            // Validate token and get user info
            JwtService.UserInfo userInfo = authService.validateToken(token);
            
            logger.info("Token validated successfully for user: {}", userInfo.getUserId());
            
            // Generate policy
            return generatePolicy(userInfo.getUserId(), "Allow", event.getMethodArn(), userInfo);
            
        } catch (Exception e) {
            logger.error("Authorization failed: {}", e.getMessage(), e);
            throw new RuntimeException("Unauthorized");
        }
    }

    /**
     * Extract JWT token from the event
     */
    private String extractToken(APIGatewayCustomAuthorizerEvent event) {
        // Try to get token from authorization header
        String authorizationToken = event.getAuthorizationToken();
        
        if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
            return authorizationToken.substring(7);
        }
        
        // Try to get from headers if available
        if (event.getHeaders() != null) {
            String authHeader = event.getHeaders().get("Authorization");
            if (authHeader == null) {
                authHeader = event.getHeaders().get("authorization");
            }
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        
        return null;
    }

    /**
     * Generate IAM policy for API Gateway
     */
    private Map<String, Object> generatePolicy(String principalId, String effect, String resource, JwtService.UserInfo userInfo) {
        Map<String, Object> policy = new HashMap<>();
        policy.put("principalId", principalId);
        
        if (effect != null && resource != null) {
            Map<String, Object> policyDocument = new HashMap<>();
            policyDocument.put("Version", "2012-10-17");
            
            Map<String, Object> statement = new HashMap<>();
            statement.put("Action", "execute-api:Invoke");
            statement.put("Effect", effect);
            statement.put("Resource", resource);
            
            policyDocument.put("Statement", List.of(statement));
            policy.put("policyDocument", policyDocument);
        }
        
        // Add user context to be passed to the Lambda function
        Map<String, Object> context = new HashMap<>();
        context.put("userId", userInfo.getUserId());
        context.put("email", userInfo.getEmail());
        context.put("firstName", userInfo.getFirstName());
        context.put("lastName", userInfo.getLastName());
        
        policy.put("context", context);
        
        logger.info("Generated policy for user: {} with effect: {}", principalId, effect);
        
        return policy;
    }
}
