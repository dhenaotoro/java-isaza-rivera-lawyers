package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadIdResponse;
import com.isazariveralawyers.api.models.Lead;
import com.isazariveralawyers.api.models.LeadStatus;
import com.isazariveralawyers.api.models.RequestType;
import com.isazariveralawyers.api.repositories.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Leads")
class LeadServiceImplTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private WhatsappService whatsappService;

    @InjectMocks
    private LeadServiceImpl leadService;

    private LeadCreateRequest leadCreateRequest;
    private Lead lead;

    @BeforeEach
    void setUp() {
        leadCreateRequest = new LeadCreateRequest();
        leadCreateRequest.setFirstName("Juan");
        leadCreateRequest.setLastName("García");
        leadCreateRequest.setEmail("juan@example.com");
        leadCreateRequest.setCity("Bogotá");
        leadCreateRequest.setPhone("3001234567");
        leadCreateRequest.setSummary("Necesito asesoría en derecho de familia");
        leadCreateRequest.setRequestType(RequestType.CHILD_SUPPORT);
        leadCreateRequest.setHasMinors(true);
        leadCreateRequest.setDataProcessingConsent(true);
        leadCreateRequest.setWhatsappConsent(true);
        leadCreateRequest.setSource("instagram");

        lead = new Lead();
        lead.setId(1L);
        lead.setFirstName("Juan");
        lead.setLastName("García");
        lead.setEmail("juan@example.com");
        lead.setCity("Bogotá");
        lead.setPhoneE164("+573001234567");
        lead.setSummary("Necesito asesoría en derecho de familia");
        lead.setRequestType(RequestType.CHILD_SUPPORT);
        lead.setHasMinors(true);
        lead.setDataProcessingConsent(true);
        lead.setWhatsappConsent(true);
        lead.setSource("instagram");
        lead.setStatus(LeadStatus.NEW);
    }

    @Test
    @DisplayName("Debe crear un nuevo lead exitosamente")
    void testCreateLead_Success() {
        // Arrange
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        // Act
        LeadIdResponse response = leadService.create(leadCreateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Debe crear lead con consentimiento de WhatsApp desactivado")
    void testCreateLead_WithoutWhatsappConsent() {
        // Arrange
        leadCreateRequest.setWhatsappConsent(false);
        lead.setWhatsappConsent(false);
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        // Act
        LeadIdResponse response = leadService.create(leadCreateRequest);

        // Assert
        assertNotNull(response);
        assertFalse(lead.isWhatsappConsent());
        verify(leadRepository, times(1)).save(any(Lead.class));
        verify(whatsappService, never()).sendConfirmationMessage(any(), any());
    }

    @Test
    @DisplayName("Debe confirmar un lead y cambiar su estado a CONFIRMED_APPOINTMENT")
    void testConfirmLead_Success() {
        // Arrange
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        // Act
        String result = leadService.confirm(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("leadId=1"));
        assertEquals(LeadStatus.CONFIRMED_APPOINTMENT, lead.getStatus());
        verify(leadRepository, times(1)).findById(1L);
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Debe retornar falso si el lead no existe")
    void testConfirmLead_NotFound() {
        // Arrange
        when(leadRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
            () -> leadService.confirm(999L));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(leadRepository, times(1)).findById(999L);
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Debe retornar falso si el lead no está en estado NEW")
    void testConfirmLead_InvalidStatus() {
        // Arrange
        lead.setStatus(LeadStatus.CONFIRMED_APPOINTMENT);
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));

        // Act
        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
            () -> leadService.confirm(1L));
        assertEquals(org.springframework.http.HttpStatus.CONFLICT, ex.getStatusCode());
        verify(leadRepository, times(1)).findById(1L);
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Debe normalizar número de teléfono a formato E.164")
    void testCreateLead_PhoneNormalization() {
        // Arrange
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        // Act
        LeadIdResponse response = leadService.create(leadCreateRequest);

        // Assert
        assertNotNull(response);
        verify(leadRepository, times(1)).save(ArgumentMatchers.argThat(l -> 
            l.getPhoneE164() != null && l.getPhoneE164().startsWith("+57")
        ));
    }

    @Test
    @DisplayName("Debe retornar todos los leads")
    void testGetAllLeads_Success() {
        // Arrange
        when(leadRepository.findAll()).thenReturn(List.of(lead));

        // Act
        var result = leadService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(lead.getId(), result.get(0).getId());
        verify(leadRepository, times(1)).findAll();
    }
}
