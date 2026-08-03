package com.pulseai.reportingservice.repository;

import com.pulseai.reportingservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findBySurveyId(Long surveyId);
    List<Report> findByRegion(String region);
}
