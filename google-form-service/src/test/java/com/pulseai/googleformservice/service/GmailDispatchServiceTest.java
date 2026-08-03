package com.pulseai.googleformservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GmailDispatchServiceTest {

    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private GmailDispatchService gmailDispatchService;

    @BeforeEach
    void setUp() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testDispatchEmails_Success() {
        List<String> emails = Arrays.asList("test1@virtusa.com", "test2@virtusa.com");
        String formUrl = "http://form.link";
        String title = "Pulse Survey";

        assertDoesNotThrow(() -> gmailDispatchService.dispatchEmails(emails, formUrl, title));
        
        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @ParameterizedTest
    @CsvSource({
        "true, SUCCESS",
        "false, SMTP_TIMEOUT",
        "false, AUTH_FAILED"
    })
    void testDispatchEmails_HandlesSMTPFailures(boolean success, String failureType) {
        List<String> emails = Arrays.asList("valid@test.com");
        String formUrl = "http://form.link";
        String title = "Test";

        if (!success) {
            doThrow(new RuntimeException(failureType)).when(mailSender).send(any(MimeMessage.class));
        } else {
            doNothing().when(mailSender).send(any(MimeMessage.class));
        }

        // Processing should NEVER crash the main thread; it logs the error
        assertDoesNotThrow(() -> gmailDispatchService.dispatchEmails(emails, formUrl, title));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
    
    @Test
    void testDispatchEmails_StressTest_1000Emails() {
        List<String> emails = Arrays.asList(new String[1000]);
        for (int i = 0; i < 1000; i++) emails.set(i, "user" + i + "@test.com");
        
        assertDoesNotThrow(() -> gmailDispatchService.dispatchEmails(emails, "url", "title"));
        
        verify(mailSender, times(1000)).send(any(MimeMessage.class));
    }
}
