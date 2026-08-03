package com.pulseai.surveyservice.repository;

import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.enums.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByStatus(SurveyStatus status);
    List<Survey> findByRegion(String region);
    List<Survey> findByRegionAndStatus(String region, SurveyStatus status);
    java.util.Optional<Survey> findFirstBySurveyTypeOrderByCreatedAtDesc(com.pulseai.surveyservice.enums.SurveyType surveyType);
}
