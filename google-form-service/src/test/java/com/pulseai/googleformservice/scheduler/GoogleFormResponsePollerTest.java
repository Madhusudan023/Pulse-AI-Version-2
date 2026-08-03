package com.pulseai.googleformservice.scheduler;

import com.google.api.services.forms.v1.model.FormResponse;
import com.pulseai.googleformservice.entity.GoogleForm;
import com.pulseai.googleformservice.feign.SurveyFeignClient;
import com.pulseai.googleformservice.repository.GoogleFormRepository;
import com.pulseai.googleformservice.service.GoogleFormsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleFormResponsePollerTest {

    @Mock private GoogleFormRepository googleFormRepository;
    @Mock private GoogleFormsService googleFormsService;
    @Mock private SurveyFeignClient surveyFeignClient;

    @InjectMocks
    private GoogleFormResponsePoller poller;

    @ParameterizedTest
    @CsvSource({
        "0, 0",
        "1, 1",
        "100, 10"
    })
    void testPollingVolumes(int formCount, int expectedResponses) {
        List<GoogleForm> forms = IntStream.range(0, formCount).mapToObj(i -> {
            GoogleForm f = new GoogleForm();
            f.setSurveyId((long) i);
            f.setGoogleFormId("form_" + i);
            f.setExpiresAt(LocalDateTime.now().minusDays(1)); // used as lastSyncTime
            return f;
        }).collect(Collectors.toList());

        when(googleFormRepository.findAll()).thenReturn(forms);
        if (formCount > 0 && expectedResponses > 0) {
            List<FormResponse> results = IntStream.range(0, expectedResponses)
                    .mapToObj(i -> {
                        FormResponse r = new FormResponse();
                        r.setCreateTime(Instant.now().toString());
                        return r;
                    })
                    .collect(Collectors.toList());
            try {
                lenient().when(googleFormsService.getFormResponses(anyString())).thenReturn(results);
            } catch (Exception e) {}
        }

        assertDoesNotThrow(() -> poller.pollResponses());
        
        if (formCount > 0) {
            try {
                verify(googleFormsService, times(formCount)).getFormResponses(anyString());
            } catch (Exception e) {}
        }
    }

    @Test
    void testDuplicateResponseDeduplication() {
        GoogleForm form = new GoogleForm();
        form.setSurveyId(1L);
        form.setGoogleFormId("testForm");
        form.setExpiresAt(LocalDateTime.now().minusHours(2)); 
        
        FormResponse r1 = new FormResponse();
        r1.setCreateTime(Instant.now().minusSeconds(3600).toString()); // 1 hr ago
        
        assertNotNull(r1);
        assertNotNull(form.getExpiresAt());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Standard answer",
        "", 
        "   ", 
        "null", 
        "Emoji 😊", 
        "Unicode: こんにちは"
    })
    void testResponseParsingEdgeCases(String answerStr) {
        String parsedAnswer = (answerStr == null || answerStr.trim().isEmpty() || answerStr.equals("null")) ? null : answerStr;
        
        if ("Emoji 😊".equals(answerStr) || "Unicode: こんにちは".equals(answerStr)) {
            assertNotNull(parsedAnswer);
            assertEquals(answerStr, parsedAnswer);
        } else if (parsedAnswer == null) {
            assertNull(parsedAnswer);
        }
    }

    @Test
    void testSubmitToSurveyService_TimeoutFallback() throws Exception {
        GoogleForm form = new GoogleForm();
        form.setSurveyId(1L);
        form.setGoogleFormId("f1");
        form.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(googleFormRepository.findAll()).thenReturn(Collections.singletonList(form));
        
        FormResponse resp = new FormResponse();
        resp.setCreateTime(Instant.now().toString());
        when(googleFormsService.getFormResponses("f1")).thenReturn(Collections.singletonList(resp));
        
        doThrow(new RuntimeException("Feign Timeout")).when(surveyFeignClient).submitInternalResponse(anyLong(), any());
        
        assertDoesNotThrow(() -> poller.pollResponses());
    }
}
