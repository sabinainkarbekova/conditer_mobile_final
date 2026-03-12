package com.example.ConnectDB.model.constructorCake;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "custom_cake_decorations")
public class CustomCakeDecoration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "custom_cake_id", nullable = false)
    private CustomCake customCake;

    @ManyToOne
    @JoinColumn(name = "decoration_id", nullable = false)
    private Decoration decoration;

    @Column(name = "pos_x", precision = 6, scale = 2)
    private BigDecimal posX;

    @Column(name = "pos_y", precision = 6, scale = 2)
    private BigDecimal posY;

    @Column(name = "scale", precision = 4, scale = 2)
    private BigDecimal scale = BigDecimal.ONE;

    @Column(name = "rotation", precision = 5, scale = 2)
    private BigDecimal rotation = BigDecimal.ZERO;

    // Конструкторы
    public CustomCakeDecoration() {}

    public CustomCakeDecoration(CustomCake customCake, Decoration decoration,
                                BigDecimal posX, BigDecimal posY,
                                BigDecimal scale, BigDecimal rotation) {
        this.customCake = customCake;
        this.decoration = decoration;
        this.posX = posX;
        this.posY = posY;
        this.scale = scale;
        this.rotation = rotation;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CustomCake getCustomCake() { return customCake; }
    public void setCustomCake(CustomCake customCake) { this.customCake = customCake; }
    public Decoration getDecoration() { return decoration; }
    public void setDecoration(Decoration decoration) { this.decoration = decoration; }
    public BigDecimal getPosX() { return posX; }
    public void setPosX(BigDecimal posX) { this.posX = posX; }
    public BigDecimal getPosY() { return posY; }
    public void setPosY(BigDecimal posY) { this.posY = posY; }
    public BigDecimal getScale() { return scale; }
    public void setScale(BigDecimal scale) { this.scale = scale; }
    public BigDecimal getRotation() { return rotation; }
    public void setRotation(BigDecimal rotation) { this.rotation = rotation; }
}