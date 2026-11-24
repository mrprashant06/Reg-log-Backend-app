package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.CategoryDto;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.CategoryResponse;
import com.ecommerce.reglog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Category Management", description = "Endpoints for retrieving and administering product categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Get All Categories", description = "Retrieves all active categories ordered by display order.")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "Get Category by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @GetMapping("/categories/slug/{slug}")
    @Operation(summary = "Get Category by Slug")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        CategoryResponse category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Category (Admin)")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryDto dto) {
        CategoryResponse created = categoryService.createCategory(dto);
        return new ResponseEntity<>(ApiResponse.success("Category created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Category (Admin)")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto dto) {
        CategoryResponse updated = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate Category (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deactivated successfully"));
    }
}
