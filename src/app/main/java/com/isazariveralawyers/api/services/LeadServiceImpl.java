package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadResponse;
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
    public LeadResponse createLead(LeadCreateRequest req) {
        Lead lead = new Lead();
        lead.setFirstName(req.getFirstName());
        lead.setLastName(req.getLastName());
        lead.setBirthDate(req.getBirthDate());
        lead.setEmail(req.getEmail());
        lead.setPhoneE164(PhoneUtils.toE164(req.getPhone()));
        lead.setRequestType(req.getRequestType());
        lead.setWhatsappConsent(Boolean.TRUE.equals(req.getWhatsappConsent()));
        lead = repo.save(lead);


        if (Boolean.TRUE.equals(lead.getWhatsappConsent())) {
            whatsappService.sendConfirmationMessage(
                lead.getPhoneE164(),
                "Hola "+lead.getFirstName()+", recibimos tu solicitud. Pronto una asesora te contactará."
            );
        }
        return map(lead);
    }


    @Override
    @Transactional(readOnly = true)
    public LeadResponse getLead(Long id) {
        return repo.findById(id).map(this::map).orElseThrow();
    }


    @Override
    @Transactional
    public void markConfirmed(Long id) {
        Lead lead = repo.findById(id).orElseThrow();
        lead.setStatus(LeadStatus.CONFIRMED_APPOINTMENT);
        repo.save(lead);
    }


    private LeadResponse map(Lead l) {
        LeadResponse dto = new LeadResponse();
        dto.setId(l.getId());
        dto.setFirstName(l.getFirstName());
        dto.setLastName(l.getLastName());
        dto.setBirthDate(l.getBirthDate());
        dto.setEmail(l.getEmail());
        dto.setPhoneE164(l.getPhoneE164());
        dto.setRequestType(l.getRequestType());
        dto.setStatus(l.getStatus());
        return dto;
    }
}
