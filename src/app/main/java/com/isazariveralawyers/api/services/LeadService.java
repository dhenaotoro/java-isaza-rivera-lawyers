package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadCreateResponse;

public interface LeadService {
    LeadCreateResponse create(LeadCreateRequest req);
    boolean confirm(Long id);
}
