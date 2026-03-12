// src/main/java/com/example/ConnectDB/repository/UserBoxRepository.java
package com.example.ConnectDB.repository.constructorBoxRepository;

import com.example.ConnectDB.model.User;
import com.example.ConnectDB.model.constructorBox.UserBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBoxRepository extends JpaRepository<UserBox, Long> {
    // Найти все коробки, связанные с конкретным пользователем
    List<UserBox> findByUser(User user);

    // Альтернатива: найти по ID пользователя (если удобнее)
    // List<UserBox> findByUserId(Long userId);
}