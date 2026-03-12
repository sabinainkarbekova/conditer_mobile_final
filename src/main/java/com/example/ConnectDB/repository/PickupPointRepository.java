// com.example.ConnectDB.repository.PickupPointRepository.java
package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Long> {
}