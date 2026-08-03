package com.pulseai.googleformservice.repository;

import com.pulseai.googleformservice.entity.SurveyEmailTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface SurveyEmailTaskRepository extends JpaRepository<SurveyEmailTask, Long> {
    Page<SurveyEmailTask> findBySurveyIdAndStatus(Long surveyId, String status, Pageable pageable);
}
