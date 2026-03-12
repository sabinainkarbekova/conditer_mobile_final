// src/main/java/com/example/ConnectDB/model/PaymentCard.java
package com.example.ConnectDB.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // Обязательно
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardholderName;

    @Column(nullable = false)
    private String cardNumber; // Хранить только последние 4 цифры или токен

    @Column(nullable = false)
    private String expiryDate;

    // Удаляем это поле
    // @Column(nullable = false)
    // private String securityCode;

    @Column(nullable = false)
    private String cardType;

    @Column(nullable = false)
    private boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Обязательно!
    private User user;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    // Удаляем геттер и сеттер для securityCode

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}