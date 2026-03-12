// com.example.ConnectDB.model.PickupPoint.java
package com.example.ConnectDB.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "pickup_points") // Укажите имя таблицы
public class PickupPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Улица обязательна")
    private String street;

    @NotBlank(message = "Город обязателен")
    private String city;

    private String postalCode;

    private String country;

    private boolean active = true; // По умолчанию активен

    // Конструкторы
    public PickupPoint() {}

    public PickupPoint(String street, String city, String postalCode, String country, boolean active) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.active = active;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}