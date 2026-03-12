// UserDeliveryAddress.java
package com.example.ConnectDB.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_delivery_addresses")
public class UserDeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false; // 🔥 Вот это поле

    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}