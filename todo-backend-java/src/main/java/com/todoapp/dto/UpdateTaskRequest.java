package com.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoapp.model.Task.TaskPriority;
import com.todoapp.model.Task.TaskStatus;

import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request DTO for updating a task
 */
public class UpdateTaskRequest {
    
    @JsonProperty("title")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;
    
    @JsonProperty("description")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @JsonProperty("status")
    private TaskStatus status;
    
    @JsonProperty("priority")
    private TaskPriority priority;
    
    @JsonProperty("dueDate")
    private LocalDate dueDate;

    public UpdateTaskRequest() {}

    public UpdateTaskRequest(String title, String description, TaskStatus status, TaskPriority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "UpdateTaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                '}';
    }
}
