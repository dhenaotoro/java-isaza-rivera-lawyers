package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadResponse;

public interface LeadService {
    LeadResponse createLead(LeadCreateRequest req);
    LeadResponse getLead(Long id);
    void markConfirmed(Long id);
}
