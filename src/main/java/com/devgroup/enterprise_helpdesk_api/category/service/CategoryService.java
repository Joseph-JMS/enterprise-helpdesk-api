package com.devgroup.enterprise_helpdesk_api.category.service;

import com.devgroup.enterprise_helpdesk_api.category.dto.CategoryRequest;
import com.devgroup.enterprise_helpdesk_api.category.dto.CategoryResponse;
import com.devgroup.enterprise_helpdesk_api.category.entity.Category;
import com.devgroup.enterprise_helpdesk_api.category.exception.CategoryAlreadyExistsException;
import com.devgroup.enterprise_helpdesk_api.category.exception.CategoryInUseException;
import com.devgroup.enterprise_helpdesk_api.category.exception.CategoryNotFoundException;
import com.devgroup.enterprise_helpdesk_api.category.repository.CategoryRepository;
import com.devgroup.enterprise_helpdesk_api.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;

    public CategoryService(CategoryRepository categoryRepository, TicketRepository ticketRepository) {
        this.categoryRepository = categoryRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CategoryResponse> findAllEnabled() {
        return categoryRepository.findAllByEnabledTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        return toResponse(getCategoryOrThrow(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("La categoria " + request.getName() + "ya existe");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getCategoryOrThrow(id);

        if (!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("La categoria " + request.getName() + "ya existe");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = getCategoryOrThrow(id);

        if (ticketRepository.existsByCategory(category)) {
            throw new CategoryInUseException("No se puede eliminar la categoria porque tiene tickets asociados");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryResponse toggle(Long id) {
        Category category = getCategoryOrThrow(id);
        category.setEnabled(!category.isEnabled());
        return toResponse(categoryRepository.save(category));
    }


    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Categoria con id " + id + "no encontrado"));
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setEnabled(category.isEnabled());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
