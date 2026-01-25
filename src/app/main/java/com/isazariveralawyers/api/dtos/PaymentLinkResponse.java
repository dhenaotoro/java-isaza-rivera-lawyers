package com.isazariveralawyers.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponse {
    private String paymentLinkUrl;
    private String providerReference;
}