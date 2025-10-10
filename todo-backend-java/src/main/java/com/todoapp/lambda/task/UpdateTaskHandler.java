package com.todoapp.lambda.task;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todoapp.dto.UpdateTaskRequest;
import com.todoapp.model.Task;
import com.todoapp.repository.TaskRepository;
import com.todoapp.util.LambdaUtils;
import com.todoapp.util.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Lambda function for updating tasks
 */
public class UpdateTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateTaskHandler.class);
    
    private final TaskRepository taskRepository;

    public UpdateTaskHandler() {
        this.taskRepository = ServiceFactory.getTaskRepository();
    }

    // Constructor for testing
    public UpdateTaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        logger.info("Processing update task request");
        
        return LambdaUtils.handleRequest(request, this::processUpdateTask);
    }

    private APIGatewayProxyResponseEvent processUpdateTask(APIGatewayProxyRequestEvent request) {
        try {
            // Get user ID from authorizer context
            String userId = getUserIdFromContext(request);
            
            // Get task ID from path parameters
            String taskId = LambdaUtils.getPathParameter(request, "taskId");
            
            // Parse request body
            UpdateTaskRequest updateRequest = LambdaUtils.parseRequestBody(request, UpdateTaskRequest.class);
            
            logger.info("Updating task: {} for user: {}", taskId, userId);
            
            // Find existing task
            Optional<Task> taskOpt = taskRepository.findByUserIdAndTaskId(userId, taskId);
            
            if (!taskOpt.isPresent()) {
                logger.warn("Task not found: {} for user: {}", taskId, userId);
                return LambdaUtils.createErrorResponse("Task not found", 404);
            }
            
            Task task = taskOpt.get();
            
            // Update task fields if provided
            if (updateRequest.getTitle() != null && !updateRequest.getTitle().trim().isEmpty()) {
                task.setTitle(updateRequest.getTitle().trim());
            }
            
            if (updateRequest.getDescription() != null) {
                task.setDescription(updateRequest.getDescription().trim());
            }
            
            if (updateRequest.getStatus() != null) {
                task.setStatus(updateRequest.getStatus());
            }
            
            if (updateRequest.getPriority() != null) {
                task.setPriority(updateRequest.getPriority());
            }
            
            if (updateRequest.getDueDate() != null) {
                task.setDueDate(updateRequest.getDueDate());
            }
            
            // Update timestamp
            task.updateTimestamp();
            
            // Save updated task
            Task updatedTask = taskRepository.update(task);
            
            logger.info("Task updated successfully: {} for user: {}", taskId, userId);
            
            return LambdaUtils.createSuccessResponse(updatedTask, "Task updated successfully");
            
        } catch (Exception e) {
            logger.error("Failed to update task: {}", e.getMessage(), e);
            
            if (e.getMessage().contains("Validation failed")) {
                return LambdaUtils.createErrorResponse(e.getMessage(), 400);
            } else {
                return LambdaUtils.createErrorResponse("Failed to update task", 500);
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
