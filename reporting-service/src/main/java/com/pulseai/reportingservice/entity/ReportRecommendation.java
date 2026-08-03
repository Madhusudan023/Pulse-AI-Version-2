package com.pulseai.reportingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_recommendation")
public class ReportRecommendation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "report_id")
    @JsonIgnore
    private Report report;

    @Column(columnDefinition = "TEXT")
    private String recommendation;
    public Report getReport() { return this.report; }
    public String getRecommendation() { return this.recommendation; }
    public void setReport(Report report) { this.report = report; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
