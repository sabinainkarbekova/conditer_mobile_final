// src/main/java/com/example/ConnectDB/controller/PaymentCardController.java
package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.PaymentCard;
import com.example.ConnectDB.service.PaymentCardService;
import com.example.ConnectDB.config.JwtTokenUtil;
import com.example.ConnectDB.service.AuthService; // Добавь этот импорт
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = {"http://localhost:50782"})
public class PaymentCardController {

    @Autowired
    private PaymentCardService cardService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService; // Добавлено: внедрение AuthService

    // Вспомогательный метод для получения ID пользователя из токена
    private Long getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);
        Long userId = authService.getUserIdByUsername(username);
        if (userId == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        return userId;
    }

    // Метод для получения ID пользователя по имени (нужно добавить в AuthService)
    // private Long getUserIdByUsername(String username) {
    //     // Это заглушка — в реальном проекте нужно получить ID из базы
    //     // Например, через UserService
    //     // Для учебного проекта можно вернуть фиктивный ID, но лучше добавить в AuthService
    //     return 1L; // ❗️ временно, замени на реальный код!
    // }

    // ---------- ДОБАВИТЬ КАРТУ ----------
    @PostMapping("/add")
    public ResponseEntity<?> addCard(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody PaymentCard card) {
        try {
            Long userId = getUserIdFromToken(authHeader); // Получаем ID пользователя
            PaymentCard savedCard = cardService.addCard(userId, card);
            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- ПОЛУЧИТЬ ВСЕ КАРТЫ ПОЛЬЗОВАТЕЛЯ ----------
    @GetMapping("/list")
    public ResponseEntity<List<PaymentCard>> getCards(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            List<PaymentCard> cards = cardService.getCardsByUserId(userId);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // ---------- УДАЛИТЬ КАРТУ ----------
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCard(@RequestHeader("Authorization") String authHeader,
                                                          @PathVariable Long id) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            String message = cardService.deleteCard(id);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- УСТАНОВИТЬ КАРТУ ПО УМОЛЧАНИЮ ----------
    @PutMapping("/{id}/default")
    public ResponseEntity<PaymentCard> setDefaultCard(@RequestHeader("Authorization") String authHeader,
                                                      @PathVariable Long id) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            PaymentCard updatedCard = cardService.setDefaultCard(id, userId);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }
    }
}