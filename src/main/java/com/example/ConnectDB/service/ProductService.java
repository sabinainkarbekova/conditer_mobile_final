package com.example.ConnectDB.service;

import com.example.ConnectDB.model.Product;
import com.example.ConnectDB.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setImageUrl(productDetails.getImageUrl());
                    product.setInStock(productDetails.getInStock());
                    product.setCategory(productDetails.getCategory());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.searchProducts(query);
    }

    public List<Product> findWithFilters(Double minPrice, Double maxPrice, Long categoryId, Boolean inStock) {
        return productRepository.findWithFilters(minPrice, maxPrice, categoryId, inStock);
    }

    public List<Product> getRecommendedProducts(List<Long> excludeIds, int limit) {
        // Implementation for product recommendations
        return productRepository.findAll().stream()
                .filter(p -> !excludeIds.contains(p.getId()))
                .limit(limit)
                .toList();
    }
}