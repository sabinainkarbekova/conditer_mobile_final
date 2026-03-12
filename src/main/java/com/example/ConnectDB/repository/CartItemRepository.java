package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    Optional<CartItem> findByCartIdAndCustomCakeId(Long cartId, Long customCakeId);
    void deleteByCartId(Long cartId);

}