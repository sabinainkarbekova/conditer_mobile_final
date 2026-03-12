package com.example.ConnectDB.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ingredients")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @Column(name = "is_allergen")
    private Boolean isAllergen = false;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    @JsonIgnore  // 🔹 чтобы не возвращать бесконечный список продуктов при запросе ингредиентов
    private List<ProductIngredient> productIngredients;
}
