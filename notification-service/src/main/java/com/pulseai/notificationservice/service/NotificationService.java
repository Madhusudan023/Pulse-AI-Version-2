package com.pulseai.notificationservice.service;

import com.pulseai.notificationservice.client.EmployeeFeignClient;
import com.pulseai.notificationservice.constant.NotificationType;
import com.pulseai.notificationservice.dto.event.ReportGeneratedEvent;
import com.pulseai.notificationservice.dto.event.SurveyCompletedEvent;
import com.pulseai.notificationservice.dto.event.SurveyPublishedEvent;
import com.pulseai.notificationservice.dto.event.SurveyReminderEvent;
import com.pulseai.notificationservice.email.EmailService;
import com.pulseai.notificationservice.entity.Notification;
import com.pulseai.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeFeignClient employeeFeignClient;
    private final EmailService emailService;

    @Transactional
    public void processSurveyPublished(SurveyPublishedEvent event) {
        log.info("Processing SurveyPublishedEvent for Region: {}", event.getRegion());
        
        List<EmployeeFeignClient.EmployeeInternalDTO> employees = employeeFeignClient.getEmployeesByRegion(event.getRegion());
        String title = "New Monthly Pulse Survey: " + event.getTitle();
        String message = "A new survey has been assigned to you. Please complete it to share your feedback.";
        
        for (var emp : employees) {
            if (event.getEmployeeIds() != null && event.getEmployeeIds().contains(emp.getEmployeeId())) {
                saveAndSendNotification(emp.getEmployeeId(), emp.getEmail(), title, message, NotificationType.SURVEY_PUBLISHED, event.getSurveyId());
            }
        }
        
        if (event.getCustomEmails() != null) {
            for (String customEmail : event.getCustomEmails()) {
                saveAndSendNotification(null, customEmail, title, message, NotificationType.SURVEY_PUBLISHED, event.getSurveyId());
            }
        }
    }

    @Transactional
    public void processSurveyReminder(SurveyReminderEvent event) {
        log.info("Processing SurveyReminderEvent for {} employees", event.getEmployeeIds().size());
        
        // In a real app we might fetch these users in bulk, but for hackathon we might just have the emails or we fetch the whole region and filter.
        List<EmployeeFeignClient.EmployeeInternalDTO> allRegionEmployees = employeeFeignClient.getEmployeesByRegion(event.getRegion());
        
        for (var emp : allRegionEmployees) {
            if (event.getEmployeeIds().contains(emp.getEmployeeId())) {
                String title = "Reminder: " + event.getTitle();
                String message = "You have pending survey assignments. Please complete them as soon as possible.";
                
                saveAndSendNotification(emp.getEmployeeId(), emp.getEmail(), title, message, NotificationType.SURVEY_REMINDER, event.getSurveyId());
            }
        }
    }

    @Transactional
    public void processReportGenerated(ReportGeneratedEvent event) {
        log.info("Processing ReportGeneratedEvent for Survey: {}", event.getSurveyId());
        
        String title = "Monthly AI Report Ready";
        String message = "The AI analysis for the " + event.getRegion() + " region has completed. Please review the report on your dashboard.";
        
        // 1. Regional HR
        List<EmployeeFeignClient.EmployeeInternalDTO> regionalHrs = employeeFeignClient.getRegionalHr(event.getRegion());
        regionalHrs.forEach(hr -> saveAndSendNotification(hr.getEmployeeId(), hr.getEmail(), title, message, NotificationType.REPORT_GENERATED, event.getSurveyId()));
        
        // 2. Global HR
        List<EmployeeFeignClient.EmployeeInternalDTO> globalHrs = employeeFeignClient.getGlobalHr();
        globalHrs.forEach(hr -> saveAndSendNotification(hr.getEmployeeId(), hr.getEmail(), title, message, NotificationType.REPORT_GENERATED, event.getSurveyId()));
        
        // 3. VP
        List<EmployeeFeignClient.EmployeeInternalDTO> vps = employeeFeignClient.getVp();
        vps.forEach(vp -> saveAndSendNotification(vp.getEmployeeId(), vp.getEmail(), title, message, NotificationType.REPORT_GENERATED, event.getSurveyId()));
    }
    
    @Transactional
    public void processSurveyCompleted(SurveyCompletedEvent event) {
        log.info("Processing SurveyCompletedEvent for Survey ID: {}, Employee ID: {}", event.getSurveyId(), event.getEmployeeId());
        
        List<Notification> notifications = notificationRepository.findByRecipientIdAndReferenceId(event.getEmployeeId(), event.getSurveyId());
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }
    
    private void saveAndSendNotification(Long employeeId, String email, String title, String message, NotificationType type, Long referenceId) {
        Notification notification = new Notification();
        notification.setRecipientId(employeeId);
        notification.setRecipientEmail(email);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setStatus("PENDING");
        notification.setReferenceId(referenceId);
        
        notification = notificationRepository.save(notification);
        
        try {
            emailService.sendEmail(email, title, message);
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus("FAILED");
            log.error("Failed to send email to {}", email, e);
        }
        
        notificationRepository.save(notification);
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationService.class);
    public NotificationService(NotificationRepository notificationRepository, EmployeeFeignClient employeeFeignClient, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.employeeFeignClient = employeeFeignClient;
        this.emailService = emailService;
    }
}
