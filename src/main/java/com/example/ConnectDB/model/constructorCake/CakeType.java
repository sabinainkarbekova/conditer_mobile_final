package com.example.ConnectDB.model.constructorCake;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cake_types")
public class CakeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    // Конструкторы
    public CakeType() {}

    public CakeType(String name, BigDecimal basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
}