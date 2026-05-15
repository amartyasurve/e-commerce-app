package com.project.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// --- MySQL Entity ---
@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private Long id; // We will retain the ID from DummyJSON
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double price;
    private String category;
    private String brand;
}

