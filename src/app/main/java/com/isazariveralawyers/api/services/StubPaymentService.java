package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.dtos.PaymentCheckoutRequest;
import com.isazariveralawyers.api.dtos.PaymentLinkResponse;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class StubPaymentService implements PaymentService {
    @Override
    public PaymentLinkResponse createPaymentLink(PaymentCheckoutRequest req) {
        // In production, build in Wompi/ePayco/PlacetoPay y support PSE, Nequi, and Daviplata.
        String fakeUrl = "https://pagos.ejemplo.com/checkout/" + UUID.randomUUID();
        return new PaymentLinkResponse(fakeUrl, UUID.randomUUID().toString());  
    }


    @Override
    public void handlePaymentWebhook(String provider, String payload) {
        //TO-DO: Implement logic to handle payment webhooks from providers
    }
}
