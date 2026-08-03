package com.pulseai.notificationservice.entity;

import com.pulseai.notificationservice.constant.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    private Long recipientId;
    private String recipientEmail;
    
    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String status; // PENDING, SENT, FAILED
    
    private Long referenceId; // Associated Survey ID
    
    private boolean isRead = false;
    
    private LocalDateTime sentAt;
    public Long getRecipientId() { return this.recipientId; }
    public String getRecipientEmail() { return this.recipientEmail; }
    public String getTitle() { return this.title; }
    public String getMessage() { return this.message; }
    public NotificationType getNotificationType() { return this.notificationType; }
    public boolean isIsRead() { return this.isRead; }
    public LocalDateTime getSentAt() { return this.sentAt; }
    public String getStatus() { return this.status; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }
    public void setRead(boolean read) { this.isRead = read; }
    public boolean isRead() { return this.isRead; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public void setStatus(String status) { this.status = status; }
    public Long getReferenceId() { return this.referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
}
