package com.example.ConnectDB.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "product_ingredients") // таблица для связки many-to-many
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductIngredient {

    @EmbeddedId
    private ProductIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"productIngredients", "category"})
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    @JsonIgnoreProperties({"productIngredients"})
    private Ingredient ingredient;

    private String amount;
}

@Embeddable
@Data
class ProductIngredientId implements java.io.Serializable {
    private Long productId;
    private Long ingredientId;
}
