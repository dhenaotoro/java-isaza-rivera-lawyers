package com.isazariveralawyers.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PaymentCheckoutRequest {
    @NotNull
    private Long leadId;


    @Min(1000)
    private long amountInCents;


    private String description = "Asesoría en derecho de familia";


    // getters/setters
    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }
    public long getAmountInCents() { return amountInCents; }
    public void setAmountInCents(long amountInCents) { this.amountInCents = amountInCents; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
