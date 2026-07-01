package com.devgroup.enterprise_helpdesk_api.user.controller;

import com.devgroup.enterprise_helpdesk_api.user.dto.request.ChangePasswordRequest;
import com.devgroup.enterprise_helpdesk_api.user.dto.request.CreateUserRequest;
import com.devgroup.enterprise_helpdesk_api.user.dto.request.UpdateProfileRequest;
import com.devgroup.enterprise_helpdesk_api.user.dto.request.UpdateUserRequest;
import com.devgroup.enterprise_helpdesk_api.user.dto.response.UserResponse;
import com.devgroup.enterprise_helpdesk_api.user.entity.RoleName;
import com.devgroup.enterprise_helpdesk_api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService authService) {
        this.userService = authService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest register) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userService.create(register));
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/by-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserResponse>> getByRole(@RequestParam RoleName roleName, Pageable pageable) {
        return ResponseEntity.ok(userService.findByRole(roleName, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.findMe(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }


    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggle(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               Authentication authentication) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

}
