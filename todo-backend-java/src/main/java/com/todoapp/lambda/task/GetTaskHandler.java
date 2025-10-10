package com.todoapp.lambda.task;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todoapp.model.Task;
import com.todoapp.repository.TaskRepository;
import com.todoapp.util.LambdaUtils;
import com.todoapp.util.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Lambda function for getting a single task
 */
public class GetTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(GetTaskHandler.class);
    
    private final TaskRepository taskRepository;

    public GetTaskHandler() {
        this.taskRepository = ServiceFactory.getTaskRepository();
    }

    // Constructor for testing
    public GetTaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        logger.info("Processing get task request");
        
        return LambdaUtils.handleRequest(request, this::processGetTask);
    }

    private APIGatewayProxyResponseEvent processGetTask(APIGatewayProxyRequestEvent request) {
        try {
            // Get user ID from authorizer context
            String userId = getUserIdFromContext(request);
            
            // Get task ID from path parameters
            String taskId = LambdaUtils.getPathParameter(request, "taskId");
            
            logger.info("Getting task: {} for user: {}", taskId, userId);
            
            // Find task
            Optional<Task> taskOpt = taskRepository.findByUserIdAndTaskId(userId, taskId);
            
            if (!taskOpt.isPresent()) {
                logger.warn("Task not found: {} for user: {}", taskId, userId);
                return LambdaUtils.createErrorResponse("Task not found", 404);
            }
            
            Task task = taskOpt.get();
            
            logger.info("Task found successfully: {} for user: {}", taskId, userId);
            
            return LambdaUtils.createSuccessResponse(task);
            
        } catch (Exception e) {
            logger.error("Failed to get task: {}", e.getMessage(), e);
            return LambdaUtils.createErrorResponse("Failed to get task", 500);
        }
    }

    /**
     * Extract user ID from authorizer context
     */
    private String getUserIdFromContext(APIGatewayProxyRequestEvent request) {
        if (request.getRequestContext() != null && 
            request.getRequestContext().getAuthorizer() != null) {
            
            Object userId = request.getRequestContext().getAuthorizer().get("userId");
            if (userId != null) {
                return userId.toString();
            }
        }
        
        throw new SecurityException("User ID not found in request context");
    }
}
