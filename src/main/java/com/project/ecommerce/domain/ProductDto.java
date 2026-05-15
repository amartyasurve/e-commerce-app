package com.project.ecommerce.domain;

public record ProductDto(
        Long id, String title, String description,
        Double price, String category, String brand
) {
}
