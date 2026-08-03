package com.pulseai.questionbank.service;

import com.pulseai.questionbank.dto.request.CreateQuestionRequest;
import com.pulseai.questionbank.dto.request.UpdateQuestionRequest;
import com.pulseai.questionbank.dto.response.QuestionResponseDTO;
import com.pulseai.questionbank.entity.Question;
import com.pulseai.questionbank.enums.QuestionSource;
import com.pulseai.questionbank.enums.QuestionStatus;
import com.pulseai.questionbank.enums.SurveyType;
import com.pulseai.questionbank.exception.BusinessException;
import com.pulseai.questionbank.exception.ResourceNotFoundException;
import com.pulseai.questionbank.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionResponseDTO createQuestion(CreateQuestionRequest request) {
        Question q = new Question();
        q.setQuestionText(request.getQuestionText());
        q.setQuestionType(request.getQuestionType());
        q.setCategory(request.getCategory());
        q.setRegion(request.getRegion());
        q.setMonth(request.getMonth());
        q.setYear(request.getYear());
        q.setSurveyType(request.getSurveyType());
        q.setRemarks(request.getRemarks());
        if (request.getOptions() != null) {
            q.setOptions(new java.util.ArrayList<>(request.getOptions()));
        }
        q.setSource(QuestionSource.HR);
        q.setStatus(QuestionStatus.APPROVED); // Direct HR creation is auto-approved

        return mapToDTO(questionRepository.save(q));
    }

    public QuestionResponseDTO createAiDraftQuestion(CreateQuestionRequest request) {
        Question q = new Question();
        q.setQuestionText(request.getQuestionText());
        q.setQuestionType(request.getQuestionType());
        q.setCategory(request.getCategory());
        q.setRegion(request.getRegion());
        q.setMonth(request.getMonth());
        q.setYear(request.getYear());
        q.setSurveyType(request.getSurveyType());
        q.setRemarks(request.getRemarks());
        if (request.getOptions() != null) {
            q.setOptions(new java.util.ArrayList<>(request.getOptions()));
        }
        q.setSource(QuestionSource.AI);
        q.setStatus(QuestionStatus.DRAFT); 

        return mapToDTO(questionRepository.save(q));
    }

    public QuestionResponseDTO updateQuestion(Long id, UpdateQuestionRequest request) {
        Question q = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (q.getUsageCount() > 0) {
            log.info("Question {} is already used in a survey. Cloning to a new version.", id);
            Question clone = new Question();
            clone.setQuestionText(request.getQuestionText());
            clone.setQuestionType(request.getQuestionType());
            clone.setCategory(request.getCategory());
            clone.setRegion(q.getRegion());
            clone.setMonth(q.getMonth());
            clone.setYear(q.getYear());
            clone.setSurveyType(q.getSurveyType());
            clone.setSource(q.getSource());
            clone.setStatus(q.getStatus());
            clone.setVersion(q.getVersion() + 1);
            clone.setRemarks(request.getRemarks());
            if (request.getOptions() != null) {
                clone.setOptions(new java.util.ArrayList<>(request.getOptions()));
            }
            // Scale fields (with defaults if not supplied)
            clone.setPositiveFrom(request.getPositiveFrom() != null ? request.getPositiveFrom() : 8);
            clone.setPositiveTo(request.getPositiveTo()     != null ? request.getPositiveTo()   : 10);
            clone.setNeutralFrom(request.getNeutralFrom()   != null ? request.getNeutralFrom()  : 5);
            clone.setNeutralTo(request.getNeutralTo()       != null ? request.getNeutralTo()    : 7);
            clone.setNegativeFrom(request.getNegativeFrom() != null ? request.getNegativeFrom() : 1);
            clone.setNegativeTo(request.getNegativeTo()     != null ? request.getNegativeTo()   : 4);
            
            // Soft delete old
            q.setActive(false);
            questionRepository.save(q);
            
            return mapToDTO(questionRepository.save(clone));
        } else {
            q.setQuestionText(request.getQuestionText());
            q.setQuestionType(request.getQuestionType());
            q.setCategory(request.getCategory());
            q.setSurveyType(request.getSurveyType());
            q.setRemarks(request.getRemarks());
            if (request.getOptions() != null) {
                q.getOptions().clear();
                q.getOptions().addAll(request.getOptions());
            }
            // Scale fields (with defaults if not supplied)
            q.setPositiveFrom(request.getPositiveFrom() != null ? request.getPositiveFrom() : q.getPositiveFrom() != null ? q.getPositiveFrom() : 8);
            q.setPositiveTo(request.getPositiveTo()     != null ? request.getPositiveTo()   : q.getPositiveTo()   != null ? q.getPositiveTo()   : 10);
            q.setNeutralFrom(request.getNeutralFrom()   != null ? request.getNeutralFrom()  : q.getNeutralFrom()  != null ? q.getNeutralFrom()  : 5);
            q.setNeutralTo(request.getNeutralTo()       != null ? request.getNeutralTo()    : q.getNeutralTo()    != null ? q.getNeutralTo()    : 7);
            q.setNegativeFrom(request.getNegativeFrom() != null ? request.getNegativeFrom() : q.getNegativeFrom() != null ? q.getNegativeFrom() : 1);
            q.setNegativeTo(request.getNegativeTo()     != null ? request.getNegativeTo()   : q.getNegativeTo()   != null ? q.getNegativeTo()   : 4);
            return mapToDTO(questionRepository.save(q));
        }
    }

    public void deleteQuestion(Long id) {
        Question q = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (q.getUsageCount() > 0) {
            throw new BusinessException("Cannot delete question that has already been used in surveys. You can deactivate it.");
        }
        questionRepository.delete(q);
    }

    public List<QuestionResponseDTO> getAllQuestions(QuestionStatus status, String region) {
        if ("GLOBAL".equalsIgnoreCase(region)) {
            if (status != null) {
                return questionRepository.findByStatus(status).stream().map(this::mapToDTO).collect(Collectors.toList());
            }
            return questionRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
        } else {
            java.util.List<String> regions = java.util.Arrays.asList("GLOBAL", region);
            if (status != null) {
                return questionRepository.findByRegionInAndStatus(regions, status).stream().map(this::mapToDTO).collect(Collectors.toList());
            }
            return questionRepository.findByRegionIn(regions).stream().map(this::mapToDTO).collect(Collectors.toList());
        }
    }

    public void approveQuestion(Long id) {
        Question q = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (q.getStatus() != QuestionStatus.DRAFT) {
            throw new BusinessException("Only draft questions can be approved");
        }
        q.setStatus(QuestionStatus.APPROVED);
        questionRepository.save(q);
    }

    public void rejectQuestion(Long id) {
        Question q = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (q.getStatus() != QuestionStatus.DRAFT) {
            throw new BusinessException("Only draft questions can be rejected");
        }
        q.setStatus(QuestionStatus.REJECTED);
        questionRepository.save(q);
    }

    public List<QuestionResponseDTO> getApprovedQuestionsByRegionAndType(String region, String surveyType) {
        java.util.List<String> regions = "GLOBAL".equalsIgnoreCase(region) ? 
                java.util.Collections.singletonList("GLOBAL") : 
                java.util.Arrays.asList("GLOBAL", region);
                
        return questionRepository.findByRegionInAndSurveyTypeAndStatus(regions, SurveyType.valueOf(surveyType), QuestionStatus.APPROVED)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    
    public QuestionResponseDTO getQuestionById(Long id) {
        return mapToDTO(questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found")));
    }

    private QuestionResponseDTO mapToDTO(Question q) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(q.getId());
        dto.setQuestionText(q.getQuestionText());
        dto.setQuestionType(q.getQuestionType());
        dto.setCategory(q.getCategory());
        dto.setSource(q.getSource());
        dto.setStatus(q.getStatus());
        dto.setRegion(q.getRegion());
        dto.setMonth(q.getMonth());
        dto.setYear(q.getYear());
        dto.setSurveyType(q.getSurveyType());
        dto.setVersion(q.getVersion());
        dto.setRemarks(q.getRemarks());
        dto.setUsageCount(q.getUsageCount());
        dto.setCreatedAt(q.getCreatedAt());
        dto.setUpdatedAt(q.getUpdatedAt());
        dto.setCreatedBy(q.getCreatedBy());
        if (q.getOptions() != null) {
            dto.setOptions(new java.util.ArrayList<>(q.getOptions()));
        }
        // Scale fields (with defaults if null)
        dto.setPositiveFrom(q.getPositiveFrom() != null ? q.getPositiveFrom() : 8);
        dto.setPositiveTo(q.getPositiveTo()     != null ? q.getPositiveTo()   : 10);
        dto.setNeutralFrom(q.getNeutralFrom()   != null ? q.getNeutralFrom()  : 5);
        dto.setNeutralTo(q.getNeutralTo()       != null ? q.getNeutralTo()    : 7);
        dto.setNegativeFrom(q.getNegativeFrom() != null ? q.getNegativeFrom() : 1);
        dto.setNegativeTo(q.getNegativeTo()     != null ? q.getNegativeTo()   : 4);
        return dto;
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(QuestionService.class);
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
}
