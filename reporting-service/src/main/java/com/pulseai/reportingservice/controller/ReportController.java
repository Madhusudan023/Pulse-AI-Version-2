package com.pulseai.reportingservice.controller;

import com.pulseai.reportingservice.entity.Report;
import com.pulseai.reportingservice.exception.ResourceNotFoundException;
import com.pulseai.reportingservice.repository.ReportRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Report APIs")
public class ReportController {

    private final ReportRepository reportRepository;

    @Operation(summary = "Endpoint for Report")
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportRepository.findAll());
    }

    @Operation(summary = "Endpoint for Report")
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id " + id));
    }

    @Operation(summary = "Endpoint for Report")
    @GetMapping("/my-region")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR', 'VP')")
    public ResponseEntity<List<Report>> getMyRegionReports(jakarta.servlet.http.HttpServletRequest request) {
        String region = (String) request.getAttribute("region");
        if ("GLOBAL".equalsIgnoreCase(region)) {
            return ResponseEntity.ok(reportRepository.findAll());
        }
        return ResponseEntity.ok(reportRepository.findByRegion(region));
    }
    
    // For a hackathon, we skip the /monthly and /comparison complex aggregations as they require custom JPQL.
    // They can be done in the frontend using the /reports data or added later.

    @Operation(summary = "Endpoint for Report")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id " + id));
                
        if (report.getExport() == null || report.getExport().getPdfPath() == null) {
            throw new ResourceNotFoundException("PDF not generated yet for report " + id);
        }
        
        File file = new File(report.getExport().getPdfPath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("PDF file missing from disk");
        }
        
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
    
    @Operation(summary = "Endpoint for Report")
    @GetMapping("/{id}/csv")
    public ResponseEntity<Resource> downloadCsv(@PathVariable Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id " + id));
                
        if (report.getExport() == null || report.getExport().getCsvPath() == null) {
            throw new ResourceNotFoundException("CSV not generated yet for report " + id);
        }
        
        File file = new File(report.getExport().getCsvPath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("CSV file missing from disk");
        }
        
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + id + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
    public ReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}
