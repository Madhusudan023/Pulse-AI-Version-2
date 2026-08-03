package com.pulseai.surveyservice.repository;

import com.pulseai.surveyservice.entity.SurveyAssignment;
import com.pulseai.surveyservice.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyAssignmentRepository extends JpaRepository<SurveyAssignment, Long> {
    List<SurveyAssignment> findByEmployeeIdAndStatus(Long employeeId, AssignmentStatus status);
    Optional<SurveyAssignment> findBySurveyIdAndEmployeeId(Long surveyId, Long employeeId);
    List<SurveyAssignment> findBySurveyId(Long surveyId);
}
