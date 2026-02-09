package com.isazariveralawyers.api.controllers;

import com.isazariveralawyers.api.dtos.LeadCreateRequest;
import com.isazariveralawyers.api.dtos.LeadCreateResponse;
import com.isazariveralawyers.api.models.Lead;
import com.isazariveralawyers.api.services.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/leads")
@CrossOrigin(origins = {"localhost", "https://react-isaza-rivera-lawyers.vercel.app"})
public class LeadController {
    private final LeadService service;
    public LeadController(LeadService service) { this.service = service; }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadCreateResponse create(@Valid @RequestBody LeadCreateRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<Lead> getAll() {
        return service.getAll();
    }

    @PostMapping("/{id}/confirm")
    public boolean confirm(@PathVariable Long id) {
        return service.confirm(id);
    }
}
