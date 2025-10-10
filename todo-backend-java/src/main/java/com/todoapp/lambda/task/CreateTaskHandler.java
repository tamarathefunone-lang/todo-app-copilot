package com.todoapp.lambda.task;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todoapp.dto.CreateTaskRequest;
import com.todoapp.model.Task;
import com.todoapp.repository.TaskRepository;
import com.todoapp.util.LambdaUtils;
import com.todoapp.util.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Lambda function for creating tasks
 */
public class CreateTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateTaskHandler.class);
    
    private final TaskRepository taskRepository;

    public CreateTaskHandler() {
        this.taskRepository = ServiceFactory.getTaskRepository();
    }

    // Constructor for testing
    public CreateTaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        logger.info("Processing create task request");
        
        return LambdaUtils.handleRequest(request, this::processCreateTask);
    }

    private APIGatewayProxyResponseEvent processCreateTask(APIGatewayProxyRequestEvent request) {
        try {
            // Get user ID from authorizer context
            String userId = getUserIdFromContext(request);
            
            // Parse request body
            CreateTaskRequest createRequest = LambdaUtils.parseRequestBody(request, CreateTaskRequest.class);
            
            logger.info("Creating task for user: {} with title: {}", userId, createRequest.getTitle());
            
            // Create task
            String taskId = UUID.randomUUID().toString();
            Task task = new Task(taskId, userId, createRequest.getTitle(), createRequest.getDescription());
            task.setPriority(createRequest.getPriority());
            task.setDueDate(createRequest.getDueDate());
            
            // Save task
            Task savedTask = taskRepository.save(task);
            
            logger.info("Task created successfully: {} for user: {}", taskId, userId);
            
            return LambdaUtils.createSuccessResponse(savedTask, "Task created successfully");
            
        } catch (Exception e) {
            logger.error("Failed to create task: {}", e.getMessage(), e);
            
            if (e.getMessage().contains("Validation failed")) {
                return LambdaUtils.createErrorResponse(e.getMessage(), 400);
            } else {
                return LambdaUtils.createErrorResponse("Failed to create task", 500);
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
