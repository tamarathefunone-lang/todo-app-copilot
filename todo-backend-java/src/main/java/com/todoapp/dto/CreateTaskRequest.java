package com.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoapp.model.Task.TaskPriority;
import com.todoapp.model.Task.TaskStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request DTO for creating a task
 */
public class CreateTaskRequest {
    
    @JsonProperty("title")
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;
    
    @JsonProperty("description")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @JsonProperty("priority")
    private TaskPriority priority = TaskPriority.MEDIUM;
    
    @JsonProperty("dueDate")
    private LocalDate dueDate;

    public CreateTaskRequest() {}

    public CreateTaskRequest(String title, String description, TaskPriority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
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
        return "CreateTaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                '}';
    }
}
