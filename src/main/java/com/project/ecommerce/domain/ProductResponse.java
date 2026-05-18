package com.project.ecommerce.domain;
import java.util.List;

// --- DTO for DummyJSON Response using Java Records ---
public record ProductResponse(List<ProductDto> products) {
}
