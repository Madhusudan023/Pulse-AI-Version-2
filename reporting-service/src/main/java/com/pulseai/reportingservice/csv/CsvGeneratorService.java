package com.pulseai.reportingservice.csv;

import com.opencsv.CSVWriter;
import com.pulseai.reportingservice.entity.Report;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CsvGeneratorService {

    public String generateCsv(Report report) {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "report_" + report.getId() + ".csv");
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile()))) {
            // Header
            writer.writeNext(new String[]{"Survey ID", "Region", "Month", "Year", "Overall Score", "Positive %", "Neutral %", "Negative %", "Participation Rate"});
            
            // Data
            writer.writeNext(new String[]{
                    String.valueOf(report.getSurveyId()),
                    report.getRegion(),
                    String.valueOf(report.getMonth()),
                    String.valueOf(report.getYear()),
                    String.valueOf(report.getOverallScore()),
                    String.valueOf(report.getPositivePercentage()),
                    String.valueOf(report.getNeutralPercentage()),
                    String.valueOf(report.getNegativePercentage()),
                    String.valueOf(report.getParticipationRate())
            });
            
            writer.writeNext(new String[]{""});
            writer.writeNext(new String[]{"Executive Summary"});
            writer.writeNext(new String[]{report.getExecutiveSummary()});
            
            return path.toString();
        } catch (Exception e) {
            log.error("Failed to generate CSV for report {}", report.getId(), e);
            return null;
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CsvGeneratorService.class);
}
