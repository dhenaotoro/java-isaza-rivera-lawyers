package com.isazariveralawyers.api.dtos;

import com.isazariveralawyers.api.models.LeadStatus;
import com.isazariveralawyers.api.models.RequestType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Schedule {
    private String lawyerAUrl;
    private String lawyerBUrl;
}

@Getter
@Setter
public class LeadCreateResponse {
    private Long id;
    private LeadStatus status;
    private Schedule schedule;
}
