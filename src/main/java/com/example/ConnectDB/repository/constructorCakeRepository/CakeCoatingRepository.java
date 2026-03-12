package com.example.ConnectDB.repository.constructorCakeRepository;

import com.example.ConnectDB.model.constructorCake.CakeCoating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CakeCoatingRepository extends JpaRepository<CakeCoating, Long> {
}