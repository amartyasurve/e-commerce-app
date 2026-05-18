package com.project.ecommerce.service.impl;

import com.project.ecommerce.domain.Product;
import com.project.ecommerce.domain.ProductDocument;
import com.project.ecommerce.exception.InvalidSearchException;
import com.project.ecommerce.repository.elasticsearch.ProductSearchRepository;
import com.project.ecommerce.repository.jpa.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductServiceImpl.
 * Tests all service methods with mocked repositories.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchRepository searchRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductDocument testDocument;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setCategory("Electronics");
        testProduct.setBrand("TestBrand");

        testDocument = ProductDocument.builder()
                .id("1")
                .title("Test Product")
                .description("Test Description")
                .price(99.99)
                .category("Electronics")
                .build();
    }

    // ==================== getAllProducts Tests ====================

    @Test
    void testGetAllProducts_Success() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getTitle());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_Exception() {
        // Arrange
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, 
                () -> productService.getAllProducts());
        assertTrue(thrown.getMessage().contains("Failed to retrieve products"));
    }

    // ==================== getProductById Tests ====================

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testGetProductById_InvalidId_Null() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> productService.getProductById(null));
        assertEquals("Product id must be a positive number", thrown.getMessage());
    }

    @Test
    void testGetProductById_InvalidId_Zero() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> productService.getProductById(0L));
        assertEquals("Product id must be a positive number", thrown.getMessage());
    }

    @Test
    void testGetProductById_InvalidId_Negative() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> productService.getProductById(-1L));
        assertEquals("Product id must be a positive number", thrown.getMessage());
    }

    // ==================== getProductsByCategory Tests ====================

    @Test
    void testGetProductsByCategory_Success() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByCategory("Electronics")).thenReturn(expectedProducts);

        // Act
        List<Product> result = productService.getProductsByCategory("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategory());
        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    void testGetProductsByCategory_NoResults() {
        // Arrange
        when(productRepository.findByCategory("NonExistent")).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productService.getProductsByCategory("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductsByCategory_NullCategory() {
        // Act & Assert
        InvalidSearchException thrown = assertThrows(InvalidSearchException.class,
                () -> productService.getProductsByCategory(null));
        assertEquals("Category cannot be empty or null", thrown.getMessage());
    }

    @Test
    void testGetProductsByCategory_EmptyCategory() {
        // Act & Assert
        InvalidSearchException thrown = assertThrows(InvalidSearchException.class,
                () -> productService.getProductsByCategory(""));
        assertEquals("Category cannot be empty or null", thrown.getMessage());
    }

    @Test
    void testGetProductsByCategory_BlankCategory() {
        // Act & Assert
        InvalidSearchException thrown = assertThrows(InvalidSearchException.class,
                () -> productService.getProductsByCategory("   "));
        assertTrue(thrown.getMessage().contains("empty or null"));
    }

    @Test
    void testGetProductsByCategory_WithWhitespace() {
        // Arrange
        when(productRepository.findByCategory("Electronics")).thenReturn(Arrays.asList(testProduct));

        // Act
        List<Product> result = productService.getProductsByCategory("  Electronics  ");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    // ==================== getAllCategories Tests ====================

    @Test
    void testGetAllCategories_Success() {
        // Arrange
        List<String> expectedCategories = Arrays.asList("Electronics", "Fashion", "Books");
        when(productRepository.findAllCategories()).thenReturn(expectedCategories);

        // Act
        List<String> result = productService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Electronics"));
        verify(productRepository, times(1)).findAllCategories();
    }

    @Test
    void testGetAllCategories_Empty() {
        // Arrange
        when(productRepository.findAllCategories()).thenReturn(Collections.emptyList());

        // Act
        List<String> result = productService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== searchProducts Tests ====================

    @Test
    void testSearchProducts_Success() {
        // Arrange
        List<ProductDocument> documents = Arrays.asList(testDocument);
        when(searchRepository.findByTitleContainingOrDescriptionContaining(
                "Test", "Test")).thenReturn(documents);

        // Act
        List<Product> result = productService.searchProducts("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getTitle());
        verify(searchRepository, times(1))
                .findByTitleContainingOrDescriptionContaining("Test", "Test");
    }

    @Test
    void testSearchProducts_NoResults() {
        // Arrange
        when(searchRepository.findByTitleContainingOrDescriptionContaining(
                "NonExistent", "NonExistent")).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productService.searchProducts("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchProducts_NullQuery() {
        // Act & Assert
        InvalidSearchException thrown = assertThrows(InvalidSearchException.class,
                () -> productService.searchProducts(null));
        assertEquals("Search query cannot be empty or null", thrown.getMessage());
    }

    @Test
    void testSearchProducts_EmptyQuery() {
        // Act & Assert
        InvalidSearchException thrown = assertThrows(InvalidSearchException.class,
                () -> productService.searchProducts(""));
        assertEquals("Search query cannot be empty or null", thrown.getMessage());
    }

    @Test
    void testSearchProducts_BlankQuery() {
        // Act & Assert
        InvalidSearchException thrown = assertThrows(InvalidSearchException.class,
                () -> productService.searchProducts("   "));
        assertTrue(thrown.getMessage().contains("empty or null"));
    }

    @Test
    void testSearchProducts_WithWhitespace() {
        // Arrange
        when(searchRepository.findByTitleContainingOrDescriptionContaining(
                "laptop", "laptop")).thenReturn(Arrays.asList(testDocument));

        // Act
        List<Product> result = productService.searchProducts("  laptop  ");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(searchRepository, times(1))
                .findByTitleContainingOrDescriptionContaining("laptop", "laptop");
    }

    @Test
    void testSearchProducts_MultipleResults() {
        // Arrange
        ProductDocument doc2 = ProductDocument.builder()
                .id("2")
                .title("Another Test Product")
                .description("Another Test Description")
                .price(149.99)
                .category("Electronics")
                .build();

        List<ProductDocument> documents = Arrays.asList(testDocument, doc2);
        when(searchRepository.findByTitleContainingOrDescriptionContaining(
                "Test", "Test")).thenReturn(documents);

        // Act
        List<Product> result = productService.searchProducts("Test");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchProducts_InvalidDocumentId() {
        // Arrange
        ProductDocument invalidDoc = ProductDocument.builder()
                .id("invalid-id")
                .title("Invalid Product")
                .description("Invalid Description")
                .price(99.99)
                .category("Electronics")
                .build();

        when(searchRepository.findByTitleContainingOrDescriptionContaining(
                "Test", "Test")).thenReturn(Arrays.asList(invalidDoc));

        // Act
        List<Product> result = productService.searchProducts("Test");

        // Assert
        assertNotNull(result);
        // Should filter out invalid products
        assertEquals(0, result.size());
    }
}

