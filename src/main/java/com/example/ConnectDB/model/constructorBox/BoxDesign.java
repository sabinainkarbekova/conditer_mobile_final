// src/main/java/com/example/ConnectDB/model/BoxDesign.java
package com.example.ConnectDB.model.constructorBox;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "box_designs")
public class BoxDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String colorCode;

    @Column(nullable = false)
    private Integer premiumLevel;
    private String imageAssetPath;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Integer getPremiumLevel() {
        return premiumLevel;
    }

    public void setPremiumLevel(Integer premiumLevel) {
        this.premiumLevel = premiumLevel;
    }

    // Обновлённый геттер и сеттер для imageAssetPath
    public String getImageAssetPath() {
        return imageAssetPath;
    }

    public void setImageAssetPath(String imageAssetPath) {
        this.imageAssetPath = imageAssetPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}