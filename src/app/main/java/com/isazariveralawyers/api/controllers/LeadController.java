package com.isazariveralawyers.api.controllers;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadResponse;
import com.isazariveralawyers.api.services.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {
    private final LeadService service;
    public LeadController(LeadService service) { this.service = service; }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadResponse create(@Valid @RequestBody LeadCreateRequest req) {
        return service.createLead(req);
    }


    @GetMapping("/{id}")
    public LeadResponse get(@PathVariable Long id) {
        return service.getLead(id);
    }


    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable Long id) {
        service.markConfirmed(id);
    }
}
