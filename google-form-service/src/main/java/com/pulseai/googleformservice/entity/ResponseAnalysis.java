package com.pulseai.googleformservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "response_analysis")
public class ResponseAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    @Column(name = "positive_percentage")
    private Double positivePercentage;

    @Column(name = "neutral_percentage")
    private Double neutralPercentage;

    @Column(name = "negative_percentage")
    private Double negativePercentage;

    @Column(name = "pulse_score")
    private Double pulseScore;

    @Column(name = "participation_rate")
    private Double participationRate;

    @Column(name = "executive_summary", columnDefinition = "TEXT")
    private String executiveSummary;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public Double getPositivePercentage() { return positivePercentage; }
    public void setPositivePercentage(Double positivePercentage) { this.positivePercentage = positivePercentage; }
    public Double getNeutralPercentage() { return neutralPercentage; }
    public void setNeutralPercentage(Double neutralPercentage) { this.neutralPercentage = neutralPercentage; }
    public Double getNegativePercentage() { return negativePercentage; }
    public void setNegativePercentage(Double negativePercentage) { this.negativePercentage = negativePercentage; }
    public Double getPulseScore() { return pulseScore; }
    public void setPulseScore(Double pulseScore) { this.pulseScore = pulseScore; }
    public Double getParticipationRate() { return participationRate; }
    public void setParticipationRate(Double participationRate) { this.participationRate = participationRate; }
    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
}
