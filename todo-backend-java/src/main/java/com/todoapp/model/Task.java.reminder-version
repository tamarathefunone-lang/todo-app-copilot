package com.todoapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Task entity for DynamoDB
 */
@DynamoDbBean
public class Task {
    
    private String taskId;
    private String userId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private ReminderType reminderType;
    private Instant reminderTime;
    private boolean isReminderSent;
    private String phoneNumber; // For SMS reminders
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDeleted;

    public Task() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = TaskStatus.PENDING;
        this.priority = TaskPriority.MEDIUM;
        this.isDeleted = false;
        this.isReminderSent = false;
    }

    public Task(String taskId, String userId, String title, String description) {
        this();
        this.taskId = taskId;
        this.userId = userId;
        this.title = title;
        this.description = description;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("userId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("taskId")
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @DynamoDbAttribute("title")
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDbAttribute("description")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDbAttribute("status")
    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @DynamoDbAttribute("priority")
    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    @DynamoDbAttribute("dueDate")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @DynamoDbAttribute("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDbAttribute("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @DynamoDbAttribute("reminderType")
    public ReminderType getReminderType() {
        return reminderType;
    }

    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
    }

    @DynamoDbAttribute("reminderTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    public Instant getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Instant reminderTime) {
        this.reminderTime = reminderTime;
    }

    @DynamoDbAttribute("isReminderSent")
    public boolean isReminderSent() {
        return isReminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        isReminderSent = reminderSent;
    }

    @DynamoDbAttribute("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @DynamoDbAttribute("isDeleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "StatusIndex")
    @DynamoDbAttribute("statusIndex")
    public String getStatusIndex() {
        return userId + "#" + status.name();
    }

    public void setStatusIndex(String statusIndex) {
        // This is computed, so we don't need to set it manually
    }

    public void updateTimestamp() {
        this.updatedAt = Instant.now();
    }

    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        updateTimestamp();
    }

    public void markAsPending() {
        this.status = TaskStatus.PENDING;
        updateTimestamp();
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        updateTimestamp();
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) && status != TaskStatus.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId) && Objects.equals(userId, task.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }

    /**
     * Task status enumeration
     */
    public enum TaskStatus {
        PENDING,
        COMPLETED,
        IN_PROGRESS
    }

    /**
     * Task priority enumeration
     */
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH
    }

    /**
     * Reminder type enumeration
     */
    public enum ReminderType {
        EMAIL,
        SMS,
        ALARM,
        NONE
    }
}
