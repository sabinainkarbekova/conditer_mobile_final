package com.example.ConnectDB.repository.constructorCakeRepository;

import com.example.ConnectDB.model.constructorCake.CakeText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CakeTextRepository extends JpaRepository<CakeText, Long> {
}