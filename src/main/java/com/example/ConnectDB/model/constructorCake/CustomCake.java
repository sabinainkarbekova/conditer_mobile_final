package com.example.ConnectDB.model.constructorCake;

import com.example.ConnectDB.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_cakes")
public class CustomCake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "cake_type_id")
    private CakeType cakeType;

    @ManyToOne
    @JoinColumn(name = "layer_id")
    private CakeLayer layer;

    @ManyToOne
    @JoinColumn(name = "cream_id")
    private CakeCream cream;

    @ManyToOne
    @JoinColumn(name = "filling_id")
    private CakeFilling filling;

    @ManyToOne
    @JoinColumn(name = "coating_id")
    private CakeCoating coating;

    @ManyToOne
    @JoinColumn(name = "coating_color_id")
    private CoatingColor coatingColor;

    @ManyToOne
    @JoinColumn(name = "text_id")
    private CakeText text;

    @Column(name = "comment")
    private String comment;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customCake", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomCakeDecoration> decorations = new ArrayList<>();

    @OneToMany(mappedBy = "customCake", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomCakeCandle> candles = new ArrayList<>();

    // Конструкторы
    public CustomCake() {
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CakeType getCakeType() { return cakeType; }
    public void setCakeType(CakeType cakeType) { this.cakeType = cakeType; }
    public CakeLayer getLayer() { return layer; }
    public void setLayer(CakeLayer layer) { this.layer = layer; }
    public CakeCream getCream() { return cream; }
    public void setCream(CakeCream cream) { this.cream = cream; }
    public CakeFilling getFilling() { return filling; }
    public void setFilling(CakeFilling filling) { this.filling = filling; }
    public CakeCoating getCoating() { return coating; }
    public void setCoating(CakeCoating coating) { this.coating = coating; }
    public CoatingColor getCoatingColor() { return coatingColor; }
    public void setCoatingColor(CoatingColor coatingColor) { this.coatingColor = coatingColor; }
    public CakeText getText() { return text; }
    public void setText(CakeText text) { this.text = text; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<CustomCakeDecoration> getDecorations() { return decorations; }
    public void setDecorations(List<CustomCakeDecoration> decorations) { this.decorations = decorations; }
    public List<CustomCakeCandle> getCandles() { return candles; }
    public void setCandles(List<CustomCakeCandle> candles) { this.candles = candles; }
}