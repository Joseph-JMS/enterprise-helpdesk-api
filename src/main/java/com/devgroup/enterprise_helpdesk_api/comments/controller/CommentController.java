package com.devgroup.enterprise_helpdesk_api.comments.controller;

import com.devgroup.enterprise_helpdesk_api.comments.dto.CommentRequest;
import com.devgroup.enterprise_helpdesk_api.comments.dto.CommentResponse;
import com.devgroup.enterprise_helpdesk_api.comments.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(@PathVariable Long ticketId,
                                                  @Valid @RequestBody CommentRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(ticketId, request, authentication.getName()));

    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getByTicket(@PathVariable Long ticketId,
                                                       Authentication authentication) {
        return ResponseEntity.ok(commentService.findByTicket(ticketId, authentication.getName(), getRoles(authentication)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> delete(@PathVariable Long ticketId,
                                                  @PathVariable Long commentId,
                                                  Authentication authentication) {
        commentService.delete(ticketId, commentId, authentication.getName(), getRoles(authentication));
        return ResponseEntity.noContent().build();
    }

    private List<String> getRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

}
