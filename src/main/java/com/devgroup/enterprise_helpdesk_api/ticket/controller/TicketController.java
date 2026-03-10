package com.devgroup.enterprise_helpdesk_api.ticket.controller;

import com.devgroup.enterprise_helpdesk_api.ticket.dto.TicketRequest;
import com.devgroup.enterprise_helpdesk_api.ticket.dto.TicketResponse;
import com.devgroup.enterprise_helpdesk_api.ticket.dto.TicketStatusHistoryResponse;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatus;
import com.devgroup.enterprise_helpdesk_api.ticket.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.create(request, authentication.getName()));
    }


    @GetMapping("/my-tickets")
    public ResponseEntity<Page<TicketResponse>> getMyTickets(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(ticketService.findMyTickets(authentication.getName(), pageable));
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN')")
    public ResponseEntity<Page<TicketResponse>> getUnassigned(Pageable pageable) {
        return ResponseEntity.ok(ticketService.findUnassigned(pageable));
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN')")
    public ResponseEntity<Page<TicketResponse>> getAssignedToMe(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(ticketService.findAssignedToMe(authentication.getName(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<TicketResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ticketService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(ticketService.findById(id, authentication.getName(), getRoles(authentication)));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TicketStatusHistoryResponse>> getHistory(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(ticketService.findHistoryByTicket(id, authentication.getName(), getRoles(authentication)));
    }


    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN')")
    public ResponseEntity<TicketResponse> assign(@PathVariable Long id,
                                                 @RequestParam(required = false) String assignedUsername, Authentication authentication) {
        return ResponseEntity.ok(ticketService.assign(id, assignedUsername, authentication.getName(), getRoles(authentication)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(@PathVariable Long id, @RequestParam TicketStatus newStatus, Authentication authentication) {
        return ResponseEntity.ok(ticketService.changeStatus(id, newStatus, authentication.getName(), getRoles(authentication)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TicketResponse> cancel(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(ticketService.cancel(id, authentication.getName(), getRoles(authentication)));
    }

    private List<String> getRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

}
