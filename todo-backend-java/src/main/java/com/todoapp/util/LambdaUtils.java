package com.todoapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todoapp.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for Lambda functions
 */
public class LambdaUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(LambdaUtils.class);
    private static final ObjectMapper objectMapper = createObjectMapper();
    private static final Validator validator = createValidator();
    
    // CORS headers
    private static final Map<String, String> CORS_HEADERS = Map.of(
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
            "Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS",
            "Content-Type", "application/json"
    );

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private static Validator createValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    /**
     * Parse JSON request body into specified class
     */
    public static <T> T parseRequestBody(APIGatewayProxyRequestEvent request, Class<T> clazz) {
        try {
            String body = request.getBody();
            if (body == null || body.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            
            logger.info("Parsing request body for class: {}", clazz.getSimpleName());
            T parsedObject = objectMapper.readValue(body, clazz);
            
            // Validate the parsed object
            Set<ConstraintViolation<T>> violations = validator.validate(parsedObject);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Validation failed: " + errorMessage);
            }
            
            return parsedObject;
        } catch (Exception e) {
            logger.error("Error parsing request body: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid request body: " + e.getMessage(), e);
        }
    }

    /**
     * Create success response
     */
    public static <T> APIGatewayProxyResponseEvent createSuccessResponse(T data) {
        return createSuccessResponse(data, null);
    }

    /**
     * Create success response with message
     */
    public static <T> APIGatewayProxyResponseEvent createSuccessResponse(T data, String message) {
        try {
            ApiResponse<T> response = ApiResponse.success(data, message);
            String responseBody = objectMapper.writeValueAsString(response);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(CORS_HEADERS)
                    .withBody(responseBody);
        } catch (Exception e) {
            logger.error("Error creating success response: {}", e.getMessage(), e);
            return createErrorResponse("Internal server error", 500);
        }
    }

    /**
     * Create error response
     */
    public static APIGatewayProxyResponseEvent createErrorResponse(String error, int statusCode) {
        return createErrorResponse(error, null, statusCode);
    }

    /**
     * Create error response with message
     */
    public static APIGatewayProxyResponseEvent createErrorResponse(String error, String message, int statusCode) {
        try {
            ApiResponse<Object> response = ApiResponse.error(error, message);
            String responseBody = objectMapper.writeValueAsString(response);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(CORS_HEADERS)
                    .withBody(responseBody);
        } catch (Exception e) {
            logger.error("Error creating error response: {}", e.getMessage(), e);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(CORS_HEADERS)
                    .withBody("{\"success\":false,\"error\":\"Internal server error\"}");
        }
    }

    /**
     * Create CORS preflight response
     */
    public static APIGatewayProxyResponseEvent createCorsResponse() {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(CORS_HEADERS)
                .withBody("");
    }

    /**
     * Extract path parameter
     */
    public static String getPathParameter(APIGatewayProxyRequestEvent request, String paramName) {
        Map<String, String> pathParameters = request.getPathParameters();
        if (pathParameters == null || !pathParameters.containsKey(paramName)) {
            throw new IllegalArgumentException("Missing required path parameter: " + paramName);
        }
        return pathParameters.get(paramName);
    }

    /**
     * Extract query parameter
     */
    public static String getQueryParameter(APIGatewayProxyRequestEvent request, String paramName) {
        Map<String, String> queryParameters = request.getQueryStringParameters();
        if (queryParameters == null) {
            return null;
        }
        return queryParameters.get(paramName);
    }

    /**
     * Extract query parameter with default value
     */
    public static String getQueryParameter(APIGatewayProxyRequestEvent request, String paramName, String defaultValue) {
        String value = getQueryParameter(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * Extract authorization header (JWT token)
     */
    public static String getAuthorizationHeader(APIGatewayProxyRequestEvent request) {
        Map<String, String> headers = request.getHeaders();
        if (headers == null) {
            return null;
        }
        
        // Try different header name variations
        String auth = headers.get("Authorization");
        if (auth == null) {
            auth = headers.get("authorization");
        }
        
        return auth;
    }

    /**
     * Extract JWT token from authorization header
     */
    public static String extractJwtToken(APIGatewayProxyRequestEvent request) {
        String authHeader = getAuthorizationHeader(request);
        if (authHeader == null) {
            throw new IllegalArgumentException("Missing Authorization header");
        }
        
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid Authorization header format. Expected 'Bearer <token>'");
        }
    }

    /**
     * Check if request is OPTIONS (CORS preflight)
     */
    public static boolean isOptionsRequest(APIGatewayProxyRequestEvent request) {
        return "OPTIONS".equalsIgnoreCase(request.getHttpMethod());
    }

    /**
     * Log request details
     */
    public static void logRequest(APIGatewayProxyRequestEvent request) {
        logger.info("Processing request: {} {}", 
                request.getHttpMethod(), 
                request.getPath());
        logger.debug("Request headers: {}", request.getHeaders());
        logger.debug("Path parameters: {}", request.getPathParameters());
        logger.debug("Query parameters: {}", request.getQueryStringParameters());
    }

    /**
     * Handle common Lambda execution pattern
     */
    public static APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            RequestHandler handler) {
        
        try {
            // Log request
            logRequest(request);
            
            // Handle CORS preflight
            if (isOptionsRequest(request)) {
                return createCorsResponse();
            }
            
            // Execute handler
            return handler.handle(request);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), 400);
        } catch (SecurityException e) {
            logger.warn("Unauthorized: {}", e.getMessage());
            return createErrorResponse("Unauthorized", 401);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            return createErrorResponse("Internal server error", 500);
        }
    }

    /**
     * Functional interface for request handling
     */
    @FunctionalInterface
    public interface RequestHandler {
        APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent request) throws Exception;
    }
}
