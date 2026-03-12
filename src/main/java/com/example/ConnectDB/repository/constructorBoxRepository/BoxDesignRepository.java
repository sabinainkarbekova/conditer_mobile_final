// src/main/java/com/example/ConnectDB/repository/BoxDesignRepository.java
package com.example.ConnectDB.repository.constructorBoxRepository;

import com.example.ConnectDB.model.constructorBox.BoxDesign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoxDesignRepository extends JpaRepository<BoxDesign, Long> {
    List<BoxDesign> findByPremiumLevel(Integer premiumLevel);
}