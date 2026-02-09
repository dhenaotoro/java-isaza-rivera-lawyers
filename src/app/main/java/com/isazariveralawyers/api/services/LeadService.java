package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadCreateResponse;
import com.isazariveralawyers.api.models.Lead;
import java.util.List;

public interface LeadService {
    LeadCreateResponse create(LeadCreateRequest req);
    boolean confirm(Long id);
    List<Lead> getAll();
}
