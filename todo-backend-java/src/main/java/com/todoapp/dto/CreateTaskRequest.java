package com.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoapp.model.Task.TaskPriority;
import com.todoapp.model.Task.TaskStatus;
import com.todoapp.model.Task.ReminderType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    
    @JsonProperty("reminderType")
    private ReminderType reminderType;
    
    @JsonProperty("reminderTime")
    private LocalDateTime reminderTime;
    
    @JsonProperty("phoneNumber")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    public CreateTaskRequest() {}

    public CreateTaskRequest(String title, String description, TaskPriority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }
    
    public CreateTaskRequest(String title, String description, TaskPriority priority, LocalDate dueDate, 
                           ReminderType reminderType, LocalDateTime reminderTime, String phoneNumber) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.reminderType = reminderType;
        this.reminderTime = reminderTime;
        this.phoneNumber = phoneNumber;
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
    
    public ReminderType getReminderType() {
        return reminderType;
    }
    
    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
    }
    
    public LocalDateTime getReminderTime() {
        return reminderTime;
    }
    
    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "CreateTaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", reminderType=" + reminderType +
                ", reminderTime=" + reminderTime +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
