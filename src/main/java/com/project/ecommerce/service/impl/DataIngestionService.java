// File: service/impl/DataIngestionService.java
package com.project.ecommerce.service.impl;

import com.project.ecommerce.domain.Product;
import com.project.ecommerce.domain.ProductDocument;
import com.project.ecommerce.domain.ProductDto;
import com.project.ecommerce.domain.ProductResponse;
import com.project.ecommerce.exception.DataIngestionException;
import com.project.ecommerce.repository.elasticsearch.ProductSearchRepository;
import com.project.ecommerce.repository.jpa.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Service responsible for ingesting product data from external API (DummyJSON)
 * into MySQL and Elasticsearch on application startup.
 */
@Service
@RequiredArgsConstructor
public class DataIngestionService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * Initializes data by fetching from external API and populating both databases.
     * Runs once on application startup via @PostConstruct.
     * Prevents duplicates by checking if data already exists.
     * Skips execution during test profile.
     */
    @PostConstruct
    public void initData() {
        // Skip data ingestion during test execution
        if (activeProfile != null && activeProfile.contains("test")) {
            System.out.println("Test profile detected. Skipping data ingestion.");
            return;
        }

        try {
            // Only ingest if DB is empty to prevent duplicates on restarts
            long productCount = productRepository.count();
            if (productCount > 0) {
                System.out.println("Database already contains " + productCount + " products. Skipping data ingestion.");
                return;
            }

            System.out.println("Starting data ingestion from DummyJSON API...");
            WebClient webClient = WebClient.create("https://dummyjson.com");

            ProductResponse response = webClient.get()
                    .uri("/products?limit=100")
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();

            if (response == null || response.products() == null || response.products().isEmpty()) {
                throw new DataIngestionException("No products received from external API");
            }

            int successCount = 0;
            int totalProducts = response.products().size();

            for (ProductDto dto : response.products()) {
                try {
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
                    ProductDocument doc = ProductDocument.builder()
                            .id(String.valueOf(dto.id()))
                            .title(dto.title())
                            .description(dto.description())
                            .price(dto.price())
                            .category(dto.category())
                            .build();

                    productSearchRepository.save(doc);
                    successCount++;
                } catch (Exception e) {
                    System.err.println("Failed to ingest product with id: " + dto.id() + ". Error: " + e.getMessage());
                }
            }

            System.out.println("Data ingestion completed. Successfully ingested " + successCount + "/" + totalProducts 
                    + " products into MySQL and Elasticsearch.");

            if (successCount == 0) {
                throw new DataIngestionException("Failed to ingest any products from external API");
            }

        } catch (WebClientResponseException e) {
            System.err.println("WebClient error during data ingestion. Status: " + e.getStatusCode() 
                   + ", Message: " + e.getMessage());
            throw new DataIngestionException("Failed to fetch data from external API: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error during data ingestion: " + e.getMessage());
            throw new DataIngestionException("Data ingestion failed: " + e.getMessage(), e);
        }
    }
}