package com.example.ConnectDB.repository;

import com.example.ConnectDB.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
    List<Ingredient> findByIsAllergenTrue();
    List<Ingredient> findByNameContainingIgnoreCase(String name);
}