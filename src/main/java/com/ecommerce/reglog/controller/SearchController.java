package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
@Tag(name = "Search & Autocomplete", description = "Endpoints for live search suggestions")
public class SearchController {

    private final ProductService productService;

    public SearchController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Live Search Suggestions", description = "Returns brand and product title auto-completions.")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(@RequestParam("q") String query) {
        List<String> suggestions = productService.getSearchSuggestions(query);
        return ResponseEntity.ok(ApiResponse.success("Suggestions retrieved", suggestions));
    }
}
