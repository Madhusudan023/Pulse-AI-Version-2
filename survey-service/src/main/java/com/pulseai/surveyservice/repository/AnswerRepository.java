package com.pulseai.surveyservice.repository;

import com.pulseai.surveyservice.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByResponseIdIn(List<Long> responseIds);
    List<Answer> findByResponseId(Long responseId);
}
