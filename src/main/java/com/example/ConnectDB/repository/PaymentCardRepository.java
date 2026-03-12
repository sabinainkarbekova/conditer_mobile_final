// src/main/java/com/example/ConnectDB/repository/PaymentCardRepository.java
package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    List<PaymentCard> findByUserId(Long userId);
    List<PaymentCard> findByUserIdAndIsDefaultTrue(Long userId); // Получить дефолтную карту
}