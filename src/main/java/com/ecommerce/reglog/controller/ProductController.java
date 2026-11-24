package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.ProductFilterRequest;
import com.ecommerce.reglog.dto.response.*;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product Catalog", description = "Endpoints for searching, filtering, and retrieving products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Search & Filter Products", description = "Dynamic multi-filter search with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        ProductFilterRequest req = new ProductFilterRequest();
        req.setQuery(query);
        req.setCategoryId(categoryId);
        req.setCategorySlug(categorySlug);
        req.setMinPrice(minPrice);
        req.setMaxPrice(maxPrice);
        req.setBrands(brands);
        req.setColors(colors);
        req.setSizes(sizes);
        req.setCategoryIds(categoryIds);
        req.setGender(gender);
        req.setSortBy(sortBy);
        req.setPage(page);
        req.setSize(size);

        Long userId = principal != null ? principal.getId() : null;
        Page<ProductSummaryResponse> result = productService.getFilteredProducts(req, userId);

        PageResponse<ProductSummaryResponse> pageResponse = new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );

        return ResponseEntity.ok(ApiResponse.success("Products retrieved", pageResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product Details by ID")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        ProductDetailResponse product = productService.getProductById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Product details retrieved", product));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get Product Details by Slug")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductBySlug(
            @PathVariable String slug,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        ProductDetailResponse product = productService.getProductBySlug(slug, userId);
        return ResponseEntity.ok(ApiResponse.success("Product details retrieved", product));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get Products by Category ID")
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<ProductSummaryResponse> result = productService.getProductsByCategory(categoryId, page, size);
        PageResponse<ProductSummaryResponse> pageResponse = new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
        return ResponseEntity.ok(ApiResponse.success("Category products retrieved", pageResponse));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get Featured Products")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getFeaturedProducts() {
        List<ProductSummaryResponse> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(ApiResponse.success("Featured products retrieved", products));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Get Top Rated Products")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getTopRatedProducts() {
        List<ProductSummaryResponse> products = productService.getTopRatedProducts();
        return ResponseEntity.ok(ApiResponse.success("Top rated products retrieved", products));
    }

    @GetMapping("/filters")
    @Operation(summary = "Get Available Filter Options")
    public ResponseEntity<ApiResponse<FilterOptionsResponse>> getFilterOptions(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId) {
        FilterOptionsResponse options = productService.getFilterOptions(query, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Filter options retrieved", options));
    }
}
