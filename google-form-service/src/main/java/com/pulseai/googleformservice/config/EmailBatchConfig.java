package com.pulseai.googleformservice.config;

import com.pulseai.googleformservice.entity.SurveyEmailTask;
import com.pulseai.googleformservice.repository.SurveyEmailTaskRepository;
import com.pulseai.googleformservice.service.GmailDispatchService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class EmailBatchConfig {

    @Autowired
    private SurveyEmailTaskRepository taskRepository;

    @Autowired
    private GmailDispatchService gmailDispatchService;

    @Bean
    public Job emailDispatchJob(JobRepository jobRepository, Step emailDispatchStep) {
        return new JobBuilder("emailDispatchJob", jobRepository)
                .start(emailDispatchStep)
                .build();
    }

    @Bean
    public Step emailDispatchStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager) {
        return new StepBuilder("emailDispatchStep", jobRepository)
                .<SurveyEmailTask, SurveyEmailTask>chunk(10, transactionManager)
                .reader(emailTaskReader(null))
                .processor(emailTaskProcessor())
                .writer(emailTaskWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<SurveyEmailTask> emailTaskReader(@Value("#{jobParameters['surveyId']}") Long surveyId) {
        return new RepositoryItemReaderBuilder<SurveyEmailTask>()
                .name("emailTaskReader")
                .repository(taskRepository)
                .methodName("findBySurveyIdAndStatus")
                .arguments(surveyId, "PENDING")
                .pageSize(10)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<SurveyEmailTask, SurveyEmailTask> emailTaskProcessor() {
        return task -> {
            try {
                gmailDispatchService.dispatchEmails(List.of(task.getEmail()), task.getFormUrl(), task.getSurveyTitle());
                task.setStatus("SENT");
            } catch (Exception e) {
                task.setStatus("FAILED");
            }
            task.setProcessedAt(LocalDateTime.now());
            return task;
        };
    }

    @Bean
    public ItemWriter<SurveyEmailTask> emailTaskWriter() {
        return tasks -> taskRepository.saveAll(tasks);
    }
}
