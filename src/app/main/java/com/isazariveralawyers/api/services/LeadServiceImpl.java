package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadCreateResponse;
import com.isazariveralawyers.api.entities.Lead;
import com.isazariveralawyers.api.entities.LeadStatus;
import com.isazariveralawyers.api.repositories.LeadRepository;
import com.isazariveralawyers.api.utils.PhoneUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository repo;
    private final WhatsappService whatsappService;


    public LeadServiceImpl(LeadRepository repo, WhatsappService whatsappService) {
        this.repo = repo; this.whatsappService = whatsappService;
    }

    @Override
    @Transactional
    public LeadCreateResponse create(LeadCreateRequest req) {
        Lead lead = new Lead();
        lead.setFirstName(req.getFirstName());
        lead.setLastName(req.getLastName());
        lead.setEmail(req.getEmail());
        lead.setCity(req.getCity());
        lead.setPhoneE164(PhoneUtils.toE164(req.getPhone()));
        lead.setSummary(req.getSummary());
        lead.setSource(req.getSource());
        lead.setRequestType(req.getRequestType());
        lead.setHasMinors(Boolean.TRUE.equals(req.getHasMinors()));
        lead.setDataProcessingConsent(Boolean.TRUE.equals(req.getDataProcessingConsent()));
        lead.setWhatsappConsent(Boolean.TRUE.equals(req.getWhatsappConsent()));
        lead = repo.save(lead);


        if (Boolean.TRUE.equals(lead.getWhatsappConsent())) {
            // Notify via WhatsApp
            /* whatsappService.sendConfirmationMessage(
                lead.getPhoneE164(),
                "Hola "+lead.getFirstName()+", recibimos tu solicitud. Pronto una asesora te contactará."
            ); */
        }
        return map(lead);
    }

    public boolean confirm(Long id) {
        Optional<Lead> leadOptional = repo.findById(id);
        if (leadOptional.isEmpty()) {
            return false;
        }
        Lead lead = leadOptional.get();
        if (lead.getStatus() != LeadStatus.NEW) {
            return false; 
        }
        lead.setStatus(LeadStatus.CONFIRMED);
        repo.save(lead);

        if (Boolean.TRUE.equals(lead.getWhatsappConsent())) {
            // Notify via WhatsApp
            /* whatsappService.sendConfirmationMessage(
                lead.getPhoneE164(),
                "Hola "+lead.getFirstName()+", recibimos tu solicitud. Pronto una asesora te contactará."
            ); */
        }
        return true;
    }

    private LeadResponse map(Lead l) {
        var dto = new LeadCreateResponse();
        dto.setId(l.getId());
        dto.setStatus(l.getStatus());

        var schedule = new Schedule();
        schedule.setLawyerAUrl(String.format("https://calendly.com/lawyer-a/consulting/leadId=%d", l.getId()));
        schedule.setLawyerBUrl(String.format("https://calendly.com/lawyer-b/consulting/leadId=%d", l.getId()));
        dto.setSchedule();
        return dto;
    }
}
