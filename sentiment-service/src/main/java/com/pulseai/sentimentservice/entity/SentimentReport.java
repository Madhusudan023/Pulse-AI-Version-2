package com.pulseai.sentimentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sentiment_reports")
public class SentimentReport extends BaseEntity {

    private Long surveyId;
    private String region;
    private Integer month;
    private Integer year;

    private Integer overallScore;
    
    private Integer positivePercentage;
    private Integer neutralPercentage;
    private Integer negativePercentage;

    @Column(columnDefinition = "TEXT")
    private String executiveSummary;

    @Column(columnDefinition = "JSON")
    private String positiveThemes;

    @Column(columnDefinition = "JSON")
    private String negativeThemes;

    @Column(columnDefinition = "JSON")
    private String recommendations;

    @Column(columnDefinition = "TEXT")
    private String rawAiResponse;

    @Column(columnDefinition = "JSON")
    private String questionWiseAnalysis;

    private Long processingTime; // in milliseconds
    private java.time.LocalDateTime generatedAt;
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public Integer getOverallScore() { return this.overallScore; }
    public Integer getPositivePercentage() { return this.positivePercentage; }
    public Integer getNeutralPercentage() { return this.neutralPercentage; }
    public Integer getNegativePercentage() { return this.negativePercentage; }
    public String getExecutiveSummary() { return this.executiveSummary; }
    public String getPositiveThemes() { return this.positiveThemes; }
    public String getNegativeThemes() { return this.negativeThemes; }
    public String getRecommendations() { return this.recommendations; }
    public String getRawAiResponse() { return this.rawAiResponse; }
    public String getQuestionWiseAnalysis() { return this.questionWiseAnalysis; }
    public java.time.LocalDateTime getGeneratedAt() { return this.generatedAt; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public void setPositivePercentage(Integer positivePercentage) { this.positivePercentage = positivePercentage; }
    public void setNeutralPercentage(Integer neutralPercentage) { this.neutralPercentage = neutralPercentage; }
    public void setNegativePercentage(Integer negativePercentage) { this.negativePercentage = negativePercentage; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
    public void setPositiveThemes(String positiveThemes) { this.positiveThemes = positiveThemes; }
    public void setNegativeThemes(String negativeThemes) { this.negativeThemes = negativeThemes; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public void setRawAiResponse(String rawAiResponse) { this.rawAiResponse = rawAiResponse; }
    public void setQuestionWiseAnalysis(String questionWiseAnalysis) { this.questionWiseAnalysis = questionWiseAnalysis; }
    public void setGeneratedAt(java.time.LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public Long getProcessingTime() { return this.processingTime; }
    public void setProcessingTime(Long processingTime) { this.processingTime = processingTime; }
}
