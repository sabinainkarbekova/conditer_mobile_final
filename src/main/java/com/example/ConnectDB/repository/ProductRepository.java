package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByInStockTrue();

    @Query("SELECT p FROM Product p WHERE " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:inStock IS NULL OR p.inStock = :inStock)")
    List<Product> findWithFilters(@Param("minPrice") Double minPrice,
                                  @Param("maxPrice") Double maxPrice,
                                  @Param("categoryId") Long categoryId,
                                  @Param("inStock") Boolean inStock);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);
}