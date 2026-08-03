package com.pulseai.surveyservice.service;

import com.pulseai.surveyservice.dto.request.AnswerRequest;
import com.pulseai.surveyservice.dto.request.SubmitSurveyRequest;
import com.pulseai.surveyservice.entity.Answer;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.entity.SurveyAssignment;
import com.pulseai.surveyservice.entity.SurveyResponse;
import com.pulseai.surveyservice.enums.AssignmentStatus;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.exception.BusinessException;
import com.pulseai.surveyservice.repository.AnswerRepository;
import com.pulseai.surveyservice.repository.SurveyAssignmentRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurveyEmployeeServiceTest {

    @Mock private SurveyRepository surveyRepository;
    @Mock private SurveyAssignmentRepository surveyAssignmentRepository;
    @Mock private SurveyResponseRepository surveyResponseRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private SurveyEmployeeService employeeService;

    private Survey survey;
    private SurveyAssignment assignment;
    private SubmitSurveyRequest submitRequest;

    @BeforeEach
    void setUp() {
        survey = new Survey();
        survey.setId(10L);
        survey.setStatus(SurveyStatus.ACTIVE);
        survey.setAnonymous(false);
        survey.setCompletedParticipants(0);

        assignment = new SurveyAssignment();
        assignment.setId(100L);
        assignment.setSurveyId(10L);
        assignment.setEmployeeId(1L);
        assignment.setStatus(AssignmentStatus.PENDING);

        submitRequest = new SubmitSurveyRequest();
        submitRequest.setResponseDuration("5m 30s");
        
        AnswerRequest ans = new AnswerRequest();
        ans.setQuestionId(1L);
        ans.setRatingAnswer(5);
        submitRequest.setAnswers(Collections.singletonList(ans));
    }

    // --- 6. Anonymous Survey Tests ---

    @Test
    void submitSurvey_Anonymous_DoesNotStoreIdentity() {
        survey.setAnonymous(true);
        when(surveyRepository.findById(10L)).thenReturn(Optional.of(survey));
        when(surveyAssignmentRepository.findBySurveyIdAndEmployeeId(10L, 1L)).thenReturn(Optional.of(assignment));
        
        SurveyResponse savedResponse = new SurveyResponse();
        savedResponse.setId(500L);
        when(surveyResponseRepository.save(any(SurveyResponse.class))).thenReturn(savedResponse);

        employeeService.submitSurvey(10L, 1L, "emp@test.com", submitRequest);

        ArgumentCaptor<SurveyResponse> responseCaptor = ArgumentCaptor.forClass(SurveyResponse.class);
        verify(surveyResponseRepository, times(1)).save(responseCaptor.capture());
        
        SurveyResponse captured = responseCaptor.getValue();
        assertNull(captured.getEmployeeId(), "Anonymous survey should NOT store employee ID");
        assertNull(captured.getEmployeeEmail(), "Anonymous survey should NOT store employee email");
        
        assertEquals(AssignmentStatus.COMPLETED, assignment.getStatus());
        verify(answerRepository, times(1)).save(any(Answer.class));
    }

    // --- 7. Non-Anonymous Survey Tests ---

    @Test
    void submitSurvey_NonAnonymous_StoresIdentity() {
        survey.setAnonymous(false);
        when(surveyRepository.findById(10L)).thenReturn(Optional.of(survey));
        when(surveyAssignmentRepository.findBySurveyIdAndEmployeeId(10L, 1L)).thenReturn(Optional.of(assignment));
        
        SurveyResponse savedResponse = new SurveyResponse();
        savedResponse.setId(500L);
        when(surveyResponseRepository.save(any(SurveyResponse.class))).thenReturn(savedResponse);

        employeeService.submitSurvey(10L, 1L, "emp@test.com", submitRequest);

        ArgumentCaptor<SurveyResponse> responseCaptor = ArgumentCaptor.forClass(SurveyResponse.class);
        verify(surveyResponseRepository, times(1)).save(responseCaptor.capture());
        
        SurveyResponse captured = responseCaptor.getValue();
        assertEquals(1L, captured.getEmployeeId());
        assertEquals("emp@test.com", captured.getEmployeeEmail());
    }

    // --- 8. Employee Participation & Edge Cases ---

    @Test
    void submitSurvey_MultipleSubmissions_ThrowsException() {
        assignment.setStatus(AssignmentStatus.COMPLETED); // Already submitted
        
        when(surveyRepository.findById(10L)).thenReturn(Optional.of(survey));
        when(surveyAssignmentRepository.findBySurveyIdAndEmployeeId(10L, 1L)).thenReturn(Optional.of(assignment));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            employeeService.submitSurvey(10L, 1L, "emp@test.com", submitRequest);
        });

        assertEquals("You have already submitted this survey.", exception.getMessage());
        verify(surveyResponseRepository, never()).save(any());
    }

    @Test
    void submitSurvey_InactiveSurvey_ThrowsException() {
        survey.setStatus(SurveyStatus.CLOSED); // Survey expired/closed
        when(surveyRepository.findById(10L)).thenReturn(Optional.of(survey));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            employeeService.submitSurvey(10L, 1L, "emp@test.com", submitRequest);
        });

        assertEquals("Survey is not currently active.", exception.getMessage());
    }

    @Test
    void submitSurvey_UnassignedEmployee_ThrowsException() {
        when(surveyRepository.findById(10L)).thenReturn(Optional.of(survey));
        when(surveyAssignmentRepository.findBySurveyIdAndEmployeeId(10L, 1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            employeeService.submitSurvey(10L, 1L, "emp@test.com", submitRequest);
        });

        assertEquals("You are not assigned to this survey.", exception.getMessage());
    }
}
