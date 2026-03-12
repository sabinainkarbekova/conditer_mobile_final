// src/main/java/com/example/ConnectDB/service/PaymentCardService.java
package com.example.ConnectDB.service;

import com.example.ConnectDB.model.PaymentCard;
import com.example.ConnectDB.model.User;
import com.example.ConnectDB.repository.PaymentCardRepository;
import com.example.ConnectDB.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentCardService {

    @Autowired
    private PaymentCardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    // Добавить карту
    public PaymentCard addCard(Long userId, PaymentCard card) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Если это первая карта — сделать её по умолчанию
        if (cardRepository.findByUserId(userId).isEmpty()) {
            card.setDefault(true);
        } else {
            // Если есть другие карты — проверить, не делаем ли эту новую по умолчанию
            if (card.isDefault()) {
                // Сбросить default со всех других карт
                cardRepository.findByUserId(userId).forEach(c -> {
                    c.setDefault(false);
                    cardRepository.save(c);
                });
            }
        }

        card.setUser(user);
        return cardRepository.save(card);
    }

    // Получить все карты пользователя
    public List<PaymentCard> getCardsByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    // Получить дефолтную карту
    public PaymentCard getDefaultCardByUserId(Long userId) {
        return cardRepository.findByUserIdAndIsDefaultTrue(userId)
                .stream().findFirst()
                .orElse(null);
    }

    // Удалить карту
    public String deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new RuntimeException("Карта не найдена");
        }
        cardRepository.deleteById(cardId);
        return "Карта удалена";
    }

    // Установить карту по умолчанию
    public PaymentCard setDefaultCard(Long cardId, Long userId) {
        PaymentCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("Эта карта не принадлежит пользователю");
        }

        // Сбросить default со всех других карт
        cardRepository.findByUserId(userId).forEach(c -> {
            c.setDefault(false);
            cardRepository.save(c);
        });

        // Установить эту карту как default
        card.setDefault(true);
        return cardRepository.save(card);
    }
}