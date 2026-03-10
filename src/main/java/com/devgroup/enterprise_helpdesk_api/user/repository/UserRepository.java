package com.devgroup.enterprise_helpdesk_api.user.repository;

import com.devgroup.enterprise_helpdesk_api.user.entity.RoleName;
import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Page<User> findByRoles_Name(RoleName roleName, Pageable pageable);
    boolean existsByEmailAndIdNot(String email, Long id);
}
