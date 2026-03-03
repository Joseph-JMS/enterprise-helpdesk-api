package com.devgroup.enterprise_helpdesk_api.user.repository;

import com.devgroup.enterprise_helpdesk_api.user.entity.Role;
import com.devgroup.enterprise_helpdesk_api.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleName name);
    boolean existsByName(RoleName name);
}
