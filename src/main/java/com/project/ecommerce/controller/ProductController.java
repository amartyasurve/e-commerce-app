// File: controller/ProductController.java
package com.project.ecommerce.controller;

import com.project.ecommerce.domain.Product;
import com.project.ecommerce.exception.ProductNotFoundException;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product-related endpoints.
 * Provides API for browsing products, filtering by category, and full-text search.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * Get all unique product categories.
     * @return ResponseEntity with list of category names
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    /**
     * Get products with optional filtering by category or full-text search.
     * Priority: search query > category filter > all products
     * 
     * @param category Optional category filter
     * @param query Optional full-text search query
     * @return ResponseEntity with list of products
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {

        if (query != null && !query.isBlank()) {
            return ResponseEntity.ok(productService.searchProducts(query));
        }

        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(productService.getProductsByCategory(category));
        }

        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Get a specific product by ID.
     * 
     * @param id Product ID
     * @return ResponseEntity with the product or 404 if not found
     * @throws ProductNotFoundException if product not found
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product id must be a positive number");
        }

        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}