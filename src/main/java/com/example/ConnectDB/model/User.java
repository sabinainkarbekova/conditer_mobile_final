// src/main/java/com/example/ConnectDB/model/User.java
package com.example.ConnectDB.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Необязательный номер телефона
    @Column(unique = true)
    private String phoneNumber;

    // Необязательная почта
    @Column(unique = true)
    private String email;

    // Роль пользователя
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // По умолчанию USER

    // Выбранный пункт самовывоза
    @Column(name = "selected_pickup_address_id")
    private Long selectedPickupAddressId;

    // Связь с картами
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<PaymentCard> paymentCards = new ArrayList<>();

    // ---------- Геттеры и сеттеры ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getSelectedPickupAddressId() { return selectedPickupAddressId; }
    public void setSelectedPickupAddressId(Long selectedPickupAddressId) {
        this.selectedPickupAddressId = selectedPickupAddressId;
    }

    public List<PaymentCard> getPaymentCards() { return paymentCards; }
    public void setPaymentCards(List<PaymentCard> paymentCards) {
        this.paymentCards = paymentCards;
    }

    // ---------- Конструкторы ----------

    public User() {}

    public User(String username, String password, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = Role.USER;
    }
}