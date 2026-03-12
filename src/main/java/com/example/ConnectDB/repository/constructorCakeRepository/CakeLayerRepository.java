package com.example.ConnectDB.repository.constructorCakeRepository;

import com.example.ConnectDB.model.constructorCake.CakeLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CakeLayerRepository extends JpaRepository<CakeLayer, Long> {
}