package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.CategoryDto;
import com.ecommerce.reglog.dto.response.CategoryResponse;
import com.ecommerce.reglog.entity.Category;
import com.ecommerce.reglog.exception.BadRequestException;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.CategoryRepository;
import com.ecommerce.reglog.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryDto dto) {
        String slug = dto.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = dto.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }
        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category with slug '" + slug + "' already exists");
        }

        Category category = new Category();
        category.setName(dto.getName().trim());
        category.setSlug(slug);
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        category.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setName(dto.getName().trim());
        if (dto.getSlug() != null && !dto.getSlug().isBlank()) {
            category.setSlug(dto.getSlug().trim());
        }
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        if (dto.getDisplayOrder() != null) {
            category.setDisplayOrder(dto.getDisplayOrder());
        }
        if (dto.getIsActive() != null) {
            category.setIsActive(dto.getIsActive());
        }
        category.setUpdatedAt(Instant.now());

        Category updated = categoryRepository.save(category);
        return CategoryResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setIsActive(false);
        categoryRepository.save(category);
    }
}
