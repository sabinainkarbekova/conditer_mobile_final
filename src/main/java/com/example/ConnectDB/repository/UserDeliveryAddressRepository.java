package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.UserDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserDeliveryAddressRepository extends JpaRepository<UserDeliveryAddress, Long> {
    List<UserDeliveryAddress> findByUserId(Long userId);

    // 🔥 Новый метод с JOIN FETCH
    @Query("SELECT uda FROM UserDeliveryAddress uda JOIN FETCH uda.address WHERE uda.userId = :userId")
    List<UserDeliveryAddress> findByUserIdWithAddress(Long userId);

    @Modifying
    @Query("UPDATE UserDeliveryAddress SET isDefault = false WHERE userId = :userId AND isDefault = true")
    void resetAllToFalseByUserId(Long userId);
}