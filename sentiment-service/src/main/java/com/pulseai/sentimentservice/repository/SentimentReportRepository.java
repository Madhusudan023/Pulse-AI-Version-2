package com.pulseai.sentimentservice.repository;

import com.pulseai.sentimentservice.entity.SentimentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SentimentReportRepository extends JpaRepository<SentimentReport, Long> {
    Optional<SentimentReport> findFirstBySurveyIdOrderByGeneratedAtDesc(Long surveyId);
}
