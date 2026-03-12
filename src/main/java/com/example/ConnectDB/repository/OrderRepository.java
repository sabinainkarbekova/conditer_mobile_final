package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByStatus(String status);

    // 🔥 НОВОЕ: Загрузить заказ с товарами (JOIN FETCH предотвращает N+1)
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    // 🔥 Опционально: проверить, что заказ принадлежит пользователю
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id AND o.user.id = :userId")
    Optional<Order> findByIdWithItemsAndUser(@Param("id") Long id, @Param("userId") Long userId);
}