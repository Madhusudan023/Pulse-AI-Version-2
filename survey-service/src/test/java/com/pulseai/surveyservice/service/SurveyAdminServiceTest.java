package com.pulseai.surveyservice.service;

import com.pulseai.surveyservice.client.EmployeeFeignClient;
import com.pulseai.surveyservice.client.QuestionBankFeignClient;
import com.pulseai.surveyservice.client.SentimentFeignClient;
import com.pulseai.surveyservice.dto.event.SurveyPublishedEvent;
import com.pulseai.surveyservice.dto.request.AddQuestionsBulkRequest;
import com.pulseai.surveyservice.dto.request.CreateSurveyRequest;
import com.pulseai.surveyservice.dto.response.EmployeeInternalResponse;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.entity.SurveyAssignment;
import com.pulseai.surveyservice.enums.AssignmentStatus;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.enums.SurveyType;
import com.pulseai.surveyservice.exception.BusinessException;
import com.pulseai.surveyservice.exception.ResourceNotFoundException;
import com.pulseai.surveyservice.repository.AnswerRepository;
import com.pulseai.surveyservice.repository.SurveyAssignmentRepository;
import com.pulseai.surveyservice.repository.SurveyQuestionRepository;
import com.pulseai.surveyservice.repository.SurveyRepository;
import com.pulseai.surveyservice.repository.SurveyResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurveyAdminServiceTest {

    @Mock private SurveyRepository surveyRepository;
    @Mock private SurveyQuestionRepository surveyQuestionRepository;
    @Mock private SurveyAssignmentRepository surveyAssignmentRepository;
    @Mock private EmployeeFeignClient employeeFeignClient;
    @Mock private QuestionBankFeignClient questionBankFeignClient;
    @Mock private SentimentFeignClient sentimentFeignClient;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private SurveyResponseRepository surveyResponseRepository;
    @Mock private AnswerRepository answerRepository;

    @InjectMocks
    private SurveyAdminService adminService;

    private CreateSurveyRequest createRequest;
    private Survey survey;

    @BeforeEach
    void setUp() {
        createRequest = new CreateSurveyRequest();
        createRequest.setTitle("Q3 Engagement");
        createRequest.setDescription("Test Description");
        createRequest.setRegion("PUNE");
        createRequest.setSurveyType(SurveyType.MONTHLY_PULSE);
        createRequest.setMonth(7);
        createRequest.setYear(2026);
        createRequest.setTargetAudience("ALL");
        createRequest.setAnonymous(true);
        createRequest.setStartDate(LocalDateTime.now().minusDays(1)); // started yesterday
        createRequest.setEndDate(LocalDateTime.now().plusDays(5));

        survey = new Survey();
        survey.setId(1L);
        survey.setTitle(createRequest.getTitle());
        survey.setRegion(createRequest.getRegion());
        survey.setTargetAudience(createRequest.getTargetAudience());
        survey.setAnonymous(createRequest.isAnonymous());
        survey.setStatus(SurveyStatus.DRAFT);
        survey.setStartDate(createRequest.getStartDate());
        survey.setEndDate(createRequest.getEndDate());
        survey.setSurveyType(createRequest.getSurveyType());
    }

    // --- 1. Survey Creation Tests ---

    @Test
    void createSurvey_Anonymous_Success() {
        when(surveyRepository.save(any(Survey.class))).thenAnswer(i -> {
            Survey s = i.getArgument(0);
            s.setId(1L);
            return s;
        });
        
        Survey created = adminService.createSurvey(createRequest);
        
        assertNotNull(created);
        assertEquals(SurveyStatus.DRAFT, created.getStatus());
        assertTrue(created.isAnonymous());
        verify(surveyRepository, times(1)).save(any(Survey.class));
    }

    @Test
    void createSurvey_NonAnonymous_Success() {
        createRequest.setAnonymous(false);
        when(surveyRepository.save(any(Survey.class))).thenAnswer(i -> {
            Survey s = i.getArgument(0);
            s.setId(1L);
            return s;
        });
        
        Survey created = adminService.createSurvey(createRequest);
        assertFalse(created.isAnonymous());
    }

    // --- 2. Survey Validation Tests (Business Logic in Update/Publish) ---

    @Test
    void updateSurvey_NotDraft_ThrowsException() {
        survey.setStatus(SurveyStatus.ACTIVE);
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        
        assertThrows(BusinessException.class, () -> adminService.updateSurvey(1L, createRequest));
    }

    // --- 3. Question Selection Tests ---

    @Test
    void addQuestionsBulk_Success() {
        AddQuestionsBulkRequest req = new AddQuestionsBulkRequest();
        req.setQuestionIds(Arrays.asList(10L, 11L));
        
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        
        adminService.addQuestionsBulk(1L, req);
        
        verify(questionBankFeignClient, times(2)).getQuestionById(anyLong());
        verify(surveyQuestionRepository, times(2)).save(any());
    }

    @Test
    void addQuestionsBulk_NotDraft_ThrowsException() {
        survey.setStatus(SurveyStatus.ACTIVE);
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        
        AddQuestionsBulkRequest req = new AddQuestionsBulkRequest();
        req.setQuestionIds(List.of(10L));
        
        assertThrows(BusinessException.class, () -> adminService.addQuestionsBulk(1L, req));
    }

    @Test
    void removeQuestions_Success() {
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        
        adminService.removeQuestionsFromSurvey(1L, Arrays.asList(10L));
        
        verify(surveyQuestionRepository, times(1)).deleteBySurveyIdAndQuestionIdIn(1L, Arrays.asList(10L));
    }

    // --- 5. Publish Survey Tests ---

    @Test
    void publishSurvey_Active_KafkaEventEmitted() {
        EmployeeInternalResponse emp = new EmployeeInternalResponse();
        emp.setEmployeeId(100L);
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        when(employeeFeignClient.getEmployeesByRegion("PUNE")).thenReturn(List.of(emp));
        when(surveyAssignmentRepository.findBySurveyIdAndEmployeeId(1L, 100L)).thenReturn(Optional.empty());
        when(surveyAssignmentRepository.findBySurveyId(1L)).thenReturn(List.of(new SurveyAssignment()));
        
        adminService.publishSurvey(1L, null);
        
        assertEquals(SurveyStatus.ACTIVE, survey.getStatus());
        verify(surveyAssignmentRepository, times(1)).save(any(SurveyAssignment.class));
        verify(surveyRepository, times(1)).save(survey);
        
        ArgumentCaptor<SurveyPublishedEvent> eventCaptor = ArgumentCaptor.forClass(SurveyPublishedEvent.class);
        verify(kafkaTemplate, times(1)).send(eq("survey-published"), anyString(), eventCaptor.capture());
        
        SurveyPublishedEvent event = eventCaptor.getValue();
        assertEquals(1L, event.getSurveyId());
        assertTrue(event.getEmployeeIds().contains(100L));
    }

    @Test
    void publishSurvey_Scheduled_NoEventEmittedImmediately() {
        survey.setStartDate(LocalDateTime.now().plusDays(2)); // Future start date
        
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        when(employeeFeignClient.getEmployeesByRegion("PUNE")).thenReturn(Collections.emptyList());
        
        adminService.publishSurvey(1L, null);
        
        assertEquals(SurveyStatus.SCHEDULED, survey.getStatus());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    // --- 9. Survey Closing Tests ---

    @Test
    void closeSurvey_Success_EventEmitted() {
        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        
        adminService.closeSurvey(1L);
        
        assertEquals(SurveyStatus.CLOSED, survey.getStatus());
        assertNotNull(survey.getClosedAt());
        verify(surveyRepository, times(1)).save(survey);
        verify(kafkaTemplate, times(1)).send(eq("survey-closed-events"), anyString(), any());
    }

}
