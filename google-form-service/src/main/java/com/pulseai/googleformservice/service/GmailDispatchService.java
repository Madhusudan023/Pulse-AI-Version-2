package com.pulseai.googleformservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GmailDispatchService {
    private static final Logger log = LoggerFactory.getLogger(GmailDispatchService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void dispatchEmails(List<String> emails, String formUrl, String title) {
        for (String email : emails) {
            log.info("Sending email to {} with link: {}", email, formUrl);
            try {
                jakarta.mail.internet.MimeMessage message = javaMailSender.createMimeMessage();
                org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
                
                helper.setFrom("hr@pulseai.com", "Pulse AI HR");
                helper.setTo(email);
                helper.setSubject("New Pulse Survey: " + title);
                
                String htmlBody = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                                  "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 8px;'>" +
                                  "<div style='text-align: center; margin-bottom: 20px;'>" +
                                  "<h2 style='color: #2c3e50; margin: 0;'>New Pulse Survey: " + title + "</h2>" +
                                  "</div>" +
                                  "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>" +
                                  "<p style='margin-top: 0;'>Hello Virtusians,</p>" +
                                  "<p>We are conducting our Employee Survey to better understand your experiences, gather valuable feedback, and identify opportunities to improve our workplace.</p>" +
                                  "<p>Your honest input will help us create a more supportive, engaging, and productive environment for everyone.</p>" +
                                  "<p style='text-align: center; margin: 25px 0;'>" +
                                  "<a href='" + formUrl + "' style='background-color: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Take the Survey Now</a>" +
                                  "</p>" +
                                  "<p>Or copy and paste this link into your browser: <br><a href='" + formUrl + "'>" + formUrl + "</a></p>" +
                                  "<p>All responses will be treated confidentially and will be used solely for organizational improvement purposes.</p>" +
                                  "<p>Thank you for taking the time to share your feedback. Your participation is greatly appreciated.</p>" +
                                  "</div>" +
                                  "<div style='border-top: 1px solid #eee; padding-top: 15px; margin-top: 20px; font-size: 14px; color: #7f8c8d;'>" +
                                  "<p style='margin: 0;'>Best Regards,</p>" +
                                  "<p style='margin: 5px 0 0 0;'><b>The Pulse AI HR Team</b></p>" +
                                  "</div>" +
                                  "</div>" +
                                  "</body></html>";
                
                helper.setText(htmlBody, true);
                
                javaMailSender.send(message);
                log.info("Email sent successfully to {}", email);
            } catch (Exception e) {
                log.error("Failed to send email to {}", email, e);
            }
        }
    }
}
