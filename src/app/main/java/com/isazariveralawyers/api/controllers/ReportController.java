package com.isazariveralawyers.api.controllers;

import com.isazariveralawyers.api.services.LeadReportScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final LeadReportScheduler leadReportScheduler;

    public ReportController(LeadReportScheduler leadReportScheduler) {
        this.leadReportScheduler = leadReportScheduler;
    }

    @PostMapping("/leads/trigger")
    public ResponseEntity<String> triggerLeadReport() {
        try {
            leadReportScheduler.exportAndSendLeadsReport();
            return ResponseEntity.ok("Lead report triggered successfully. Check logs for details.");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Failed to trigger report: " + ex.getMessage());
        }
    }
}
