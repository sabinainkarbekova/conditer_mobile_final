package com.example.ConnectDB.repository.constructorCakeRepository;

import com.example.ConnectDB.model.constructorCake.CustomCakeCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomCakeCandleRepository extends JpaRepository<CustomCakeCandle, Long> {
    List<CustomCakeCandle> findByCustomCakeId(Long customCakeId);
    void deleteByCustomCakeId(Long customCakeId);
}