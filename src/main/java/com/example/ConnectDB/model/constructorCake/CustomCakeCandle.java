package com.example.ConnectDB.model.constructorCake;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_cake_candles")
public class CustomCakeCandle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "custom_cake_id", nullable = false)
    private CustomCake customCake;

    @ManyToOne
    @JoinColumn(name = "candle_id", nullable = false)
    private Candle candle;

    @Column(name = "quantity")
    private Integer quantity = 1;

    // Конструкторы
    public CustomCakeCandle() {}

    public CustomCakeCandle(CustomCake customCake, Candle candle, Integer quantity) {
        this.customCake = customCake;
        this.candle = candle;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CustomCake getCustomCake() { return customCake; }
    public void setCustomCake(CustomCake customCake) { this.customCake = customCake; }
    public Candle getCandle() { return candle; }
    public void setCandle(Candle candle) { this.candle = candle; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}