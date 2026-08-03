package com.pulseai.sentimentservice.repository;

import com.pulseai.sentimentservice.entity.AiProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiProcessingLogRepository extends JpaRepository<AiProcessingLog, Long> {
}
