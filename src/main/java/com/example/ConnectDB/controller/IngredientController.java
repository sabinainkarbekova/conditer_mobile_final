package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.Ingredient;
import com.example.ConnectDB.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.findAll();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        return ingredientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
        Ingredient savedIngredient = ingredientService.save(ingredient);
        return ResponseEntity.ok(savedIngredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredientDetails) {
        try {
            Ingredient updatedIngredient = ingredientService.update(id, ingredientDetails);
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        try {
            ingredientService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ingredient>> searchIngredients(@RequestParam String query) {
        List<Ingredient> ingredients = ingredientService.searchByName(query);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/allergens")
    public ResponseEntity<List<Ingredient>> getAllergens() {
        List<Ingredient> allergens = ingredientService.findAllergens();
        return ResponseEntity.ok(allergens);
    }
}