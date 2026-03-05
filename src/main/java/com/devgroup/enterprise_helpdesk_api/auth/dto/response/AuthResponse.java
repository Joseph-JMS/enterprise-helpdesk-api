package com.devgroup.enterprise_helpdesk_api.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private List<String> roles;
    private long expiresIn;

    public AuthResponse(String accessToken, String refreshToken, String username, List<String> roles, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.roles = roles;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @JsonIgnore
    public String getRefreshToken() {
        return refreshToken;
    }
    public String getUsername() {
        return username;
    }
    public List<String> getRoles() {
        return roles;
    }
    public long getExpiresIn() {
        return expiresIn;
    }
}
