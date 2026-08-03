package com.pulseai.reportingservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_question_analysis")
public class ReportQuestionAnalysis extends BaseEntity {

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "report_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Report report;
    
    private String questionText;
    private Integer positivePercentage;
    private Integer neutralPercentage;
    private Integer negativePercentage;
    private String summary;
    
    public ReportQuestionAnalysis() {}

    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public Integer getPositivePercentage() { return positivePercentage; }
    public void setPositivePercentage(Integer positivePercentage) { this.positivePercentage = positivePercentage; }

    public Integer getNeutralPercentage() { return neutralPercentage; }
    public void setNeutralPercentage(Integer neutralPercentage) { this.neutralPercentage = neutralPercentage; }

    public Integer getNegativePercentage() { return negativePercentage; }
    public void setNegativePercentage(Integer negativePercentage) { this.negativePercentage = negativePercentage; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
