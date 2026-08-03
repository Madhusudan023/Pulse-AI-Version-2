package com.pulseai.notificationservice.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("hr@pulseai.com", "Pulse AI HR");
            helper.setTo(to);
            helper.setSubject(subject);
            
            String htmlBody = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                              "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 8px;'>" +
                              "<div style='text-align: center; margin-bottom: 20px;'>" +
                              "<h2 style='color: #2c3e50; margin: 0;'>" + subject + "</h2>" +
                              "</div>" +
                              "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>" +
                              "<p style='margin: 0;'>" + body + "</p>" +
                              "</div>" +
                              "<div style='border-top: 1px solid #eee; padding-top: 15px; margin-top: 20px; font-size: 14px; color: #7f8c8d;'>" +
                              "<p style='margin: 0;'>Best Regards,</p>" +
                              "<p style='margin: 5px 0 0 0;'><b>The Pulse AI HR Team</b></p>" +
                              "</div>" +
                              "</div>" +
                              "</body></html>";
            
            helper.setText(htmlBody, true);
            
            mailSender.send(message);
            log.info("Email successfully sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
            throw new RuntimeException("Email delivery failed", e);
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailService.class);
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}
