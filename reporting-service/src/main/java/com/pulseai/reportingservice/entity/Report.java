package com.pulseai.reportingservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report")
public class Report extends BaseEntity {

    private Long surveyId;
    private String region;
    private Integer month;
    private Integer year;

    private Integer overallScore;
    
    private Integer positivePercentage;
    private Integer neutralPercentage;
    private Integer negativePercentage;

    private Double participationRate;

    @Column(columnDefinition = "TEXT")
    private String executiveSummary;

    private LocalDateTime generatedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportTheme> themes = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportRecommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportQuestionAnalysis> questionAnalysis = new ArrayList<>();

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReportExport export;
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public Integer getOverallScore() { return this.overallScore; }
    public Integer getPositivePercentage() { return this.positivePercentage; }
    public Integer getNeutralPercentage() { return this.neutralPercentage; }
    public Integer getNegativePercentage() { return this.negativePercentage; }
    public Double getParticipationRate() { return this.participationRate; }
    public String getExecutiveSummary() { return this.executiveSummary; }
    public LocalDateTime getGeneratedAt() { return this.generatedAt; }
    public List<ReportTheme> getThemes() { return this.themes; }
    public List<ReportRecommendation> getRecommendations() { return this.recommendations; }
    public List<ReportQuestionAnalysis> getQuestionAnalysis() { return this.questionAnalysis; }
    public ReportExport getExport() { return this.export; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public void setPositivePercentage(Integer positivePercentage) { this.positivePercentage = positivePercentage; }
    public void setNeutralPercentage(Integer neutralPercentage) { this.neutralPercentage = neutralPercentage; }
    public void setNegativePercentage(Integer negativePercentage) { this.negativePercentage = negativePercentage; }
    public void setParticipationRate(Double participationRate) { this.participationRate = participationRate; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public void setThemes(List<ReportTheme> themes) { this.themes = themes; }
    public void setRecommendations(List<ReportRecommendation> recommendations) { this.recommendations = recommendations; }
    public void setQuestionAnalysis(List<ReportQuestionAnalysis> questionAnalysis) { this.questionAnalysis = questionAnalysis; }
    public void setExport(ReportExport export) { this.export = export; }
}
