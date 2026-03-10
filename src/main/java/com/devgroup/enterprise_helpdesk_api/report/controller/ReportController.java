package com.devgroup.enterprise_helpdesk_api.report.controller;

import com.devgroup.enterprise_helpdesk_api.report.dto.*;
import com.devgroup.enterprise_helpdesk_api.report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SummaryResponse> getSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    @GetMapping("/by-category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CategoryReportResponse>> getByCategory() {
        return ResponseEntity.ok(reportService.getByCategory());
    }

    @GetMapping("/by-technician")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<TechnicianReportResponse>> getByTechnician() {
        return ResponseEntity.ok(reportService.getByTechnician());
    }

    @GetMapping("/by-priority")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PriorityReportResponse>> getByPriority() {
        return ResponseEntity.ok(reportService.getByPriority());
    }

    @GetMapping("/average-resolution-time")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AverageResolutionResponse>> getAverageResolutionTime() {
        return ResponseEntity.ok(reportService.getAverageResolutionTime());
    }

    @GetMapping("/sla")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<SlaReportResponse>> getSla() {
        return ResponseEntity.ok(reportService.getSla());
    }

    @GetMapping("/my-performance")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN')")
    public ResponseEntity<TechnicianReportResponse> getMyPerformance(Authentication authentication) {
        return ResponseEntity.ok(reportService.getMyPerformance(authentication.getName()));
    }

    @GetMapping("/my-sla")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN')")
    public ResponseEntity<List<SlaReportResponse>> getMySla(Authentication authentication) {
        return ResponseEntity.ok(reportService.getMySla(authentication.getName()));
    }
}
