// File: service/ProductService.java
package com.project.ecommerce.service;

import com.project.ecommerce.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String query);
    List<String> getAllCategories();
}
