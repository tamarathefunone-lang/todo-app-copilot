package com.todoapp.lambda.reminder;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.service.ReminderService;
import com.todoapp.model.Task;
import com.todoapp.repository.TaskRepository;
import com.todoapp.util.ServiceFactory;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lambda function to process scheduled reminders
 * Triggered by EventBridge rules created by ReminderService
 */
public class ReminderProcessorHandler implements RequestHandler<Map<String, Object>, String> {
    
    private static final Logger logger = Logger.getLogger(ReminderProcessorHandler.class.getName());
    private final ReminderService reminderService;
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    
    public ReminderProcessorHandler() {
        this.reminderService = new ReminderService();
        this.taskRepository = ServiceFactory.getTaskRepository();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        try {
            logger.info("Processing reminder event: " + event.toString());
            
            // Extract reminder details from event
            String taskId = (String) event.get("taskId");
            String reminderType = (String) event.get("reminderType");
            String userEmail = (String) event.get("userEmail");
            String phoneNumber = (String) event.get("phoneNumber");
            
            if (taskId == null || reminderType == null) {
                logger.warning("Missing required fields in reminder event");
                return "ERROR: Missing taskId or reminderType";
            }
            
            // Get the task from database  
            // We need userId to find the task, but we only have taskId in the event
            // For now, let's try to get it from the event or use a different approach
            Task task = null;
            // TODO: We need to modify this to work with the actual repository interface
            if (task == null) {
                logger.warning("Task not found: " + taskId);
                return "ERROR: Task not found";
            }
            
            // Check if reminder was already sent
            if (task.isReminderSent()) {
                logger.info("Reminder already sent for task: " + taskId);
                return "INFO: Reminder already sent";
            }
            
            boolean success = false;
            
            // Process the reminder based on type
            switch (reminderType.toUpperCase()) {
                case "EMAIL":
                    if (userEmail != null && !userEmail.isEmpty()) {
                        success = reminderService.sendEmailReminder(task, userEmail);
                    } else {
                        logger.warning("No email address provided for email reminder");
                    }
                    break;
                    
                case "SMS":
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        success = reminderService.sendSmsReminder(task, phoneNumber);
                    } else {
                        logger.warning("No phone number provided for SMS reminder");
                    }
                    break;
                    
                case "ALARM":
                    success = reminderService.processAlarmReminder(task);
                    break;
                    
                default:
                    logger.warning("Unknown reminder type: " + reminderType);
                    return "ERROR: Unknown reminder type";
            }
            
            // Update task to mark reminder as sent
            if (success) {
                task.setReminderSent(true);
                taskRepository.save(task);
                logger.info("Reminder processed successfully for task: " + taskId);
                return "SUCCESS: Reminder sent";
            } else {
                logger.warning("Failed to process reminder for task: " + taskId);
                return "ERROR: Failed to send reminder";
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing reminder", e);
            return "ERROR: " + e.getMessage();
        }
    }
}
