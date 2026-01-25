package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadResponse;

public interface LeadService {
    LeadResponse createLead(LeadCreateRequest req);
    boolean confirm(Long id);
}
