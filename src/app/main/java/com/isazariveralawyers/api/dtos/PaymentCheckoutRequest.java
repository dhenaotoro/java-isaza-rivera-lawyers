package com.isazariveralawyers.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentCheckoutRequest {
    @NotNull
    private Long leadId;


    @Min(1000)
    private long amountInCents;


    private String description = "Asesoría en derecho de familia";
}
