package com.pulseai.reportingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_theme")
public class ReportTheme extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "report_id")
    @JsonIgnore
    private Report report;

    private String theme;
    
    private String type; // POSITIVE or NEGATIVE
    public Report getReport() { return this.report; }
    public String getTheme() { return this.theme; }
    public String getType() { return this.type; }
    public void setReport(Report report) { this.report = report; }
    public void setTheme(String theme) { this.theme = theme; }
    public void setType(String type) { this.type = type; }
}
