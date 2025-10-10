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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda function for listing tasks with filtering
 */
public class ListTasksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(ListTasksHandler.class);
    
    private final TaskRepository taskRepository;

    public ListTasksHandler() {
        this.taskRepository = ServiceFactory.getTaskRepository();
    }

    // Constructor for testing
    public ListTasksHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        logger.info("Processing list tasks request");
        
        return LambdaUtils.handleRequest(request, this::processListTasks);
    }

    private APIGatewayProxyResponseEvent processListTasks(APIGatewayProxyRequestEvent request) {
        try {
            // Get user ID from authorizer context
            String userId = getUserIdFromContext(request);
            
            // Get query parameters for filtering
            String status = LambdaUtils.getQueryParameter(request, "status");
            String priority = LambdaUtils.getQueryParameter(request, "priority");
            String overdue = LambdaUtils.getQueryParameter(request, "overdue");
            String includeStats = LambdaUtils.getQueryParameter(request, "includeStats", "false");
            
            logger.info("Listing tasks for user: {} with filters - status: {}, priority: {}, overdue: {}", 
                    userId, status, priority, overdue);
            
            List<Task> tasks;
            
            // Apply filters
            if ("true".equalsIgnoreCase(overdue)) {
                tasks = taskRepository.findOverdueTasks(userId);
            } else if (status != null && !status.trim().isEmpty()) {
                try {
                    Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
                    tasks = taskRepository.findByUserIdAndStatus(userId, taskStatus);
                } catch (IllegalArgumentException e) {
                    return LambdaUtils.createErrorResponse("Invalid status value: " + status, 400);
                }
            } else if (priority != null && !priority.trim().isEmpty()) {
                try {
                    Task.TaskPriority taskPriority = Task.TaskPriority.valueOf(priority.toUpperCase());
                    tasks = taskRepository.findByUserIdAndPriority(userId, taskPriority);
                } catch (IllegalArgumentException e) {
                    return LambdaUtils.createErrorResponse("Invalid priority value: " + priority, 400);
                }
            } else {
                // Get all tasks
                tasks = taskRepository.findByUserId(userId);
            }
            
            logger.info("Found {} tasks for user: {}", tasks.size(), userId);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("tasks", tasks);
            response.put("count", tasks.size());
            
            // Include statistics if requested
            if ("true".equalsIgnoreCase(includeStats)) {
                TaskRepository.TaskStats stats = taskRepository.getTaskStats(userId);
                Map<String, Object> statsMap = new HashMap<>();
                statsMap.put("totalTasks", stats.getTotalTasks());
                statsMap.put("completedTasks", stats.getCompletedTasks());
                statsMap.put("pendingTasks", stats.getPendingTasks());
                statsMap.put("inProgressTasks", stats.getInProgressTasks());
                statsMap.put("overdueTasks", stats.getOverdueTasks());
                response.put("statistics", statsMap);
            }
            
            return LambdaUtils.createSuccessResponse(response);
            
        } catch (Exception e) {
            logger.error("Failed to list tasks: {}", e.getMessage(), e);
            return LambdaUtils.createErrorResponse("Failed to list tasks", 500);
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
