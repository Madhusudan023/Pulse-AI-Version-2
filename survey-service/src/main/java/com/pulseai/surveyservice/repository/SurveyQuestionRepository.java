package com.pulseai.surveyservice.repository;

import com.pulseai.surveyservice.entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    List<SurveyQuestion> findBySurveyIdOrderByDisplayOrderAsc(Long surveyId);
    void deleteBySurveyIdAndQuestionIdIn(Long surveyId, java.util.List<Long> questionIds);
}

