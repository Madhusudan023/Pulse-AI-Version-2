package com.pulseai.reportingservice.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.pulseai.reportingservice.entity.Report;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfGeneratorService {

    public String generatePdf(Report report) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("Employee Pulse Survey Report", titleFont));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Region: " + report.getRegion()));
            document.add(new Paragraph("Month/Year: " + report.getMonth() + "/" + report.getYear()));
            document.add(new Paragraph("Overall Score: " + report.getOverallScore()));
            document.add(new Paragraph("Participation Rate: " + report.getParticipationRate() + "%"));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Executive Summary:"));
            document.add(new Paragraph(report.getExecutiveSummary()));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Positive Themes:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            report.getThemes().stream().filter(t -> "POSITIVE".equals(t.getType()))
                    .forEach(t -> {
                        try { document.add(new Paragraph("• " + t.getTheme())); } catch (Exception e) {}
                    });
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Negative Themes:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            report.getThemes().stream().filter(t -> "NEGATIVE".equals(t.getType()))
                    .forEach(t -> {
                        try { document.add(new Paragraph("• " + t.getTheme())); } catch (Exception e) {}
                    });
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("AI Recommendations:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            report.getRecommendations().forEach(r -> {
                try { document.add(new Paragraph("• " + r.getRecommendation())); } catch (Exception e) {}
            });
            
            document.close();
            
            // In a real app we might upload this to S3. For hackathon, we can write to a local temp dir.
            Path path = Paths.get(System.getProperty("java.io.tmpdir"), "report_" + report.getId() + ".pdf");
            Files.write(path, out.toByteArray());
            
            return path.toString();
        } catch (Exception e) {
            log.error("Failed to generate PDF for report {}", report.getId(), e);
            return null;
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PdfGeneratorService.class);
}
