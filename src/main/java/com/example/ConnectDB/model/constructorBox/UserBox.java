// src/main/java/com/example/ConnectDB/model/UserBox.java
package com.example.ConnectDB.model.constructorBox;

import com.example.ConnectDB.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_boxes")
public class UserBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Объект User в Java

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_design_id", nullable = false)
    private BoxDesign boxDesign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "greeting_card_id", nullable = false)
    private GreetingCard greetingCard;

    @Column(length = 1000)
    private String customMessage;

    @Column(length = 2048)
    private String finalImageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UserBox() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BoxDesign getBoxDesign() {
        return boxDesign;
    }

    public void setBoxDesign(BoxDesign boxDesign) {
        this.boxDesign = boxDesign;
    }

    public GreetingCard getGreetingCard() {
        return greetingCard;
    }

    public void setGreetingCard(GreetingCard greetingCard) {
        this.greetingCard = greetingCard;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getFinalImageUrl() {
        return finalImageUrl;
    }

    public void setFinalImageUrl(String finalImageUrl) {
        this.finalImageUrl = finalImageUrl;
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