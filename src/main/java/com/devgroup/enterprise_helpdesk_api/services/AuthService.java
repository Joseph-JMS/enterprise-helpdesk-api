package com.devgroup.enterprise_helpdesk_api.services;

import com.devgroup.enterprise_helpdesk_api.entities.Role;
import com.devgroup.enterprise_helpdesk_api.entities.User;
import com.devgroup.enterprise_helpdesk_api.enums.RoleName;
import com.devgroup.enterprise_helpdesk_api.repositories.RoleRepository;
import com.devgroup.enterprise_helpdesk_api.repositories.UserRepository;
import com.devgroup.enterprise_helpdesk_api.security.payloads.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Error: El username ya está en uso");
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

    private Role getRoleFromDb(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Error: Rol " + name + " no encontrado en la DB."));
    }
}