package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.config.ReportProperties;
import com.isazariveralawyers.api.models.Lead;
import com.isazariveralawyers.api.models.RequestType;
import com.isazariveralawyers.api.repositories.LeadRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del reporte programado de leads")
class LeadReportSchedulerTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private WhatsappService whatsappService;

    @Mock
    private LeadReportEmailService leadReportEmailService;

    private LeadReportScheduler scheduler;

    @BeforeEach
    void setUp() {
        ReportProperties reportProperties = new ReportProperties();
        reportProperties.setWhatsappRecipient("+573108216768");
        reportProperties.setEmailRecipient("leslierivera.2503@gmail.com");
        reportProperties.setEmailSubject("Reporte diario de leads");
        scheduler = new LeadReportScheduler(leadRepository, whatsappService, leadReportEmailService, reportProperties);
    }

    @Test
    @DisplayName("Debe exportar columnas solicitadas y enviar CSV por WhatsApp y correo")
    void exportAndSendLeadsReport_SendsCsvToWhatsappAndEmail() {
        Lead lead = new Lead();
        lead.setFirstName("Ana");
        lead.setLastName("Perez");
        lead.setCity("Bogota");
        lead.setRequestType(RequestType.DIVORCED);
        lead.setSummary("Necesito asesoría legal");
        lead.setEmail("ana@example.com");
        lead.setPhoneE164("+573001234567");
        when(leadRepository.findAll()).thenReturn(List.of(lead));

        scheduler.exportAndSendLeadsReport();

        ArgumentCaptor<byte[]> csvCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(whatsappService, times(1)).sendDocumentMessage(
            org.mockito.ArgumentMatchers.eq("+573108216768"),
            org.mockito.ArgumentMatchers.contains("leads-"),
            csvCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("Reporte diario de leads")
        );
        String csv = new String(csvCaptor.getValue(), StandardCharsets.UTF_8);
        assertTrue(csv.contains("name,city,request_type,description,email,cellphone"));
        assertTrue(csv.contains("Ana Perez,Bogota,DIVORCED,Necesito asesoría legal,ana@example.com,+573001234567"));

        verify(leadReportEmailService, times(1)).sendCsvReport(
            org.mockito.ArgumentMatchers.eq("leslierivera.2503@gmail.com"),
            org.mockito.ArgumentMatchers.eq("Reporte diario de leads"),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.contains("leads-"),
            org.mockito.ArgumentMatchers.any(byte[].class)
        );
    }

    @Test
    @DisplayName("Debe escapar correctamente comillas y comas en CSV")
    void toCsv_EscapesSpecialCharacters() {
        Lead lead = new Lead();
        lead.setFirstName("Ana,\"Mari\"");
        lead.setLastName("Perez");
        lead.setCity("Bogota");
        lead.setRequestType(RequestType.CUSTODY);
        lead.setSummary("Caso con, coma");
        lead.setEmail("ana@example.com");
        lead.setPhoneE164("+573001234567");

        String csv = scheduler.toCsv(List.of(lead));

        assertTrue(csv.contains("\"Ana,\"\"Mari\"\" Perez\""));
        assertTrue(csv.contains("\"Caso con, coma\""));
    }
}