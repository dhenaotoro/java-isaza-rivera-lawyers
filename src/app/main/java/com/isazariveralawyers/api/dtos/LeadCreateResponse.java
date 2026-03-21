package com.isazariveralawyers.api.dtos;

import com.isazariveralawyers.api.models.LeadStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeadCreateResponse {
    private Long id;
    private LeadStatus status;
    private Schedule schedule;
}
