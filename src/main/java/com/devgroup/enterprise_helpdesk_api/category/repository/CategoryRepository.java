package com.devgroup.enterprise_helpdesk_api.category.repository;

import com.devgroup.enterprise_helpdesk_api.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    List<Category> findAllByEnabledTrue();
}
