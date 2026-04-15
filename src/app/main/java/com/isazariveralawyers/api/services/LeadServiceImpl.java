package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadIdResponse;
import com.isazariveralawyers.api.models.Lead;
import com.isazariveralawyers.api.models.LeadStatus;
import com.isazariveralawyers.api.repositories.LeadRepository;
import com.isazariveralawyers.api.utils.PhoneUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository repo;
    private final WhatsappService whatsappService;
    private static final String CONFIRMATION_MESSAGE = "Lead confirmed successfully";


    public LeadServiceImpl(LeadRepository repo, WhatsappService whatsappService) {
        this.repo = repo; this.whatsappService = whatsappService;
    }

    @Override
    @Transactional
    public LeadIdResponse create(LeadCreateRequest req) {
        Lead lead = new Lead();
        lead.setFirstName(req.getFirstName());
        lead.setLastName(req.getLastName());
        lead.setEmail(req.getEmail());
        lead.setCity(req.getCity());
        lead.setPhoneE164(PhoneUtils.toE164(req.getPhone()));
        lead.setSummary(req.getSummary());
        lead.setSource(req.getSource());
        lead.setRequestType(req.getRequestType());
        lead.setHasMinors(req.isHasMinors());
        lead.setDataProcessingConsent(req.isDataProcessingConsent());
        lead.setWhatsappConsent(req.isWhatsappConsent());
        lead = repo.save(lead);
        sendWhatsappConfirmationIfAllowed(lead);
        var response = new LeadIdResponse();
        response.setId(lead.getId());
        return response;
    }

    public String confirm(Long id) {
        Long safeId = Objects.requireNonNull(id, "id is required");
        Optional<Lead> leadOptional = repo.findById(safeId);
        if (leadOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found");
        }
        Lead lead = leadOptional.get();
        if (lead.getStatus() != LeadStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lead already confirmed");
        }
        lead.setStatus(LeadStatus.CONFIRMED_APPOINTMENT);
        repo.save(lead);
        sendWhatsappConfirmationIfAllowed(lead);
        return CONFIRMATION_MESSAGE;
    }

    private void sendWhatsappConfirmationIfAllowed(Lead lead) {
        if (!lead.isWhatsappConsent() || lead.getPhoneE164() == null || lead.getPhoneE164().isBlank()) {
            return;
        }

        String firstName = lead.getFirstName() == null ? "" : lead.getFirstName().trim();
        String greetingName = firstName.isEmpty() ? "" : " " + firstName;
        whatsappService.sendConfirmationMessage(
            lead.getPhoneE164(),
            "Hola" + greetingName + ", recibimos tu solicitud. Pronto una asesora te contactará."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lead> getAll() {
        return repo.findAll();
    }
}
