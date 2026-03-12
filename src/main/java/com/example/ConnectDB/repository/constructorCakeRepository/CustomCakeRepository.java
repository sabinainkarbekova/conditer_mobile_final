package com.example.ConnectDB.repository.constructorCakeRepository;

import com.example.ConnectDB.model.constructorCake.CustomCake;
import com.example.ConnectDB.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomCakeRepository extends JpaRepository<CustomCake, Long> {
    List<CustomCake> findByUser(User user);
    List<CustomCake> findByUserId(Long userId);
}