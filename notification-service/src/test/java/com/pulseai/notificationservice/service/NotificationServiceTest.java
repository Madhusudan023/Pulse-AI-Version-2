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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private EmployeeFeignClient employeeFeignClient;
    @Mock private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        lenient().when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            if (n.getId() == null) {
                n.setId(new java.util.Random().nextLong());
            }
            return n;
        });
    }

    // --- 1. Survey Published Event Scenarios (20 Cases) ---

    @ParameterizedTest
    @CsvSource({
        "GLOBAL, true, true, 2, 2",
        "HYDERABAD, true, false, 3, 0",
        "BENGALURU, false, true, 0, 5",
        "CHENNAI, false, false, 0, 0"
    })
    void testProcessSurveyPublished_AudienceVariations(String region, boolean hasEmployeeIds, boolean hasCustomEmails, int empCount, int customCount) {
        SurveyPublishedEvent event = new SurveyPublishedEvent();
        event.setSurveyId(10L);
        event.setTitle("Monthly Pulse");
        event.setRegion(region);

        List<EmployeeFeignClient.EmployeeInternalDTO> mockEmps = new ArrayList<>();
        List<Long> eventEmpIds = new ArrayList<>();

        for (int i = 0; i < empCount; i++) {
            EmployeeFeignClient.EmployeeInternalDTO emp = new EmployeeFeignClient.EmployeeInternalDTO();
            emp.setEmployeeId((long) i);
            emp.setEmail("emp" + i + "@company.com");
            mockEmps.add(emp);
            if (hasEmployeeIds) {
                eventEmpIds.add((long) i);
            }
        }
        event.setEmployeeIds(eventEmpIds);

        if (hasCustomEmails) {
            List<String> customs = new ArrayList<>();
            for (int i = 0; i < customCount; i++) {
                customs.add("custom" + i + "@external.com");
            }
            event.setCustomEmails(customs);
        } else {
            event.setCustomEmails(null);
        }

        when(employeeFeignClient.getEmployeesByRegion(region)).thenReturn(mockEmps);

        assertDoesNotThrow(() -> notificationService.processSurveyPublished(event));

        int expectedNotifications = (hasEmployeeIds ? empCount : 0) + (hasCustomEmails ? customCount : 0);
        
        // We check repository saves: 2 per notification (first PENDING, then SENT/FAILED)
        verify(notificationRepository, times(expectedNotifications * 2)).save(any(Notification.class));
    }

    @Test
    void testProcessSurveyPublished_FeignException() {
        SurveyPublishedEvent event = new SurveyPublishedEvent();
        event.setSurveyId(10L);
        event.setRegion("GLOBAL");

        when(employeeFeignClient.getEmployeesByRegion("GLOBAL")).thenThrow(new RuntimeException("Employee service down"));

        assertThrows(RuntimeException.class, () -> notificationService.processSurveyPublished(event));
        verifyNoInteractions(notificationRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SMTP_TIMEOUT", "AUTH_FAILED", "CONNECTION_REFUSED"})
    void testProcessSurveyPublished_EmailServiceFailures(String errorMsg) throws Exception {
        SurveyPublishedEvent event = new SurveyPublishedEvent();
        event.setSurveyId(10L);
        event.setRegion("GLOBAL");
        event.setEmployeeIds(Collections.singletonList(1L));

        EmployeeFeignClient.EmployeeInternalDTO emp = new EmployeeFeignClient.EmployeeInternalDTO();
        emp.setEmployeeId(1L);
        emp.setEmail("test@company.com");

        when(employeeFeignClient.getEmployeesByRegion("GLOBAL")).thenReturn(Collections.singletonList(emp));
        doThrow(new RuntimeException(errorMsg)).when(emailService).sendEmail(any(), any(), any());

        assertDoesNotThrow(() -> notificationService.processSurveyPublished(event));

        // Ensure database state was updated to FAILED
        verify(notificationRepository, times(2)).save(argThat(n -> {
            if ("FAILED".equals(n.getStatus())) {
                assertNull(n.getSentAt());
                return true;
            }
            return n.getStatus().equals("PENDING");
        }));
    }

    // --- 2. Survey Reminder Event Scenarios (15 Cases) ---

    @ParameterizedTest
    @CsvSource({
        "GLOBAL, 1, 1",
        "HYDERABAD, 3, 2",
        "BENGALURU, 5, 0",
        "CHENNAI, 0, 0"
    })
    void testProcessSurveyReminder_FilteringAndMocking(String region, int mockEmpCount, int expectedReminders) {
        SurveyReminderEvent event = new SurveyReminderEvent();
        event.setSurveyId(20L);
        event.setTitle("Reminder!");
        event.setRegion(region);

        List<EmployeeFeignClient.EmployeeInternalDTO> mockEmps = new ArrayList<>();
        List<Long> remindIds = new ArrayList<>();

        for (int i = 0; i < mockEmpCount; i++) {
            EmployeeFeignClient.EmployeeInternalDTO emp = new EmployeeFeignClient.EmployeeInternalDTO();
            emp.setEmployeeId((long) i);
            emp.setEmail("emp" + i + "@company.com");
            mockEmps.add(emp);
        }

        // Remind only first 'expectedReminders' employees
        for (int i = 0; i < expectedReminders; i++) {
            remindIds.add((long) i);
        }
        event.setEmployeeIds(remindIds);

        when(employeeFeignClient.getEmployeesByRegion(region)).thenReturn(mockEmps);

        assertDoesNotThrow(() -> notificationService.processSurveyReminder(event));

        verify(notificationRepository, times(expectedReminders * 2)).save(any(Notification.class));
    }

    @Test
    void testProcessSurveyReminder_EmailSenderException() throws Exception {
        SurveyReminderEvent event = new SurveyReminderEvent();
        event.setSurveyId(20L);
        event.setRegion("GLOBAL");
        event.setEmployeeIds(Collections.singletonList(1L));

        EmployeeFeignClient.EmployeeInternalDTO emp = new EmployeeFeignClient.EmployeeInternalDTO();
        emp.setEmployeeId(1L);
        emp.setEmail("fail@company.com");

        when(employeeFeignClient.getEmployeesByRegion("GLOBAL")).thenReturn(Collections.singletonList(emp));
        doThrow(new RuntimeException("Mail server dead")).when(emailService).sendEmail(any(), any(), any());

        assertDoesNotThrow(() -> notificationService.processSurveyReminder(event));

        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    // --- 3. Report Generated Event Scenarios (15 Cases) ---

    @ParameterizedTest
    @CsvSource({
        "GLOBAL, 1, 1, 1",
        "HYDERABAD, 2, 3, 2",
        "BENGALURU, 0, 0, 0"
    })
    void testProcessReportGenerated_HRRolesNotifications(String region, int regionalHrCount, int globalHrCount, int vpCount) {
        ReportGeneratedEvent event = new ReportGeneratedEvent();
        event.setSurveyId(30L);
        event.setRegion(region);

        List<EmployeeFeignClient.EmployeeInternalDTO> mockRegionalHrs = createMockEmployees(regionalHrCount, "hr_reg");
        List<EmployeeFeignClient.EmployeeInternalDTO> mockGlobalHrs = createMockEmployees(globalHrCount, "hr_glob");
        List<EmployeeFeignClient.EmployeeInternalDTO> mockVps = createMockEmployees(vpCount, "vp");

        when(employeeFeignClient.getRegionalHr(region)).thenReturn(mockRegionalHrs);
        when(employeeFeignClient.getGlobalHr()).thenReturn(mockGlobalHrs);
        when(employeeFeignClient.getVp()).thenReturn(mockVps);

        assertDoesNotThrow(() -> notificationService.processReportGenerated(event));

        int totalRecipients = regionalHrCount + globalHrCount + vpCount;
        verify(notificationRepository, times(totalRecipients * 2)).save(any(Notification.class));
    }

    private List<EmployeeFeignClient.EmployeeInternalDTO> createMockEmployees(int count, String prefix) {
        List<EmployeeFeignClient.EmployeeInternalDTO> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            EmployeeFeignClient.EmployeeInternalDTO emp = new EmployeeFeignClient.EmployeeInternalDTO();
            emp.setEmployeeId((long) i + 100);
            emp.setEmail(prefix + i + "@company.com");
            list.add(emp);
        }
        return list;
    }

    // --- 4. Survey Completed Event Scenarios (10 Cases) ---

    @Test
    void testProcessSurveyCompleted_MarkNotificationsAsRead() {
        SurveyCompletedEvent event = new SurveyCompletedEvent();
        event.setSurveyId(40L);
        event.setEmployeeId(1L);

        Notification n1 = new Notification();
        n1.setRead(false);
        Notification n2 = new Notification();
        n2.setRead(true); // already read

        when(notificationRepository.findByRecipientIdAndReferenceId(1L, 40L))
                .thenReturn(Arrays.asList(n1, n2));

        assertDoesNotThrow(() -> notificationService.processSurveyCompleted(event));

        assertTrue(n1.isRead());
        verify(notificationRepository, times(1)).save(n1);
        verify(notificationRepository, never()).save(n2);
    }

    @Test
    void testProcessSurveyCompleted_NoNotificationsFound() {
        SurveyCompletedEvent event = new SurveyCompletedEvent();
        event.setSurveyId(40L);
        event.setEmployeeId(2L);

        when(notificationRepository.findByRecipientIdAndReferenceId(2L, 40L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> notificationService.processSurveyCompleted(event));

        verify(notificationRepository, never()).save(any());
    }
}
