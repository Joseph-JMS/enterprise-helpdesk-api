package com.devgroup.enterprise_helpdesk_api.user.service;

import com.devgroup.enterprise_helpdesk_api.user.dto.request.CreateUserRequest;
import com.devgroup.enterprise_helpdesk_api.user.dto.request.UpdateProfileRequest;
import com.devgroup.enterprise_helpdesk_api.user.dto.request.UpdateUserRequest;
import com.devgroup.enterprise_helpdesk_api.user.dto.response.UserResponse;
import com.devgroup.enterprise_helpdesk_api.user.entity.Role;
import com.devgroup.enterprise_helpdesk_api.user.entity.RoleName;
import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import com.devgroup.enterprise_helpdesk_api.user.exception.InvalidRoleException;
import com.devgroup.enterprise_helpdesk_api.user.exception.UserAlreadyExistsException;
import com.devgroup.enterprise_helpdesk_api.user.exception.UserNotFoundException;
import com.devgroup.enterprise_helpdesk_api.user.repository.RoleRepository;
import com.devgroup.enterprise_helpdesk_api.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("El username ya esta en uso");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El email ya esta en uso");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(resolveRoles(request.getRoles()));

        return toResponse(userRepository.save(user));
    }


    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public Page<UserResponse> findByRole(RoleName roleName, Pageable pageable) {
        return userRepository.findByRoles_Name(roleName, pageable)
                .map(this::toResponse);
    }


    public UserResponse findById(Long id) {
        return toResponse(getUserOrThrow(id));
    }

    public UserResponse findMe(String username) {
        return toResponse(getUserByUsernameOrThrow(username));
    }


    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = getUserOrThrow(id);

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new UserAlreadyExistsException("El email ya esta en uso");
        }

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(resolveRoles(request.getRoles()));

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = getUserByUsernameOrThrow(username);

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
            throw new UserAlreadyExistsException("El email ya esta en uso");
        }

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse toggle(Long id) {
        User user = getUserOrThrow(id);
        user.setEnabled(!user.isEnabled());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = getUserOrThrow(id);
        userRepository.delete(user);
    }


    private List<Role> resolveRoles(List<String> roleNames) {
        return roleNames.stream().map(roleStr -> {
            try {
                RoleName roleName = RoleName.valueOf(roleStr);
                return roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalStateException("Rol " + roleStr + " no encontrado en la DB"));
            } catch (IllegalArgumentException e) {
                throw new InvalidRoleException("Role no valido: " + roleStr);
            }
        }).toList();
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario con id " + id + " no encontrado"));
    }

    private User getUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario " + username + " no encontrado"));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEnabled(user.isEnabled());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList()
        );
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

}
