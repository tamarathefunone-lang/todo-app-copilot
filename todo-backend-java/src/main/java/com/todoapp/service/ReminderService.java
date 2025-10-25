package com.todoapp.service;

import com.todoapp.model.Task;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling reminder functionality
 * Supports EMAIL, SMS, and ALARM reminder types
 */
public class ReminderService {
    
    private static final Logger logger = Logger.getLogger(ReminderService.class.getName());
    
    // AWS clients
    private final SesClient sesClient;
    private final SnsClient snsClient;
    private final EventBridgeClient eventBridgeClient;
    
    // Configuration
    private static final String FROM_EMAIL = System.getenv("REMINDER_FROM_EMAIL");
    private static final String RULE_NAME_PREFIX = "reminder-task-";
    
    public ReminderService() {
        Region region = Region.US_EAST_1;
        this.sesClient = SesClient.builder().region(region).build();
        this.snsClient = SnsClient.builder().region(region).build();
        this.eventBridgeClient = EventBridgeClient.builder().region(region).build();
    }
    
    /**
     * Schedule a reminder for a task based on the reminder type
     */
    public boolean scheduleReminder(Task task) {
        try {
            if (task.getReminderType() == null || task.getReminderTime() == null) {
                return false; // No reminder needed
            }
            
            switch (task.getReminderType()) {
                case EMAIL:
                    return scheduleEmailReminder(task);
                case SMS:
                    return scheduleSmsReminder(task);
                case ALARM:
                    return scheduleAlarmReminder(task);
                default:
                    logger.warning("Unknown reminder type: " + task.getReminderType());
                    return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to schedule reminder for task " + task.getTaskId(), e);
            return false;
        }
    }
    
    /**
     * Schedule an email reminder using EventBridge
     */
    private boolean scheduleEmailReminder(Task task) {
        try {
            String ruleName = RULE_NAME_PREFIX + task.getTaskId() + "-email";
            String scheduleExpression = createScheduleExpression(task.getReminderTime());
            
            PutRuleRequest putRuleRequest = PutRuleRequest.builder()
                    .name(ruleName)
                    .scheduleExpression(scheduleExpression)
                    .description("Email reminder for task: " + task.getTitle())
                    .state(RuleState.ENABLED)
                    .build();
                    
            eventBridgeClient.putRule(putRuleRequest);
            logger.info("Scheduled email reminder for task: " + task.getTaskId());
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to schedule email reminder", e);
            return false;
        }
    }
    
    /**
     * Schedule an SMS reminder using EventBridge
     */
    private boolean scheduleSmsReminder(Task task) {
        try {
            String ruleName = RULE_NAME_PREFIX + task.getTaskId() + "-sms";
            String scheduleExpression = createScheduleExpression(task.getReminderTime());
            
            PutRuleRequest putRuleRequest = PutRuleRequest.builder()
                    .name(ruleName)
                    .scheduleExpression(scheduleExpression)
                    .description("SMS reminder for task: " + task.getTitle())
                    .state(RuleState.ENABLED)
                    .build();
                    
            eventBridgeClient.putRule(putRuleRequest);
            logger.info("Scheduled SMS reminder for task: " + task.getTaskId());
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to schedule SMS reminder", e);
            return false;
        }
    }
    
    /**
     * Schedule an alarm reminder using EventBridge
     */
    private boolean scheduleAlarmReminder(Task task) {
        try {
            String ruleName = RULE_NAME_PREFIX + task.getTaskId() + "-alarm";
            String scheduleExpression = createScheduleExpression(task.getReminderTime());
            
            PutRuleRequest putRuleRequest = PutRuleRequest.builder()
                    .name(ruleName)
                    .scheduleExpression(scheduleExpression)
                    .description("Alarm reminder for task: " + task.getTitle())
                    .state(RuleState.ENABLED)
                    .build();
                    
            eventBridgeClient.putRule(putRuleRequest);
            logger.info("Scheduled alarm reminder for task: " + task.getTaskId());
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to schedule alarm reminder", e);
            return false;
        }
    }
    
    /**
     * Send immediate email reminder
     */
    public boolean sendEmailReminder(Task task, String userEmail) {
        try {
            if (FROM_EMAIL == null) {
                logger.warning("FROM_EMAIL environment variable not set");
                return false;
            }
            
            String subject = "Task Reminder: " + task.getTitle();
            String htmlBody = createEmailHtmlBody(task);
            String textBody = createEmailTextBody(task);
            
            Content subjectContent = Content.builder().data(subject).charset("UTF-8").build();
            Content htmlContent = Content.builder().data(htmlBody).charset("UTF-8").build();
            Content textContent = Content.builder().data(textBody).charset("UTF-8").build();
            
            Body body = Body.builder().html(htmlContent).text(textContent).build();
            Message message = Message.builder().subject(subjectContent).body(body).build();
            Destination destination = Destination.builder().toAddresses(userEmail).build();
            
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(destination)
                    .message(message)
                    .source(FROM_EMAIL)
                    .build();
                    
            SendEmailResponse response = sesClient.sendEmail(emailRequest);
            logger.info("Email sent successfully. Message ID: " + response.messageId());
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send email reminder", e);
            return false;
        }
    }
    
    /**
     * Send immediate SMS reminder
     */
    public boolean sendSmsReminder(Task task, String phoneNumber) {
        try {
            String message = createSmsMessage(task);
            
            PublishRequest publishRequest = PublishRequest.builder()
                    .phoneNumber(phoneNumber)
                    .message(message)
                    .build();
                    
            PublishResponse response = snsClient.publish(publishRequest);
            logger.info("SMS sent successfully. Message ID: " + response.messageId());
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send SMS reminder", e);
            return false;
        }
    }
    
    /**
     * Process alarm reminder
     */
    public boolean processAlarmReminder(Task task) {
        try {
            logger.info("Processing alarm reminder for task: " + task.getTitle());
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process alarm reminder", e);
            return false;
        }
    }
    
    /**
     * Create schedule expression for EventBridge (cron format)
     */
    private String createScheduleExpression(Instant reminderTime) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(reminderTime, ZoneOffset.UTC);
        return String.format("cron(%d %d %d %d ? %d)",
                localDateTime.getMinute(),
                localDateTime.getHour(),
                localDateTime.getDayOfMonth(),
                localDateTime.getMonthValue(),
                localDateTime.getYear());
    }
    
    /**
     * Create HTML email body for reminders
     */
    private String createEmailHtmlBody(Task task) {
        return String.format(
            "<html><body>" +
            "<h2>📋 Task Reminder</h2>" +
            "<p><strong>Title:</strong> %s</p>" +
            "<p><strong>Description:</strong> %s</p>" +
            "<p><strong>Priority:</strong> %s</p>" +
            "<p><strong>Due Date:</strong> %s</p>" +
            "<p>Don't forget to complete this task!</p>" +
            "</body></html>",
            task.getTitle(),
            task.getDescription() != null ? task.getDescription() : "No description",
            task.getPriority() != null ? task.getPriority().toString() : "Normal",
            task.getDueDate() != null ? task.getDueDate().toString() : "No due date"
        );
    }
    
    /**
     * Create plain text email body for reminders
     */
    private String createEmailTextBody(Task task) {
        return String.format(
            "TASK REMINDER\n\n" +
            "Title: %s\n" +
            "Description: %s\n" +
            "Priority: %s\n" +
            "Due Date: %s\n\n" +
            "Don't forget to complete this task!",
            task.getTitle(),
            task.getDescription() != null ? task.getDescription() : "No description",
            task.getPriority() != null ? task.getPriority().toString() : "Normal",
            task.getDueDate() != null ? task.getDueDate().toString() : "No due date"
        );
    }
    
    /**
     * Create SMS message for reminders
     */
    private String createSmsMessage(Task task) {
        return String.format(
            "📋 Task Reminder: %s\n\n%s\n\nDue: %s | Priority: %s",
            task.getTitle(),
            task.getDescription() != null ? task.getDescription() : "No description",
            task.getDueDate() != null ? task.getDueDate().toString() : "No due date",
            task.getPriority() != null ? task.getPriority().toString() : "Normal"
        );
    }
}
