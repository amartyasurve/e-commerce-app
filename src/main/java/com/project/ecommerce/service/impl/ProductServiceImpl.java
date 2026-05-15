// File: service/impl/ProductServiceImpl.java
package com.project.ecommerce.service.impl;

import com.project.ecommerce.domain.Product;
import com.project.ecommerce.domain.ProductDocument;
import com.project.ecommerce.repository.elasticsearch.ProductSearchRepository;
import com.project.ecommerce.repository.jpa.ProductRepository;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSearchRepository searchRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    @Override
    public List<Product> searchProducts(String query) {
        // Querying Elasticsearch for full-text search
        List<ProductDocument> docs = searchRepository.findByTitleContainingOrDescriptionContaining(query, query);

        // Map ES Documents back to JPA Entities for uniform API response
        return docs.stream()
                .map(doc -> {
                    Product p = new Product();
                    p.setId(Long.parseLong(doc.getId()));
                    p.setTitle(doc.getTitle());
                    p.setDescription(doc.getDescription());
                    p.setPrice(doc.getPrice());
                    p.setCategory(doc.getCategory());
                    return p;
                })
                .collect(Collectors.toList());
    }
}