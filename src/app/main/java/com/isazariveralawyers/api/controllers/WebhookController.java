package com.isazariveralawyers.api.controllers;

import com.isazariveralawyers.api.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final PaymentService paymentService;
    public WebhookController(PaymentService paymentService) { this.paymentService = paymentService; }


    @PostMapping("/payments/{provider}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void paymentWebhook(@PathVariable String provider, @RequestBody String payload) {
        paymentService.handlePaymentWebhook(provider, payload);
    }
}
