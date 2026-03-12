package com.example.ConnectDB.service;

import com.example.ConnectDB.model.Ingredient;
import com.example.ConnectDB.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> findById(Long id) {
        return ingredientRepository.findById(id);
    }

    public Ingredient save(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public Ingredient update(Long id, Ingredient ingredientDetails) {
        return ingredientRepository.findById(id)
                .map(ingredient -> {
                    ingredient.setName(ingredientDetails.getName());
                    ingredient.setIsAllergen(ingredientDetails.getIsAllergen());
                    return ingredientRepository.save(ingredient);
                })
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
    }

    public void delete(Long id) {
        ingredientRepository.deleteById(id);
    }

    public List<Ingredient> searchByName(String query) {
        return ingredientRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Ingredient> findAllergens() {
        return ingredientRepository.findByIsAllergenTrue();
    }
}