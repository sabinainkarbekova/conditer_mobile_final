// src/main/java/com/example/ConnectDB/repository/GreetingCardRepository.java
package com.example.ConnectDB.repository.constructorBoxRepository;

import com.example.ConnectDB.model.constructorBox.GreetingCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreetingCardRepository extends JpaRepository<GreetingCard, Long> {
}