package com.pulseai.reportingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_export")
public class ReportExport extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "report_id")
    @JsonIgnore
    private Report report;

    private String pdfPath;
    
    private String csvPath;
    
    private LocalDateTime generatedAt;
    public Report getReport() { return this.report; }
    public String getPdfPath() { return this.pdfPath; }
    public String getCsvPath() { return this.csvPath; }
    public LocalDateTime getGeneratedAt() { return this.generatedAt; }
    public void setReport(Report report) { this.report = report; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public void setCsvPath(String csvPath) { this.csvPath = csvPath; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
