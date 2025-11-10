package com.isazariveralawyers.api.controllers;

import com.isazariveralawyers.api.dtos.PaymentCheckoutRequest;
import com.isazariveralawyers.api.dtos.PaymentLinkResponse;
import com.isazariveralawyers.api.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }


    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentLinkResponse checkout(@Valid @RequestBody PaymentCheckoutRequest req) {
        return paymentService.createPaymentLink(req);
    }
}
