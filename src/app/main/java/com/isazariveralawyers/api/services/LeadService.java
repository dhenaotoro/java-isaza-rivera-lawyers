package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadIdResponse;
import com.isazariveralawyers.api.models.Lead;
import java.util.List;

public interface LeadService {
    LeadIdResponse create(LeadCreateRequest req);
    String confirm(Long id);
    List<Lead> getAll();
}
