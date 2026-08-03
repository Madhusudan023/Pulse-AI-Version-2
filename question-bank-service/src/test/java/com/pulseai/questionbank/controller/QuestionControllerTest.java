package com.pulseai.questionbank.controller;

import com.pulseai.questionbank.dto.ApiResponse;
import com.pulseai.questionbank.dto.request.CreateQuestionRequest;
import com.pulseai.questionbank.dto.request.UpdateQuestionRequest;
import com.pulseai.questionbank.dto.response.QuestionResponseDTO;
import com.pulseai.questionbank.enums.QuestionStatus;
import com.pulseai.questionbank.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {

    @Mock private QuestionService questionService;
    @Mock private HttpServletRequest request;

    private QuestionController questionController;
    private InternalQuestionController internalQuestionController;

    @BeforeEach
    void setUp() {
        questionController = new QuestionController(questionService);
        internalQuestionController = new InternalQuestionController(questionService);
    }

    // --- A. QuestionController Tests (12 scenarios) ---

    @Test
    void testCreateQuestion_WithSpecifiedRegion() {
        CreateQuestionRequest createReq = new CreateQuestionRequest();
        createReq.setQuestionText("Q1");
        createReq.setRegion("HYDERABAD");

        QuestionResponseDTO mockRes = new QuestionResponseDTO();
        mockRes.setId(1L);
        mockRes.setRegion("HYDERABAD");

        when(request.getAttribute("region")).thenReturn("GLOBAL");
        when(questionService.createQuestion(createReq)).thenReturn(mockRes);

        ResponseEntity<ApiResponse<QuestionResponseDTO>> entity = questionController.createQuestion(createReq, request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertTrue(entity.getBody().isSuccess());
        assertEquals("HYDERABAD", entity.getBody().getData().getRegion());
        verify(questionService, times(1)).createQuestion(createReq);
    }

    @Test
    void testCreateQuestion_FallBackToRequestRegion() {
        CreateQuestionRequest createReq = new CreateQuestionRequest();
        createReq.setQuestionText("Q1");
        createReq.setRegion(null); // Will fallback to request region

        QuestionResponseDTO mockRes = new QuestionResponseDTO();
        mockRes.setId(1L);
        mockRes.setRegion("PUNE");

        when(request.getAttribute("region")).thenReturn("PUNE");
        when(questionService.createQuestion(createReq)).thenReturn(mockRes);

        ResponseEntity<ApiResponse<QuestionResponseDTO>> entity = questionController.createQuestion(createReq, request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals("PUNE", createReq.getRegion()); // fallback checked
    }

    @Test
    void testUpdateQuestion() {
        UpdateQuestionRequest updateReq = new UpdateQuestionRequest();
        updateReq.setQuestionText("Q2");

        QuestionResponseDTO mockRes = new QuestionResponseDTO();
        mockRes.setId(10L);

        when(questionService.updateQuestion(eq(10L), any())).thenReturn(mockRes);

        ResponseEntity<ApiResponse<QuestionResponseDTO>> entity = questionController.updateQuestion(10L, updateReq);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(10L, entity.getBody().getData().getId());
    }

    @Test
    void testDeleteQuestion() {
        doNothing().when(questionService).deleteQuestion(5L);

        ResponseEntity<ApiResponse<Void>> entity = questionController.deleteQuestion(5L);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals("Deleted successfully", entity.getBody().getMessage());
        verify(questionService, times(1)).deleteQuestion(5L);
    }

    @Test
    void testGetAllQuestions_WithStatusFilter() {
        when(request.getAttribute("region")).thenReturn("CHENNAI");
        when(questionService.getAllQuestions(QuestionStatus.APPROVED, "CHENNAI")).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> entity = questionController.getAllQuestions(QuestionStatus.APPROVED, request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().getData().isEmpty());
    }

    @Test
    void testGetAllQuestions_NullStatusFilter() {
        when(request.getAttribute("region")).thenReturn("CHENNAI");
        when(questionService.getAllQuestions(null, "CHENNAI")).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> entity = questionController.getAllQuestions(null, request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void testApproveQuestion() {
        doNothing().when(questionService).approveQuestion(7L);

        ResponseEntity<ApiResponse<Void>> entity = questionController.approveQuestion(7L);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals("Question approved", entity.getBody().getMessage());
    }

    @Test
    void testRejectQuestion() {
        doNothing().when(questionService).rejectQuestion(8L);

        ResponseEntity<ApiResponse<Void>> entity = questionController.rejectQuestion(8L);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals("Question rejected", entity.getBody().getMessage());
    }

    @Test
    void testGetOnboardingQuestions() {
        when(request.getAttribute("region")).thenReturn("GLOBAL");
        when(questionService.getApprovedQuestionsByRegionAndType("GLOBAL", "ONBOARDING")).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> entity = questionController.getOnboardingQuestions(request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody().getData());
    }

    @Test
    void testGetMonthlyPulseQuestions() {
        when(request.getAttribute("region")).thenReturn("BENGALURU");
        when(questionService.getApprovedQuestionsByRegionAndType("BENGALURU", "MONTHLY_PULSE")).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> entity = questionController.getMonthlyPulseQuestions(request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody().getData());
    }

    // --- B. InternalQuestionController Tests (10 scenarios) ---

    @Test
    void testGetApprovedQuestions_Internal() {
        when(questionService.getApprovedQuestionsByRegionAndType("HYDERABAD", "MONTHLY"))
                .thenReturn(Collections.singletonList(new QuestionResponseDTO()));

        ResponseEntity<List<QuestionResponseDTO>> entity = internalQuestionController.getApprovedQuestions("HYDERABAD", "MONTHLY");

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(1, entity.getBody().size());
    }

    @Test
    void testGetQuestionById_Internal() {
        QuestionResponseDTO mockRes = new QuestionResponseDTO();
        mockRes.setId(100L);
        when(questionService.getQuestionById(100L)).thenReturn(mockRes);

        ResponseEntity<QuestionResponseDTO> entity = internalQuestionController.getQuestionById(100L);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(100L, entity.getBody().getId());
    }

    @Test
    void testAddAiDraftQuestions_Internal() {
        CreateQuestionRequest q1 = new CreateQuestionRequest();
        q1.setQuestionText("AI Q1");
        CreateQuestionRequest q2 = new CreateQuestionRequest();
        q2.setQuestionText("AI Q2");

        ResponseEntity<Void> entity = internalQuestionController.addAiDraftQuestions(Arrays.asList(q1, q2));

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        verify(questionService, times(1)).createAiDraftQuestion(q1);
        verify(questionService, times(1)).createAiDraftQuestion(q2);
    }

    @Test
    void testGetQuestionsByIds_Internal_Bulk() {
        QuestionResponseDTO r1 = new QuestionResponseDTO(); r1.setId(1L);
        QuestionResponseDTO r2 = new QuestionResponseDTO(); r2.setId(2L);

        when(questionService.getQuestionById(1L)).thenReturn(r1);
        when(questionService.getQuestionById(2L)).thenReturn(r2);

        ResponseEntity<List<QuestionResponseDTO>> entity = internalQuestionController.getQuestionsByIds(Arrays.asList(1L, 2L));

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(2, entity.getBody().size());
        assertEquals(1L, entity.getBody().get(0).getId());
        assertEquals(2L, entity.getBody().get(1).getId());
    }
}
