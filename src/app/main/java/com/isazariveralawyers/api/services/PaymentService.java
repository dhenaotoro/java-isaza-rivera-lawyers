package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.PaymentCheckoutRequest;
import com.isazariveralawyers.api.dtos.PaymentLinkResponse;

public interface PaymentService {
    PaymentLinkResponse createPaymentLink(PaymentCheckoutRequest req);
    void handlePaymentWebhook(String provider, String payload);
}
