package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.config.ReportProperties;
import com.isazariveralawyers.api.models.Lead;
import com.isazariveralawyers.api.repositories.LeadRepository;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LeadReportScheduler {
    private static final Logger log = LoggerFactory.getLogger(LeadReportScheduler.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final LeadRepository leadRepository;
    private final WhatsappService whatsappService;
    private final LeadReportEmailService leadReportEmailService;
    private final ReportProperties reportProperties;

    @Value("${app.reports.leads.cron:0 0 18 * * *}")
    private String cronExpression = "0 0 18 * * *";

    @Value("${app.reports.leads.zone:America/Bogota}")
    private String scheduleZone = "America/Bogota";

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

    @PostConstruct
    void logSchedulerConfiguration() {
        String systemDefaultZone = ZoneId.systemDefault().getId();
        String systemTime = ZonedDateTime.now().toString();
        String bogotaTime = ZonedDateTime.now(ZoneId.of(scheduleZone)).toString();
        log.warn("========== SCHEDULER TIMEZONE DEBUG ==========");
        log.warn("System default timezone: {}", systemDefaultZone);
        log.warn("Current system time: {}", systemTime);
        log.warn("Current Bogota time: {}", bogotaTime);
        log.warn("Scheduler timezone: {}", scheduleZone);
        log.warn("=============================================");
        log.info(
            "Lead report scheduler configured. cron='{}', zone='{}', emailRecipient='{}', whatsappRecipient='{}'",
            cronExpression,
            scheduleZone,
            reportProperties.getEmailRecipient(),
            reportProperties.getWhatsappRecipient()
        );
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "${app.reports.leads.cron:0 0 18 * * *}", zone = "${app.reports.leads.zone:America/Bogota}")
    public void exportAndSendLeadsReport() {
        ZonedDateTime startedAt = ZonedDateTime.now(ZoneId.of(scheduleZone));
        ZonedDateTime systemTime = ZonedDateTime.now();
        log.warn("========== SCHEDULER JOB TRIGGERED ==========");
        log.warn("System time (UTC): {}", systemTime);
        log.warn("Bogota time: {}", startedAt);
        log.warn("=============================================");
        try {
            List<Lead> leads = leadRepository.findAll();
            log.info("Lead report job started at {} with {} lead(s).", startedAt, leads.size());

            String csv = toCsv(leads);
            byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);
            String date = LocalDate.now(ZoneId.of(scheduleZone)).format(DATE_FORMATTER);
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

            log.info(
                "Lead report job completed successfully. fileName='{}', leadCount={}, startedAt='{}'",
                fileName,
                leads.size(),
                startedAt
            );
        } catch (Exception ex) {
            log.error(
                "Lead report job failed. cron='{}', zone='{}', startedAt='{}'",
                cronExpression,
                scheduleZone,
                startedAt,
                ex
            );
            throw ex;
        }
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