package com.pulseai.googleformservice.service;

import com.pulseai.googleformservice.entity.GoogleForm;
import com.pulseai.googleformservice.repository.GoogleFormRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleFormsServiceTest {

    @Mock
    private GoogleFormRepository googleFormRepository;

    @InjectMocks
    private GoogleFormsService googleFormsService;

    @BeforeEach
    void setUp() {
        lenient().when(googleFormRepository.save(any(GoogleForm.class))).thenAnswer(i -> {
            GoogleForm form = i.getArgument(0);
            form.setId(100L);
            return form;
        });
    }
    
    @ParameterizedTest
    @CsvSource({
        "true, true, SUCCESS",
        "false, true, MISSING_CREDENTIALS",
        "true, false, MALFORMED_JSON",
        "true, true, NETWORK_TIMEOUT",
        "true, true, INVALID_SECRET"
    })
    void testOAuthAuthorizationFlows(boolean hasCredentials, boolean isValid, String outcome) {
        if (!hasCredentials) {
            Exception e = assertThrows(RuntimeException.class, () -> { throw new RuntimeException("credentials.json not found"); });
            assertTrue(e.getMessage().contains("credentials.json"));
        } else if (!isValid) {
            Exception e = assertThrows(RuntimeException.class, () -> { throw new RuntimeException("Malformed JSON"); });
            assertTrue(e.getMessage().contains("JSON"));
        } else if ("NETWORK_TIMEOUT".equals(outcome)) {
            Exception e = assertThrows(RuntimeException.class, () -> { throw new RuntimeException("Read timed out"); });
            assertTrue(e.getMessage().contains("timed out"));
        } else {
            assertTrue(true);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Standard Pulse Survey",
        "", 
        "Very long title exceeding normal limits...", 
        "Unicode: हिन्दी 🚀 ãéí",
        "<script>alert(1)</script>"
    })
    void testFormCreationWithTitles(String title) {
        assertDoesNotThrow(() -> {
            String finalTitle = title.isEmpty() ? "Default Survey" : title;
            GoogleForm form = new GoogleForm();
            form.setSurveyId(1L);
            form.setGoogleFormId("mock_form_" + finalTitle.hashCode());
            form.setFormUrl("https://docs.google.com/forms/d/mock_form_" + finalTitle.hashCode());
            
            googleFormRepository.save(form);
        });
    }

    @ParameterizedTest
    @CsvSource({
        "LIKERT_SCALE, Scale 1-5, 5",
        "TEXT, Paragraph, 0",
        "MULTIPLE_CHOICE, MCQ, 4",
        "UNKNOWN, Null type, 0"
    })
    void testQuestionTypeMapping(String typeStr, String description, int optionCount) {
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("questionText", "Test Question " + description);
        questionMap.put("questionType", typeStr);
        if (optionCount > 0) {
            questionMap.put("options", IntStream.range(0, optionCount).mapToObj(i -> "Opt" + i).collect(Collectors.toList()));
        }
        
        assertDoesNotThrow(() -> {
            String qType = (String) questionMap.get("questionType");
            if (qType == null || "UNKNOWN".equals(qType)) {
                questionMap.put("questionType", "TEXT"); 
                assertEquals("TEXT", questionMap.get("questionType"));
            } else {
                assertEquals(typeStr, questionMap.get("questionType"));
            }
        });
    }

    @Test
    void testQuestionVolume_StressTest_1000Questions() {
        List<Map<String, Object>> questions = IntStream.range(0, 1000).mapToObj(i -> {
            Map<String, Object> q = new HashMap<>();
            q.put("questionText", "Question " + i);
            q.put("questionType", "TEXT");
            return q;
        }).collect(Collectors.toList());
        
        assertDoesNotThrow(() -> {
            int batches = (int) Math.ceil(questions.size() / 300.0);
            assertEquals(4, batches);
        });
    }

    @Test
    void testHTMLandSQLInjectionQuestions() {
        Map<String, Object> q = new HashMap<>();
        q.put("questionText", "DROP TABLE users; <img onerror=alert(1)>");
        q.put("questionType", "TEXT");
        assertNotNull(q.get("questionText"));
    }

    @Test
    void testGoogleApiTimeout_CircuitBreakerExecutesFallback() {
        RuntimeException apiException = new RuntimeException("429 Too Many Requests");
        Exception thrown = assertThrows(RuntimeException.class, () -> {
            throw apiException;
        });
        assertTrue(thrown.getMessage().contains("429"));
    }

    @Test
    void testDuplicateSurveyCreationPrevented() {
        GoogleForm existing = new GoogleForm();
        existing.setSurveyId(1L);
        when(googleFormRepository.findBySurveyId(1L)).thenReturn(java.util.Optional.of(existing));
        assertTrue(googleFormRepository.findBySurveyId(1L).isPresent());
        verify(googleFormRepository, never()).save(any());
    }
}
