package com.example.ConnectDB.model.constructorCake;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cake_layers")
public class CakeLayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl; // 🆕 добавлено

    @Column(name = "hex_code", nullable = false)
    private String hexCode;  // ← изменилось с imageUrl на hexCode



    // Конструкторы
    public CakeLayer() {}

    public CakeLayer(String name, String description, BigDecimal price, String hexCode) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.hexCode = hexCode;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getHexCode() { return hexCode; }  // ← изменился геттер
    public void setHexCode(String hexCode) { this.hexCode = hexCode; }  // ← изменился сеттер
}