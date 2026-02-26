package com.devgroup.enterprise_helpdesk_api.controllers;

import com.devgroup.enterprise_helpdesk_api.security.payloads.RegisterRequest;
import com.devgroup.enterprise_helpdesk_api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/create-user")
    public ResponseEntity<?> createTechnicalOrAdmin(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            return ResponseEntity.ok(Map.of("message", "Usuario administrativo creado con exito"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
