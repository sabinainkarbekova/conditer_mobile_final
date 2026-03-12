package com.example.ConnectDB.model;

import com.example.ConnectDB.model.constructorCake.CustomCake;
import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data // <--- ДОБАВЬ ЭТУ АННОТАЦИЮ
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private com.example.ConnectDB.model.Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_cake_id")
    private CustomCake customCake;

    private Integer quantity = 1;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;
}
