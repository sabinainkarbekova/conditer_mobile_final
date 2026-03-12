package com.example.ConnectDB.model.constructorCake;

import jakarta.persistence.*;

@Entity
@Table(name = "coating_colors")
public class CoatingColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "hex_code", nullable = false)
    private String hexCode;

    // Конструкторы, геттеры и сеттеры
    public CoatingColor() {}

    public CoatingColor(String name, String hexCode) {
        this.name = name;
        this.hexCode = hexCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHexCode() { return hexCode; }
    public void setHexCode(String hexCode) { this.hexCode = hexCode; }
}