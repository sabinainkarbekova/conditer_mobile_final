// src/main/java/com/example/ConnectDB/repository/UserRepository.java
package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.User;
import com.example.ConnectDB.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Новый метод для поиска пользователей по роли
    List<User> findByRole(Role role);

    long countByRole(Role role);
}
