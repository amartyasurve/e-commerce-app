// File: repository/elasticsearch/ProductSearchRepository.java
package com.project.ecommerce.repository.elasticsearch;

import com.project.ecommerce.domain.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    List<ProductDocument> findByTitleContainingOrDescriptionContaining(String title, String description);
}