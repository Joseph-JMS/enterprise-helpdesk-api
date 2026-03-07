package com.devgroup.enterprise_helpdesk_api.auth.controller;

import com.devgroup.enterprise_helpdesk_api.auth.dto.response.AuthResponse;
import com.devgroup.enterprise_helpdesk_api.auth.exception.InvalidTokenException;
import com.devgroup.enterprise_helpdesk_api.auth.exception.RefreshTokenException;
import com.devgroup.enterprise_helpdesk_api.auth.service.RefreshTokenService;
import com.devgroup.enterprise_helpdesk_api.auth.dto.request.LoginRequest;
import com.devgroup.enterprise_helpdesk_api.auth.dto.request.RegisterRequest;
import com.devgroup.enterprise_helpdesk_api.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuario registrado exitosamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String rawRefreshToken = refreshTokenService.createRefreshToken(request.getUsername());
        AuthResponse authResponse = authService.buildAuthResponse(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(rawRefreshToken).toString())
                .body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = "refreshToken", required = false) String rawRefreshToken) {
        if (rawRefreshToken == null) {
            throw new RefreshTokenException("Refresh token no encontrado");
        }

        AuthResponse authResponse = authService.refreshAuth(rawRefreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshTokenCookie(authResponse.getRefreshToken()).toString())
                .body(new AuthResponse(
                        authResponse.getAccessToken(),
                        null,
                        authResponse.getUsername(),
                        authResponse.getRoles(),
                        authResponse.getExpiresIn()
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            refreshTokenService.revokeByUsername(authentication.getName());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie().toString())
                .body(Map.of("message", "Sesion cerrada exitosamente"));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String,Object>> validate(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Header de autorizacion faltante o invalida");
        }
        String token = authHeader.substring(7);
        return ResponseEntity.ok(authService.validateAuth(token));
    }


    private ResponseCookie buildRefreshTokenCookie(String rawRefreshToken) {
        return ResponseCookie.from("refreshToken", rawRefreshToken)
                .httpOnly(true)
                .secure(false) //Cambiar a true para prod
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(Duration.ZERO)
                .sameSite("Strict")
                .build();
    }

}
