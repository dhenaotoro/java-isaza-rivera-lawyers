package com.isazariveralawyers.api.controllers;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadIdResponse;
import com.isazariveralawyers.api.models.Lead;
import com.isazariveralawyers.api.models.LeadStatus;
import com.isazariveralawyers.api.models.RequestType;
import com.isazariveralawyers.api.services.LeadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeadController.class)
@DisplayName("Pruebas del Controlador de Leads")
class LeadControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LeadService leadService() {
            return mock(LeadService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeadService leadService;

    private LeadCreateRequest leadCreateRequest;
    private LeadIdResponse leadIdResponse;

    @BeforeEach
    void setUp() {
        reset(leadService);
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

        leadIdResponse = new LeadIdResponse();
        leadIdResponse.setId(1L);
    }

    @Test
    @DisplayName("Debe crear un lead con POST /api/v1/leads")
    void testCreateLead_Success() throws Exception {
        // Arrange
        when(leadService.create(any(LeadCreateRequest.class))).thenReturn(leadIdResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(leadCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(leadService, times(1)).create(any(LeadCreateRequest.class));
    }

    @Test
    @DisplayName("Debe validar que el email sea requerido")
    void testCreateLead_MissingEmail() throws Exception {
        // Arrange
        leadCreateRequest.setEmail(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(leadCreateRequest)))
                .andExpect(status().isBadRequest());

        verify(leadService, never()).create(any(LeadCreateRequest.class));
    }

    @Test
    @DisplayName("Debe validar que el firstName sea requerido")
    void testCreateLead_MissingFirstName() throws Exception {
        // Arrange
        leadCreateRequest.setFirstName(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(leadCreateRequest)))
                .andExpect(status().isBadRequest());

        verify(leadService, never()).create(any(LeadCreateRequest.class));
    }

    @Test
    @DisplayName("Debe validar que el email sea un formato válido")
    void testCreateLead_InvalidEmail() throws Exception {
        // Arrange
        leadCreateRequest.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(leadCreateRequest)))
                .andExpect(status().isBadRequest());

        verify(leadService, never()).create(any(LeadCreateRequest.class));
    }

    @Test
    @DisplayName("Debe confirmar un lead con POST /api/v1/leads/{id}/confirm")
    void testConfirmLead_Success() throws Exception {
        // Arrange
        when(leadService.confirm(1L)).thenReturn("Lead confirmed successfully");

        // Act & Assert
        mockMvc.perform(post("/api/v1/leads/1/confirm"))
                .andExpect(status().isOk())
            .andExpect(content().string("Lead confirmed successfully"));

        verify(leadService, times(1)).confirm(1L);
    }

    @Test
    @DisplayName("Debe listar todos los leads con GET /api/v1/leads")
    void testGetAllLeads_Success() throws Exception {
        // Arrange
        Lead lead = new Lead();
        lead.setId(1L);
        lead.setFirstName("Juan");
        lead.setLastName("Garcia");
        lead.setEmail("juan@example.com");
        lead.setCity("Bogota");
        lead.setStatus(LeadStatus.NEW);

        when(leadService.getAll()).thenReturn(List.of(lead));

        // Act & Assert
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("juan@example.com"))
                .andExpect(jsonPath("$[0].status").value("NEW"));

        verify(leadService, times(1)).getAll();
    }

    @Test
        @DisplayName("Debe retornar 404 si el lead no existe")
        void testConfirmLead_NotFound() throws Exception {
        // Arrange
        when(leadService.confirm(999L))
            .thenThrow(new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Lead not found"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/leads/999/confirm"))
            .andExpect(status().isNotFound());

        verify(leadService, times(1)).confirm(999L);
        }
}
