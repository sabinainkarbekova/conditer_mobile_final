package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.Product;
import com.example.ConnectDB.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean inStock) {

        List<Product> products = productService.findWithFilters(minPrice, maxPrice, categoryId, inStock);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProductsSimple() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        List<Product> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.findByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<Product>> getRecommendedProducts(
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(required = false) List<Long> excludeIds) {

        List<Product> products = productService.getRecommendedProducts(
                excludeIds != null ? excludeIds : List.of(), limit);
        return ResponseEntity.ok(products);
    }
}