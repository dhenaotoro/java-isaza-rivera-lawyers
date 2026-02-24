package com.isazariveralawyers.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.reports.leads")
public class ReportProperties {
    private String whatsappRecipient = "+573108216768";
    private String emailRecipient = "leslierivera.2503@gmail.com";
    private String emailSubject = "Reporte diario de leads";
}