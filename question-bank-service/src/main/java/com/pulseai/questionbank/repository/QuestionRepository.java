package com.pulseai.questionbank.repository;

import com.pulseai.questionbank.entity.Question;
import com.pulseai.questionbank.enums.QuestionStatus;
import com.pulseai.questionbank.enums.SurveyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByStatus(QuestionStatus status);
    List<Question> findByRegion(String region);
    List<Question> findByRegionAndStatus(String region, QuestionStatus status);
    
    // Support querying for GLOBAL + specific region
    List<Question> findByRegionIn(List<String> regions);
    List<Question> findByRegionInAndStatus(List<String> regions, QuestionStatus status);
    List<Question> findByRegionInAndSurveyTypeAndStatus(List<String> regions, SurveyType surveyType, QuestionStatus status);
}
