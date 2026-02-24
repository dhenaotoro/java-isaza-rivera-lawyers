package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.config.ReportProperties;
import com.isazariveralawyers.api.models.Lead;
import com.isazariveralawyers.api.repositories.LeadRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LeadReportScheduler {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final LeadRepository leadRepository;
    private final WhatsappService whatsappService;
    private final LeadReportEmailService leadReportEmailService;
    private final ReportProperties reportProperties;

    public LeadReportScheduler(
        LeadRepository leadRepository,
        WhatsappService whatsappService,
        LeadReportEmailService leadReportEmailService,
        ReportProperties reportProperties
    ) {
        this.leadRepository = leadRepository;
        this.whatsappService = whatsappService;
        this.leadReportEmailService = leadReportEmailService;
        this.reportProperties = reportProperties;
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "${app.reports.leads.cron:0 0 18 * * *}", zone = "${app.reports.leads.zone:America/Bogota}")
    public void exportAndSendLeadsReport() {
        List<Lead> leads = leadRepository.findAll();
        String csv = toCsv(leads);
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);
        String date = LocalDate.now().format(DATE_FORMATTER);
        String fileName = "leads-" + date + ".csv";

        whatsappService.sendDocumentMessage(
            reportProperties.getWhatsappRecipient(),
            fileName,
            csvBytes,
            "Reporte diario de leads"
        );

        leadReportEmailService.sendCsvReport(
            reportProperties.getEmailRecipient(),
            reportProperties.getEmailSubject(),
            "Adjunto encontrarás el reporte diario de leads en formato CSV.",
            fileName,
            csvBytes
        );
    }

    String toCsv(List<Lead> leads) {
        StringBuilder builder = new StringBuilder();
        builder.append("name,city,request_type,description,email,cellphone\n");

        for (Lead lead : leads) {
            String fullName = joinName(lead.getFirstName(), lead.getLastName());
            builder
                .append(escapeCsv(fullName)).append(',')
                .append(escapeCsv(lead.getCity())).append(',')
                .append(escapeCsv(lead.getRequestType() == null ? "" : lead.getRequestType().name())).append(',')
                .append(escapeCsv(lead.getSummary())).append(',')
                .append(escapeCsv(lead.getEmail())).append(',')
                .append(escapeCsv(lead.getPhoneE164()))
                .append('\n');
        }

        return builder.toString();
    }

    private String joinName(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        return (first + " " + last).trim();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}