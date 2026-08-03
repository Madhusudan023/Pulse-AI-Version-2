package com.pulseai.googleformservice.repository;

import com.pulseai.googleformservice.entity.GoogleForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleFormRepository extends JpaRepository<GoogleForm, Long> {
    Optional<GoogleForm> findBySurveyId(Long surveyId);
    Optional<GoogleForm> findByGoogleFormId(String googleFormId);
}
