package com.devgroup.enterprise_helpdesk_api.user.controller;

import com.devgroup.enterprise_helpdesk_api.auth.dto.request.RegisterRequest;
import com.devgroup.enterprise_helpdesk_api.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Acceso concedido");
    }

    @PostMapping("/create-user")
    public ResponseEntity<Map<String, String>> createTechnicalOrAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
            authService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario administrativo creado con exito"));
    }
}
