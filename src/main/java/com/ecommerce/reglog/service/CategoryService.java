package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.CategoryDto;
import com.ecommerce.reglog.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllActiveCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse getCategoryBySlug(String slug);
    CategoryResponse createCategory(CategoryDto dto);
    CategoryResponse updateCategory(Long id, CategoryDto dto);
    void deleteCategory(Long id);
}
