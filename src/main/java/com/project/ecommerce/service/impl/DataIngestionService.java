// File: service/impl/DataIngestionService.java
package com.project.ecommerce.service.impl;

import com.project.ecommerce.domain.Product;
import com.project.ecommerce.domain.ProductDocument;
import com.project.ecommerce.domain.ProductDto;
import com.project.ecommerce.domain.ProductResponse;
import com.project.ecommerce.repository.elasticsearch.ProductSearchRepository;
import com.project.ecommerce.repository.jpa.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class DataIngestionService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    @PostConstruct
    public void initData() {
        // Only ingest if DB is empty to prevent duplicates on restarts
        if (productRepository.count() == 0) {
            WebClient webClient = WebClient.create("https://dummyjson.com");

            ProductResponse response = webClient.get()
                    .uri("/products?limit=100")
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();

            if (response != null && response.products() != null) {
                for (ProductDto dto : response.products()) {
                    // 1. Map & Save to MySQL
                    Product product = new Product();
                    product.setId(dto.id());
                    product.setTitle(dto.title());
                    product.setDescription(dto.description());
                    product.setPrice(dto.price());
                    product.setCategory(dto.category());
                    product.setBrand(dto.brand());

                    productRepository.save(product);

                    // 2. Map & Save to Elasticsearch
                    ProductDocument doc = new ProductDocument();
                    doc.setId(String.valueOf(dto.id()));
                    doc.setTitle(dto.title());
                    doc.setDescription(dto.description());
                    doc.setPrice(dto.price());
                    doc.setCategory(dto.category());

                    productSearchRepository.save(doc);
                }
                System.out.println("Data ingestion to MySQL and Elasticsearch completed successfully.");
            }
        }
    }
}