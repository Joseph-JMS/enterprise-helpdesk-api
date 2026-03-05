package com.devgroup.enterprise_helpdesk_api.auth.service;

import com.devgroup.enterprise_helpdesk_api.auth.dto.response.AuthResponse;
import com.devgroup.enterprise_helpdesk_api.auth.entity.RefreshToken;
import com.devgroup.enterprise_helpdesk_api.auth.exception.InvalidTokenException;
import com.devgroup.enterprise_helpdesk_api.user.entity.Role;
import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import com.devgroup.enterprise_helpdesk_api.user.entity.RoleName;
import com.devgroup.enterprise_helpdesk_api.user.exception.UserAlreadyExistsException;
import com.devgroup.enterprise_helpdesk_api.user.repository.RoleRepository;
import com.devgroup.enterprise_helpdesk_api.user.repository.UserRepository;
import com.devgroup.enterprise_helpdesk_api.security.JwtUtils;
import com.devgroup.enterprise_helpdesk_api.auth.dto.request.RegisterRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public void registerUser(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("El username ya esta en uso");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        List<Role> roles = new ArrayList<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            roles.add(getRoleFromDb(RoleName.ROLE_USER));
        } else {
            request.getRoles().forEach(roleStr -> {
                RoleName roleName = RoleName.valueOf(roleStr);
                roles.add(getRoleFromDb(roleName));
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public AuthResponse buildAuthResponse(Authentication authentication) {
        String username = authentication.getName();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken = jwtUtils.generateAccessToken(authentication);
//        String refreshToken = refreshTokenService.createRefreshToken(username);

        return new AuthResponse(
                accessToken,
                null,
                username,
                roles,
                jwtUtils.getExpirationMs() / 1000
        );
    }

    public AuthResponse refreshAuth(String rawRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(rawRefreshToken);

        String username = refreshToken.getUser().getUsername();
        List<String> roles = refreshToken.getUser().getRoles().stream()
                .map(role -> role.getName().name())
                .toList();

        String newAccessToken = jwtUtils.generateAccessTokenFromUsername(username, roles);

        refreshTokenService.revokeByUsername(username);
        String newRefreshToken = refreshTokenService.createRefreshToken(username);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                username,
                roles,
                jwtUtils.getExpirationMs() / 1000
        );
    }

    public Map<String, Object> validateAuth(String token) {
        if (jwtUtils.isTokenInvalid(token)) {
            throw new InvalidTokenException("Token invalido o expirado");
        }

        return Map.of(
                "valid", true,
                "username", jwtUtils.getUsernameFromToken(token),
                "roles", jwtUtils.getRolesFromToken(token),
                "expiresAt", jwtUtils.getExpirationFromToken(token).toInstant().toString()
        );
    }

    private Role getRoleFromDb(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Rol " + name + " no encontrado en la DB."));
    }
}