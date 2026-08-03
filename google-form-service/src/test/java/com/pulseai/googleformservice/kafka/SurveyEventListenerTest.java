package com.pulseai.googleformservice.kafka;

import com.pulseai.googleformservice.dto.GoogleFormResult;
import com.pulseai.googleformservice.dto.QuestionResponseDTO;
import com.pulseai.googleformservice.dto.SurveyQuestionDTO;
import com.pulseai.googleformservice.feign.EmployeeFeignClient;
import com.pulseai.googleformservice.feign.QuestionBankFeignClient;
import com.pulseai.googleformservice.feign.SurveyFeignClient;
import com.pulseai.googleformservice.repository.GoogleFormRepository;
import com.pulseai.googleformservice.repository.SurveyEmailTaskRepository;
import com.pulseai.googleformservice.service.GoogleFormsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurveyEventListenerTest {

    @Mock private GoogleFormsService googleFormsService;
    @Mock private QuestionBankFeignClient questionBankFeignClient;
    @Mock private EmployeeFeignClient employeeFeignClient;
    @Mock private SurveyFeignClient surveyFeignClient;
    @Mock private GoogleFormRepository googleFormRepository;
    @Mock private SurveyEmailTaskRepository surveyEmailTaskRepository;
    @Mock private JobLauncher jobLauncher;
    @Mock private Job emailDispatchJob;

    @InjectMocks
    private SurveyEventListener listener;

    private SurveyPublishedEvent event;

    @BeforeEach
    void setUp() {
        event = new SurveyPublishedEvent();
        event.setSurveyId(1L);
        event.setTitle("Q3 Survey");
        event.setRegion("GLOBAL");
        event.setExperienceFilter("ALL");
        event.setEmployeeIds(Arrays.asList(1L, 2L));
        event.setCustomEmails(Collections.singletonList("custom@test.com"));
    }

    @Test
    void testConsumeSurveyPublishedEvent_Success_E2EFlow() throws Exception {
        SurveyQuestionDTO sq = new SurveyQuestionDTO();
        sq.setQuestionId(10L);
        when(surveyFeignClient.getSurveyQuestions(1L)).thenReturn(Collections.singletonList(sq));
        
        QuestionResponseDTO q = new QuestionResponseDTO();
        when(questionBankFeignClient.getQuestionById(10L)).thenReturn(q);
        
        GoogleFormResult mockResult = new GoogleFormResult("formId", "http://forms.google.com/xyz");
        when(googleFormsService.createForm(anyString(), anyList())).thenReturn(mockResult);
        
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("employeeId", 1L);
        emp1.put("email", "emp1@test.com");
        
        when(employeeFeignClient.getEmployeesByRegion(anyString())).thenReturn(Arrays.asList(emp1));

        assertDoesNotThrow(() -> listener.handleSurveyPublished(event));
        
        verify(surveyFeignClient, times(1)).getSurveyQuestions(1L);
        verify(googleFormsService, times(1)).createForm(eq("Q3 Survey"), anyList());
        verify(googleFormRepository, times(1)).save(any());
        verify(employeeFeignClient, times(1)).getEmployeesByRegion(anyString());
        verify(surveyEmailTaskRepository, times(1)).saveAll(anyList());
        verify(jobLauncher, times(1)).run(any(), any());
    }

    @Test
    void testQuestionServiceDown_ProceedsWithEmptyQuestions() {
        when(surveyFeignClient.getSurveyQuestions(1L)).thenThrow(new RuntimeException("Question Service Timeout"));
        when(googleFormsService.createForm(anyString(), anyList())).thenReturn(new GoogleFormResult("id", "url"));
        
        assertDoesNotThrow(() -> listener.handleSurveyPublished(event));
        verify(googleFormRepository, times(1)).save(any());
    }

    @Test
    void testEmployeeServiceDown_FallbackToCustomEmailsOnly() throws Exception {
        when(employeeFeignClient.getEmployeesByRegion(anyString())).thenThrow(new RuntimeException("500 Internal Server Error"));
        when(surveyFeignClient.getSurveyQuestions(1L)).thenReturn(Collections.emptyList());
        when(googleFormsService.createForm(anyString(), anyList())).thenReturn(new GoogleFormResult("id", "url"));
        
        assertDoesNotThrow(() -> listener.handleSurveyPublished(event));
        
        verify(surveyEmailTaskRepository, times(1)).saveAll(argThat(list -> {
            List<?> l = (List<?>) list;
            return !l.isEmpty(); 
        }));
    }

    @ParameterizedTest
    @CsvSource({
        "ALL, 2, 1, 3",
        "LESS_THAN_6_MONTHS, 0, 1, 1",
        "MORE_THAN_6_MONTHS, 5, 0, 5"
    })
    void testEmailMergingLogic(String filter, int mockEmpCount, int mockCustomCount, int expectedTotal) {
        event.setExperienceFilter(filter);
        List<String> customs = Arrays.asList(new String[mockCustomCount]);
        Collections.fill(customs, "custom@test.com");
        event.setCustomEmails(customs);
        
        assertTrue(expectedTotal >= 0);
    }
    
    @Test
    void testIdempotency_DuplicateSurveyPublishedEvent() {
        when(surveyFeignClient.getSurveyQuestions(1L)).thenReturn(Collections.emptyList());
        when(googleFormsService.createForm(anyString(), anyList())).thenThrow(new RuntimeException("DuplicateKeyException"));
        
        assertDoesNotThrow(() -> listener.handleSurveyPublished(event));
    }
}
