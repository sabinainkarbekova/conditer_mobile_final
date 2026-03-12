package com.example.ConnectDB.repository.constructorCakeRepository;

import com.example.ConnectDB.model.constructorCake.CustomCakeDecoration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomCakeDecorationRepository extends JpaRepository<CustomCakeDecoration, Long> {
    List<CustomCakeDecoration> findByCustomCakeId(Long customCakeId);
    void deleteByCustomCakeId(Long customCakeId);
}