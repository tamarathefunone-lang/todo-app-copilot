package com.todoapp.repository;

import com.todoapp.model.Task;
import com.todoapp.model.Task.TaskStatus;
import com.todoapp.model.Task.TaskPriority;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Task operations with DynamoDB
 */
public class TaskRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskRepository.class);
    private static final String TABLE_NAME = "Tasks";
    
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Task> taskTable;

    public TaskRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.taskTable = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Task.class));
    }

    /**
     * Save a task to DynamoDB
     */
    public Task save(Task task) {
        try {
            logger.info("Saving task with ID: {} for user: {}", task.getTaskId(), task.getUserId());
            taskTable.putItem(task);
            logger.info("Task saved successfully: {}", task.getTaskId());
            return task;
        } catch (Exception e) {
            logger.error("Error saving task: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save task", e);
        }
    }

    /**
     * Find task by user ID and task ID
     */
    public Optional<Task> findByUserIdAndTaskId(String userId, String taskId) {
        try {
            logger.info("Finding task by userId: {} and taskId: {}", userId, taskId);
            Key key = Key.builder()
                    .partitionValue(userId)
                    .sortValue(taskId)
                    .build();
            
            Task task = taskTable.getItem(key);
            
            if (task != null && !task.isDeleted()) {
                logger.info("Task found: {}", taskId);
                return Optional.of(task);
            } else {
                logger.info("Task not found or deleted: {}", taskId);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error finding task by userId {} and taskId {}: {}", userId, taskId, e.getMessage(), e);
            throw new RuntimeException("Failed to find task", e);
        }
    }

    /**
     * Find all tasks for a user
     */
    public List<Task> findByUserId(String userId) {
        try {
            logger.info("Finding all tasks for user: {}", userId);
            
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(userId)
                    .build());

            Expression filterExpression = Expression.builder()
                    .expression("isDeleted = :deleted")
                    .putExpressionValue(":deleted", AttributeValue.builder().bool(false).build())
                    .build();
            
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .filterExpression(filterExpression)
                    .build();

            List<Task> tasks = taskTable.query(queryRequest)
                    .items()
                    .stream()
                    .collect(Collectors.toList());

            logger.info("Found {} tasks for user: {}", tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            logger.error("Error finding tasks for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to find tasks for user", e);
        }
    }

    /**
     * Find tasks by user ID and status
     */
    public List<Task> findByUserIdAndStatus(String userId, TaskStatus status) {
        try {
            logger.info("Finding tasks for user: {} with status: {}", userId, status);
            
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(userId)
                    .build());

            Expression filterExpression = Expression.builder()
                    .expression("isDeleted = :deleted AND #status = :status")
                    .putExpressionName("#status", "status")
                    .putExpressionValue(":deleted", AttributeValue.builder().bool(false).build())
                    .putExpressionValue(":status", AttributeValue.builder().s(status.name()).build())
                    .build();
            
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .filterExpression(filterExpression)
                    .build();

            List<Task> tasks = taskTable.query(queryRequest)
                    .items()
                    .stream()
                    .collect(Collectors.toList());

            logger.info("Found {} tasks with status {} for user: {}", tasks.size(), status, userId);
            return tasks;
        } catch (Exception e) {
            logger.error("Error finding tasks by status for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to find tasks by status", e);
        }
    }

    /**
     * Find tasks by user ID and priority
     */
    public List<Task> findByUserIdAndPriority(String userId, TaskPriority priority) {
        try {
            logger.info("Finding tasks for user: {} with priority: {}", userId, priority);
            
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(userId)
                    .build());

            Expression filterExpression = Expression.builder()
                    .expression("isDeleted = :deleted AND priority = :priority")
                    .putExpressionValue(":deleted", AttributeValue.builder().bool(false).build())
                    .putExpressionValue(":priority", AttributeValue.builder().s(priority.name()).build())
                    .build();
            
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .filterExpression(filterExpression)
                    .build();

            List<Task> tasks = taskTable.query(queryRequest)
                    .items()
                    .stream()
                    .collect(Collectors.toList());

            logger.info("Found {} tasks with priority {} for user: {}", tasks.size(), priority, userId);
            return tasks;
        } catch (Exception e) {
            logger.error("Error finding tasks by priority for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to find tasks by priority", e);
        }
    }

    /**
     * Find overdue tasks for a user
     */
    public List<Task> findOverdueTasks(String userId) {
        try {
            logger.info("Finding overdue tasks for user: {}", userId);
            
            String today = LocalDate.now().toString();
            
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(userId)
                    .build());

            Expression filterExpression = Expression.builder()
                    .expression("isDeleted = :deleted AND #status <> :completed AND dueDate < :today")
                    .putExpressionName("#status", "status")
                    .putExpressionValue(":deleted", AttributeValue.builder().bool(false).build())
                    .putExpressionValue(":completed", AttributeValue.builder().s(TaskStatus.COMPLETED.name()).build())
                    .putExpressionValue(":today", AttributeValue.builder().s(today).build())
                    .build();
            
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .filterExpression(filterExpression)
                    .build();

            List<Task> tasks = taskTable.query(queryRequest)
                    .items()
                    .stream()
                    .collect(Collectors.toList());

            logger.info("Found {} overdue tasks for user: {}", tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            logger.error("Error finding overdue tasks for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to find overdue tasks", e);
        }
    }

    /**
     * Update task
     */
    public Task update(Task task) {
        try {
            logger.info("Updating task: {} for user: {}", task.getTaskId(), task.getUserId());
            task.updateTimestamp();
            taskTable.putItem(task);
            logger.info("Task updated successfully: {}", task.getTaskId());
            return task;
        } catch (Exception e) {
            logger.error("Error updating task {}: {}", task.getTaskId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update task", e);
        }
    }

    /**
     * Soft delete task by marking as deleted
     */
    public void delete(String userId, String taskId) {
        try {
            logger.info("Soft deleting task: {} for user: {}", taskId, userId);
            Optional<Task> taskOpt = findByUserIdAndTaskId(userId, taskId);
            
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.markAsDeleted();
                taskTable.putItem(task);
                logger.info("Task soft deleted successfully: {}", taskId);
            } else {
                logger.warn("Task not found for deletion: {} for user: {}", taskId, userId);
                throw new RuntimeException("Task not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting task {} for user {}: {}", taskId, userId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    /**
     * Count tasks by status for a user
     */
    public long countByUserIdAndStatus(String userId, TaskStatus status) {
        try {
            List<Task> tasks = findByUserIdAndStatus(userId, status);
            return tasks.size();
        } catch (Exception e) {
            logger.error("Error counting tasks by status for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to count tasks by status", e);
        }
    }

    /**
     * Get task statistics for a user
     */
    public TaskStats getTaskStats(String userId) {
        try {
            logger.info("Getting task statistics for user: {}", userId);
            
            List<Task> allTasks = findByUserId(userId);
            
            long totalTasks = allTasks.size();
            long completedTasks = allTasks.stream().mapToLong(task -> 
                task.getStatus() == TaskStatus.COMPLETED ? 1 : 0).sum();
            long pendingTasks = allTasks.stream().mapToLong(task -> 
                task.getStatus() == TaskStatus.PENDING ? 1 : 0).sum();
            long inProgressTasks = allTasks.stream().mapToLong(task -> 
                task.getStatus() == TaskStatus.IN_PROGRESS ? 1 : 0).sum();
            long overdueTasks = allTasks.stream().mapToLong(task -> 
                task.isOverdue() ? 1 : 0).sum();

            return new TaskStats(totalTasks, completedTasks, pendingTasks, inProgressTasks, overdueTasks);
        } catch (Exception e) {
            logger.error("Error getting task statistics for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to get task statistics", e);
        }
    }

    /**
     * Task statistics data class
     */
    public static class TaskStats {
        private final long totalTasks;
        private final long completedTasks;
        private final long pendingTasks;
        private final long inProgressTasks;
        private final long overdueTasks;

        public TaskStats(long totalTasks, long completedTasks, long pendingTasks, long inProgressTasks, long overdueTasks) {
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.pendingTasks = pendingTasks;
            this.inProgressTasks = inProgressTasks;
            this.overdueTasks = overdueTasks;
        }

        public long getTotalTasks() { return totalTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public long getPendingTasks() { return pendingTasks; }
        public long getInProgressTasks() { return inProgressTasks; }
        public long getOverdueTasks() { return overdueTasks; }
    }
}
