package com.isazariveralawyers.api.services;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class LeadReportEmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeadReportEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.reports.leads.email-from:no-reply@isazariveralawyers.com}")
    private String from;

    public LeadReportEmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void sendCsvReport(String recipient, String subject, String body, String fileName, byte[] csvBytes) {
        String safeRecipient = Objects.requireNonNull(recipient, "recipient is required");
        String safeSubject = Objects.requireNonNull(subject, "subject is required");
        String safeBody = Objects.requireNonNull(body, "body is required");
        String safeFileName = Objects.requireNonNull(fileName, "fileName is required");
        byte[] safeCsvBytes = Objects.requireNonNull(csvBytes, "csvBytes is required");
        String safeFrom = Objects.requireNonNull(from, "email from is required");

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            LOGGER.warn("JavaMailSender is not configured. Skipping lead report email to {}", safeRecipient);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(safeFrom);
            helper.setTo(safeRecipient);
            helper.setSubject(safeSubject);
            helper.setText(safeBody, false);
            helper.addAttachment(safeFileName, new ByteArrayResource(safeCsvBytes));
            mailSender.send(message);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to send lead report email", ex);
        }
    }
}