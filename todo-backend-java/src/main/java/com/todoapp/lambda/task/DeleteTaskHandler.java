package com.todoapp.lambda.task;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todoapp.repository.TaskRepository;
import com.todoapp.util.LambdaUtils;
import com.todoapp.util.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lambda function for deleting tasks
 */
public class DeleteTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteTaskHandler.class);
    
    private final TaskRepository taskRepository;

    public DeleteTaskHandler() {
        this.taskRepository = ServiceFactory.getTaskRepository();
    }

    // Constructor for testing
    public DeleteTaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        logger.info("Processing delete task request");
        
        return LambdaUtils.handleRequest(request, this::processDeleteTask);
    }

    private APIGatewayProxyResponseEvent processDeleteTask(APIGatewayProxyRequestEvent request) {
        try {
            // Get user ID from authorizer context
            String userId = getUserIdFromContext(request);
            
            // Get task ID from path parameters
            String taskId = LambdaUtils.getPathParameter(request, "taskId");
            
            logger.info("Deleting task: {} for user: {}", taskId, userId);
            
            // Delete task (soft delete)
            taskRepository.delete(userId, taskId);
            
            logger.info("Task deleted successfully: {} for user: {}", taskId, userId);
            
            return LambdaUtils.createSuccessResponse(null, "Task deleted successfully");
            
        } catch (Exception e) {
            logger.error("Failed to delete task: {}", e.getMessage(), e);
            
            if (e.getMessage().contains("Task not found")) {
                return LambdaUtils.createErrorResponse("Task not found", 404);
            } else {
                return LambdaUtils.createErrorResponse("Failed to delete task", 500);
            }
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
