// File: service/impl/ProductServiceImpl.java
package com.project.ecommerce.service.impl;

import com.project.ecommerce.domain.Product;
import com.project.ecommerce.domain.ProductDocument;
import com.project.ecommerce.exception.InvalidSearchException;
import com.project.ecommerce.repository.elasticsearch.ProductSearchRepository;
import com.project.ecommerce.repository.jpa.ProductRepository;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService.
 * Handles business logic for product operations across MySQL and Elasticsearch.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository searchRepository;

    @Override
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve products: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product id must be a positive number");
        }
        
        try {
            return productRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve product: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new InvalidSearchException("Category cannot be empty or null");
        }

        try {
            return productRepository.findByCategory(category.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve products by category: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAllCategories() {
        try {
            return productRepository.findAllCategories();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve categories: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> searchProducts(String query) {
        if (query == null || query.isBlank()) {
            throw new InvalidSearchException("Search query cannot be empty or null");
        }

        try {
            // Querying Elasticsearch for full-text search
            List<ProductDocument> docs = searchRepository
                    .findByTitleContainingOrDescriptionContaining(query.trim(), query.trim());

            // Map ES Documents back to JPA Entities for uniform API response
            return docs.stream()
                    .map(doc -> {
                        Product p = new Product();
                        try {
                            p.setId(Long.parseLong(doc.getId()));
                        } catch (NumberFormatException e) {
                            return null;
                        }
                        p.setTitle(doc.getTitle());
                        p.setDescription(doc.getDescription());
                        p.setPrice(doc.getPrice());
                        p.setCategory(doc.getCategory());
                        return p;
                    })
                    .filter(product -> product != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }
}