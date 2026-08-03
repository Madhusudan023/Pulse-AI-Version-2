package com.pulseai.reportingservice.seeder;

import com.pulseai.reportingservice.entity.Report;
import com.pulseai.reportingservice.entity.ReportExport;
import com.pulseai.reportingservice.entity.ReportRecommendation;
import com.pulseai.reportingservice.entity.ReportTheme;
import com.pulseai.reportingservice.repository.ReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ReportRepository reportRepository;
    private final Random random = new Random();

    public DataSeeder(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (reportRepository.count() == 0) {
            System.out.println("Seeding initial mock reports for VP Dashboard trend charts...");
            
            String[] regions = {"APAC", "EMEA", "AMERICAS", "GLOBAL"};
            
            for (String region : regions) {
                // Generate reports for the last 6 months
                for (int i = 0; i < 6; i++) {
                    Report r = new Report();
                    r.setSurveyId((long) (random.nextInt(100) + 1));
                    r.setRegion(region);
                    r.setMonth(LocalDateTime.now().minusMonths(i).getMonthValue());
                    r.setYear(LocalDateTime.now().minusMonths(i).getYear());
                    
                    // Slightly trend upwards over time (i=0 is current month)
                    int baseScore = 65 + (5 - i) * 3;
                    r.setOverallScore(baseScore + random.nextInt(15));
                    
                    r.setPositivePercentage(r.getOverallScore());
                    r.setNegativePercentage(random.nextInt(100 - r.getPositivePercentage()));
                    r.setNeutralPercentage(100 - r.getPositivePercentage() - r.getNegativePercentage());
                    
                    r.setParticipationRate(70.0 + random.nextInt(25));
                    r.setExecutiveSummary("Overall engagement in " + region + " for month " + r.getMonth() + " shows strong indicators in workload balance, but communication needs improvement.");
                    r.setGeneratedAt(LocalDateTime.now().minusMonths(i));
                    
                    // Add themes
                    List<ReportTheme> themes = new ArrayList<>();
                    String[] sampleThemes = {"Work-Life Balance", "Management Support", "Career Growth", "Communication"};
                    for(int t=0; t<2; t++) {
                        ReportTheme theme = new ReportTheme();
                        theme.setTheme(sampleThemes[random.nextInt(sampleThemes.length)]);
                        theme.setType(random.nextBoolean() ? "POSITIVE" : "NEGATIVE");
                        theme.setReport(r);
                        themes.add(theme);
                    }
                    r.setThemes(themes);
                    
                    // Add recommendations
                    List<ReportRecommendation> recs = new ArrayList<>();
                    ReportRecommendation rec = new ReportRecommendation();
                    rec.setRecommendation("Schedule weekly 1-on-1s to address communication gaps.");
                    rec.setReport(r);
                    recs.add(rec);
                    r.setRecommendations(recs);
                    
                    // Add dummy export paths just so the frontend doesn't crash if it expects the object
                    ReportExport export = new ReportExport();
                    export.setPdfPath("/tmp/reports/report_" + r.getId() + ".pdf");
                    export.setCsvPath("/tmp/reports/report_" + r.getId() + ".csv");
                    export.setGeneratedAt(LocalDateTime.now().minusMonths(i));
                    export.setReport(r);
                    r.setExport(export);
                    
                    reportRepository.save(r);
                }
            }
            System.out.println("Mock reports seeded successfully.");
        }
    }
}
