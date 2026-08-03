package com.pulseai.googleformservice.kafka;

import com.pulseai.googleformservice.feign.EmployeeFeignClient;
import com.pulseai.googleformservice.service.GmailDispatchService;
import com.pulseai.googleformservice.entity.SurveyEmailTask;
import com.pulseai.googleformservice.repository.SurveyEmailTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SurveyEventListener {
    private static final Logger log = LoggerFactory.getLogger(SurveyEventListener.class);

    @Autowired
    private EmployeeFeignClient employeeFeignClient;

    @Autowired
    private GmailDispatchService gmailDispatchService;

    @Autowired
    private com.pulseai.googleformservice.service.GoogleFormsService googleFormsService;

    @Autowired
    private com.pulseai.googleformservice.feign.SurveyFeignClient surveyFeignClient;

    @Autowired
    private com.pulseai.googleformservice.feign.QuestionBankFeignClient questionBankFeignClient;

    @Autowired
    private com.pulseai.googleformservice.repository.GoogleFormRepository googleFormRepository;

    @Autowired
    private SurveyEmailTaskRepository surveyEmailTaskRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job emailDispatchJob;

    @KafkaListener(topics = "survey-published", groupId = "google-form-service-group")
    public void handleSurveyPublished(SurveyPublishedEvent event) {
        log.info("Received SurveyPublishedEvent for Survey ID: {}", event.getSurveyId());
        log.info("Target Region: {}, Audience: {}", event.getRegion(), event.getTargetAudience());
        
        String formUrl;
        try {
            log.info("Fetching questions for Survey ID: {}", event.getSurveyId());
            List<com.pulseai.googleformservice.dto.SurveyQuestionDTO> surveyQuestions = new ArrayList<>();
            try {
                surveyQuestions = surveyFeignClient.getSurveyQuestions(event.getSurveyId());
            } catch (Exception e) {
                log.warn("Failed to fetch survey questions from survey-service. Proceeding with empty questions. Error: {}", e.getMessage());
            }
            
            List<com.pulseai.googleformservice.dto.QuestionResponseDTO> questions = new ArrayList<>();
            for (com.pulseai.googleformservice.dto.SurveyQuestionDTO sq : surveyQuestions) {
                try {
                    com.pulseai.googleformservice.dto.QuestionResponseDTO q = questionBankFeignClient.getQuestionById(sq.getQuestionId());
                    if (q != null) {
                        questions.add(q);
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch question ID: {}", sq.getQuestionId(), e);
                }
            }

            log.info("Creating real Google Form via API with {} questions...", questions.size());
            com.pulseai.googleformservice.dto.GoogleFormResult result = googleFormsService.createForm(event.getTitle(), questions);
            formUrl = result.getFormUrl();

            com.pulseai.googleformservice.entity.GoogleForm gf = new com.pulseai.googleformservice.entity.GoogleForm();
            gf.setSurveyId(event.getSurveyId());
            gf.setGoogleFormId(result.getFormId());
            gf.setFormUrl(formUrl);
            gf.setStatus("ACTIVE");
            gf.setCreatedAt(java.time.LocalDateTime.now());
            googleFormRepository.save(gf);
            log.info("Saved GoogleForm mapping to database.");
        } catch (Exception e) {
            log.error("Aborting survey dispatch: Failed to create Google Form. Error: {}", e.getMessage());
            return;
        }

        log.info("Fetching employees in region: {}", event.getRegion());
        List<Object> employees = null;
        try {
            employees = employeeFeignClient.getEmployeesByRegion(event.getRegion());
            log.info("Successfully fetched {} employees from employee-service.", employees.size());
        } catch (Exception e) {
            log.warn("Failed to fetch employees from employee-service (is it running?): {}", e.getMessage());
            log.info("Falling back to sending an email to madhusudanbadgujar260@gmail.com for testing purposes.");
            employees = new ArrayList<>();
        }
        
        List<String> emails = new ArrayList<>();
        if (employees != null && !employees.isEmpty()) {
            for (Object empObj : employees) {
                try {
                    Map<String, Object> empMap = (Map<String, Object>) empObj;
                    Long empId = empMap.get("employeeId") != null ? Long.parseLong(empMap.get("employeeId").toString()) : null;
                    if (empId != null && event.getEmployeeIds() != null && event.getEmployeeIds().contains(empId)) {
                        String email = (String) empMap.get("email");
                        
                        boolean matchesExperience = true;
                        if (event.getExperienceFilter() != null && !event.getExperienceFilter().equalsIgnoreCase("ALL")) {
                            Object joiningDateObj = empMap.get("joiningDate");
                            if (joiningDateObj != null) {
                                try {
                                    java.time.LocalDate joiningDate = java.time.LocalDate.parse(joiningDateObj.toString().substring(0, 10));
                                    long months = java.time.temporal.ChronoUnit.MONTHS.between(joiningDate, java.time.LocalDate.now());
                                    if (event.getExperienceFilter().equalsIgnoreCase("LESS_THAN_6_MONTHS") && months >= 6) {
                                        matchesExperience = false;
                                    } else if (event.getExperienceFilter().equalsIgnoreCase("MORE_THAN_6_MONTHS") && months < 6) {
                                        matchesExperience = false;
                                    }
                                } catch (Exception ex) {
                                    log.warn("Could not parse joiningDate for employee {}: {}", empId, ex.getMessage());
                                }
                            }
                        }

                        if (email != null && matchesExperience) {
                            emails.add(email);
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to parse employee object: {}", empObj);
                }
            }
        } else {
            // Fallback for testing
            emails.add("madhusudanbadgujar260@gmail.com");
        }
        
        if (event.getCustomEmails() != null && !event.getCustomEmails().isEmpty()) {
            log.info("Adding {} custom emails from XML upload", event.getCustomEmails().size());
            emails.addAll(event.getCustomEmails());
        }
        
        log.info("Saving {} email tasks to database for Spring Batch processing...", emails.size());
        List<SurveyEmailTask> tasks = new ArrayList<>();
        for (String email : emails) {
            SurveyEmailTask task = new SurveyEmailTask();
            task.setSurveyId(event.getSurveyId());
            task.setEmail(email);
            task.setFormUrl(formUrl);
            task.setSurveyTitle(event.getTitle());
            task.setStatus("PENDING");
            task.setCreatedAt(LocalDateTime.now());
            tasks.add(task);
        }
        surveyEmailTaskRepository.saveAll(tasks);
        
        log.info("Triggering Spring Batch Job for Survey ID: {}", event.getSurveyId());
        try {
            jobLauncher.run(emailDispatchJob, new JobParametersBuilder()
                    .addLong("surveyId", event.getSurveyId())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
        } catch (Exception e) {
            log.error("Failed to trigger Spring Batch Job", e);
        }
    }

    @KafkaListener(topics = {"survey-closed", "survey-closed-events"}, groupId = "google-form-service-group")
    public void handleSurveyClosed(com.pulseai.googleformservice.dto.event.SurveyClosedEvent event) {
        log.info("Received SurveyClosedEvent for Survey ID: {}", event.getSurveyId());
        try {
            googleFormsService.closeForm(event.getSurveyId());
        } catch (Exception e) {
            log.error("Failed to process SurveyClosedEvent for Survey ID: {}", event.getSurveyId(), e);
        }
    }

    @KafkaListener(topics = "survey-reactivated", groupId = "google-form-service-group")
    public void handleSurveyReactivated(SurveyPublishedEvent event) {
        log.info("Received survey-reactivated event for Survey ID: {}", event.getSurveyId());
        try {
            googleFormsService.reactivateForm(event.getSurveyId());
        } catch (Exception e) {
            log.error("Failed to reactivate Google Form for Survey ID: {}", event.getSurveyId(), e);
        }
    }
}
