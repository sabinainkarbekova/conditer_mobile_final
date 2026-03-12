package com.example.ConnectDB.model.constructorCake;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cake_texts")
public class CakeText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "font_style")
    private String fontStyle;

    @Column(name = "color")
    private String color;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // Конструкторы
    public CakeText() {}

    public CakeText(String text, String fontStyle, String color, BigDecimal price) {
        this.text = text;
        this.fontStyle = fontStyle;
        this.color = color;
        this.price = price;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getFontStyle() { return fontStyle; }
    public void setFontStyle(String fontStyle) { this.fontStyle = fontStyle; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}