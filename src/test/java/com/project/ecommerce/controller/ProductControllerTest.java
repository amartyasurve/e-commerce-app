package com.project.ecommerce.controller;

import com.project.ecommerce.domain.Product;
import com.project.ecommerce.exception.InvalidSearchException;
import com.project.ecommerce.exception.ProductNotFoundException;
import com.project.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductController.
 * Tests all REST endpoints with mocked service layer.
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setCategory("Electronics");
        testProduct.setBrand("TestBrand");
    }

    // ==================== GET /categories Tests ====================

    @Test
    void testGetAllCategories_Success() {
        // Arrange
        List<String> expectedCategories = Arrays.asList("Electronics", "Fashion", "Books");
        when(productService.getAllCategories()).thenReturn(expectedCategories);

        // Act
        ResponseEntity<List<String>> response = productController.getAllCategories();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        verify(productService, times(1)).getAllCategories();
    }

    @Test
    void testGetAllCategories_EmptyList() {
        // Arrange
        when(productService.getAllCategories()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<String>> response = productController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ==================== GET /products Tests ====================

    @Test
    void testGetProducts_AllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(expectedProducts);

        // Act
        ResponseEntity<List<Product>> response = productController.getProducts(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getAllProducts();
        verify(productService, never()).searchProducts(anyString());
        verify(productService, never()).getProductsByCategory(anyString());
    }

    @Test
    void testGetProducts_WithSearchQuery() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productService.searchProducts("laptop")).thenReturn(expectedProducts);

        // Act
        ResponseEntity<List<Product>> response = productController.getProducts(null, "laptop");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).searchProducts("laptop");
        verify(productService, never()).getAllProducts();
    }

    @Test
    void testGetProducts_WithCategory() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productService.getProductsByCategory("Electronics")).thenReturn(expectedProducts);

        // Act
        ResponseEntity<List<Product>> response = productController.getProducts("Electronics", null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getProductsByCategory("Electronics");
        verify(productService, never()).getAllProducts();
    }

    @Test
    void testGetProducts_QueryPriority() {
        // Arrange - When both query and category are provided, query should take priority
        List<Product> searchResults = Arrays.asList(testProduct);
        when(productService.searchProducts("laptop")).thenReturn(searchResults);

        // Act
        ResponseEntity<List<Product>> response = productController.getProducts("Electronics", "laptop");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).searchProducts("laptop");
        verify(productService, never()).getProductsByCategory(anyString());
    }

    @Test
    void testGetProducts_EmptySearchQuery_FallsBackToAllProducts() {
        // Arrange
        List<Product> allProducts = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(allProducts);

        // Act
        ResponseEntity<List<Product>> response = productController.getProducts(null, "");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProducts_BlankSearchQuery_FallsBackToAllProducts() {
        // Arrange
        List<Product> allProducts = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(allProducts);

        // Act
        ResponseEntity<List<Product>> response = productController.getProducts(null, "   ");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProducts_SearchThrowsException() {
        // Arrange
        when(productService.searchProducts("invalid"))
                .thenThrow(new InvalidSearchException("Invalid query"));

        // Act & Assert
        assertThrows(InvalidSearchException.class, 
                () -> productController.getProducts(null, "invalid"));
    }

    // ==================== GET /products/{id} Tests ====================

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        ResponseEntity<Product> response = productController.getProductById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getTitle());
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, 
                () -> productController.getProductById(999L));
        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void testGetProductById_InvalidId_Null() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> productController.getProductById(null));
        assertEquals("Product id must be a positive number", thrown.getMessage());
    }

    @Test
    void testGetProductById_InvalidId_Zero() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> productController.getProductById(0L));
        assertEquals("Product id must be a positive number", thrown.getMessage());
    }

    @Test
    void testGetProductById_InvalidId_Negative() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> productController.getProductById(-5L));
        assertEquals("Product id must be a positive number", thrown.getMessage());
    }

    @Test
    void testGetProductById_ServiceException() {
        // Arrange
        when(productService.getProductById(1L))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, 
                () -> productController.getProductById(1L));
    }
}

