package com.pulseai.questionbank.service;

import com.pulseai.questionbank.dto.request.CreateQuestionRequest;
import com.pulseai.questionbank.dto.request.UpdateQuestionRequest;
import com.pulseai.questionbank.dto.response.QuestionResponseDTO;
import com.pulseai.questionbank.entity.Question;
import com.pulseai.questionbank.enums.*;
import com.pulseai.questionbank.exception.BusinessException;
import com.pulseai.questionbank.exception.ResourceNotFoundException;
import com.pulseai.questionbank.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private CreateQuestionRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateQuestionRequest();
        createRequest.setQuestionText("How satisfied are you with work?");
        createRequest.setQuestionType(QuestionType.LIKERT_SCALE);
        createRequest.setCategory(QuestionCategory.CULTURE);
        createRequest.setRegion("GLOBAL");
        createRequest.setMonth(8);
        createRequest.setYear(2026);
        createRequest.setSurveyType(SurveyType.MONTHLY_PULSE);
        createRequest.setRemarks("Standard Q");
        createRequest.setOptions(Arrays.asList("1", "2", "3", "4", "5"));
    }

    // --- 1. createQuestion and createAiDraftQuestion Tests ---

    @Test
    void testCreateQuestion_HRDirectAutoApproved() {
        when(questionRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(1L);
            return q;
        });

        QuestionResponseDTO response = questionService.createQuestion(createRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(QuestionSource.HR, response.getSource());
        assertEquals(QuestionStatus.APPROVED, response.getStatus());
        assertEquals("How satisfied are you with work?", response.getQuestionText());
        assertEquals(5, response.getOptions().size());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testCreateAiDraftQuestion_DraftAI() {
        when(questionRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(2L);
            return q;
        });

        QuestionResponseDTO response = questionService.createAiDraftQuestion(createRequest);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(QuestionSource.AI, response.getSource());
        assertEquals(QuestionStatus.DRAFT, response.getStatus());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    // --- 2. updateQuestion Tests (Cloning vs In-Place) ---

    @Test
    void testUpdateQuestion_InPlace_WhenUsageCountZero() {
        Question existing = new Question();
        existing.setId(10L);
        existing.setQuestionText("Old text");
        existing.setUsageCount(0);
        existing.setVersion(1);
        existing.setOptions(new ArrayList<>());

        UpdateQuestionRequest updateReq = new UpdateQuestionRequest();
        updateReq.setQuestionText("New text");
        updateReq.setQuestionType(QuestionType.TEXT);
        updateReq.setCategory(QuestionCategory.CULTURE);
        updateReq.setSurveyType(SurveyType.MONTHLY_PULSE);
        updateReq.setRemarks("updated remarks");

        when(questionRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(questionRepository.save(existing)).thenReturn(existing);

        QuestionResponseDTO response = questionService.updateQuestion(10L, updateReq);

        assertEquals("New text", response.getQuestionText());
        assertEquals(QuestionType.TEXT, response.getQuestionType());
        assertEquals("updated remarks", response.getRemarks());
        assertEquals(1, response.getVersion()); // version unchanged
        verify(questionRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateQuestion_ClonedVersion_WhenUsageCountGreaterThanZero() {
        Question existing = new Question();
        existing.setId(10L);
        existing.setQuestionText("Old text");
        existing.setUsageCount(2);
        existing.setVersion(1);
        existing.setRegion("GLOBAL");
        existing.setSource(QuestionSource.HR);
        existing.setStatus(QuestionStatus.APPROVED);

        UpdateQuestionRequest updateReq = new UpdateQuestionRequest();
        updateReq.setQuestionText("Cloned text");
        updateReq.setQuestionType(QuestionType.LIKERT_SCALE);
        updateReq.setCategory(QuestionCategory.CULTURE);
        updateReq.setRemarks("Cloned remarks");
        updateReq.setPositiveFrom(9); // custom values

        when(questionRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(questionRepository.save(any(Question.class))).thenAnswer(i -> i.getArgument(0));

        QuestionResponseDTO response = questionService.updateQuestion(10L, updateReq);

        // verify original soft-deleted (active = false)
        assertFalse(existing.isActive());
        verify(questionRepository).save(existing);

        // verify new cloned question properties
        assertEquals("Cloned text", response.getQuestionText());
        assertEquals(2, response.getVersion()); // version incremented
        assertEquals(9, response.getPositiveFrom());
        assertEquals(10, response.getPositiveTo()); // default fallback
        assertEquals(5, response.getNeutralFrom());  // default fallback
    }

    @Test
    void testUpdateQuestion_NotFound() {
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> 
            questionService.updateQuestion(99L, new UpdateQuestionRequest())
        );
    }

    // --- 3. deleteQuestion Tests ---

    @Test
    void testDeleteQuestion_Success_WhenUsageCountZero() {
        Question q = new Question();
        q.setId(5L);
        q.setUsageCount(0);

        when(questionRepository.findById(5L)).thenReturn(Optional.of(q));

        assertDoesNotThrow(() -> questionService.deleteQuestion(5L));
        verify(questionRepository, times(1)).delete(q);
    }

    @Test
    void testDeleteQuestion_Failure_WhenUsageCountGreaterThanZero() {
        Question q = new Question();
        q.setId(5L);
        q.setUsageCount(1);

        when(questionRepository.findById(5L)).thenReturn(Optional.of(q));

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            questionService.deleteQuestion(5L)
        );
        assertTrue(ex.getMessage().contains("Cannot delete question"));
        verify(questionRepository, never()).delete(any());
    }

    @Test
    void testDeleteQuestion_NotFound() {
        when(questionRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> questionService.deleteQuestion(5L));
    }

    // --- 4. approveQuestion & rejectQuestion Tests ---

    @Test
    void testApproveQuestion_Success() {
        Question q = new Question();
        q.setId(10L);
        q.setStatus(QuestionStatus.DRAFT);

        when(questionRepository.findById(10L)).thenReturn(Optional.of(q));
        when(questionRepository.save(q)).thenReturn(q);

        assertDoesNotThrow(() -> questionService.approveQuestion(10L));
        assertEquals(QuestionStatus.APPROVED, q.getStatus());
        verify(questionRepository, times(1)).save(q);
    }

    @ParameterizedTest
    @EnumSource(value = QuestionStatus.class, names = {"APPROVED", "REJECTED"})
    void testApproveQuestion_Failure_WhenNotDraft(QuestionStatus status) {
        Question q = new Question();
        q.setId(10L);
        q.setStatus(status);

        when(questionRepository.findById(10L)).thenReturn(Optional.of(q));

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            questionService.approveQuestion(10L)
        );
        assertTrue(ex.getMessage().contains("Only draft questions"));
        verify(questionRepository, never()).save(any());
    }

    @Test
    void testRejectQuestion_Success() {
        Question q = new Question();
        q.setId(10L);
        q.setStatus(QuestionStatus.DRAFT);

        when(questionRepository.findById(10L)).thenReturn(Optional.of(q));
        when(questionRepository.save(q)).thenReturn(q);

        assertDoesNotThrow(() -> questionService.rejectQuestion(10L));
        assertEquals(QuestionStatus.REJECTED, q.getStatus());
        verify(questionRepository, times(1)).save(q);
    }

    // --- 5. Query and Fetching Tests ---

    @Test
    void testGetAllQuestions_Global_NullStatus() {
        Question q = new Question();
        when(questionRepository.findAll()).thenReturn(Collections.singletonList(q));

        List<QuestionResponseDTO> res = questionService.getAllQuestions(null, "GLOBAL");
        assertEquals(1, res.size());
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    void testGetAllQuestions_Global_WithStatus() {
        Question q = new Question();
        when(questionRepository.findByStatus(QuestionStatus.APPROVED)).thenReturn(Collections.singletonList(q));

        List<QuestionResponseDTO> res = questionService.getAllQuestions(QuestionStatus.APPROVED, "GLOBAL");
        assertEquals(1, res.size());
        verify(questionRepository, times(1)).findByStatus(QuestionStatus.APPROVED);
    }

    @Test
    void testGetAllQuestions_Regional_NullStatus() {
        Question q = new Question();
        List<String> expectedRegions = Arrays.asList("GLOBAL", "HYDERABAD");
        when(questionRepository.findByRegionIn(expectedRegions)).thenReturn(Collections.singletonList(q));

        List<QuestionResponseDTO> res = questionService.getAllQuestions(null, "HYDERABAD");
        assertEquals(1, res.size());
        verify(questionRepository, times(1)).findByRegionIn(expectedRegions);
    }

    @Test
    void testGetAllQuestions_Regional_WithStatus() {
        Question q = new Question();
        List<String> expectedRegions = Arrays.asList("GLOBAL", "HYDERABAD");
        when(questionRepository.findByRegionInAndStatus(expectedRegions, QuestionStatus.APPROVED)).thenReturn(Collections.singletonList(q));

        List<QuestionResponseDTO> res = questionService.getAllQuestions(QuestionStatus.APPROVED, "HYDERABAD");
        assertEquals(1, res.size());
        verify(questionRepository, times(1)).findByRegionInAndStatus(expectedRegions, QuestionStatus.APPROVED);
    }

    @Test
    void testGetApprovedQuestionsByRegionAndType_Regional() {
        Question q = new Question();
        List<String> expectedRegions = Arrays.asList("GLOBAL", "BENGALURU");
        when(questionRepository.findByRegionInAndSurveyTypeAndStatus(
                expectedRegions, SurveyType.MONTHLY_PULSE, QuestionStatus.APPROVED))
                .thenReturn(Collections.singletonList(q));

        List<QuestionResponseDTO> res = questionService.getApprovedQuestionsByRegionAndType("BENGALURU", "MONTHLY_PULSE");
        assertEquals(1, res.size());
    }

    @Test
    void testGetQuestionById_Success() {
        Question q = new Question();
        q.setId(1L);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(q));

        QuestionResponseDTO dto = questionService.getQuestionById(1L);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }
}
