// src/main/java/com/example/ConnectDB/controller/BoxController.java
package com.example.ConnectDB.controller.controllerBox;

import com.example.ConnectDB.model.*;
import com.example.ConnectDB.model.constructorBox.BoxDesign;
import com.example.ConnectDB.model.constructorBox.GreetingCard;
import com.example.ConnectDB.model.constructorBox.UserBox;
import com.example.ConnectDB.repository.*;
import com.example.ConnectDB.config.JwtTokenUtil;
import com.example.ConnectDB.repository.constructorBoxRepository.BoxDesignRepository;
import com.example.ConnectDB.repository.constructorBoxRepository.GreetingCardRepository;
import com.example.ConnectDB.repository.constructorBoxRepository.UserBoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/boxes")
public class BoxController {

    @Autowired
    private UserBoxRepository userBoxRepository;

    @Autowired
    private UserRepository userRepository; // Нужен для поиска пользователя по username

    @Autowired
    private BoxDesignRepository boxDesignRepository;

    @Autowired
    private GreetingCardRepository greetingCardRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil; // Для извлечения username из токена

    // --- Получить все доступные дизайны коробок ---
    @GetMapping("/designs")
    public ResponseEntity<List<BoxDesign>> getAllBoxDesigns() {
        List<BoxDesign> designs = boxDesignRepository.findAll();
        return ResponseEntity.ok(designs);
    }

    // --- Получить все доступные открытки ---
    @GetMapping("/cards")
    public ResponseEntity<List<GreetingCard>> getAllGreetingCards() {
        List<GreetingCard> cards = greetingCardRepository.findAll();
        return ResponseEntity.ok(cards);
    }

    // --- Создать новую коробку для авторизованного пользователя ---
    @PostMapping("/create")
    public ResponseEntity<?> createBox(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody CreateBoxRequest request) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "No token provided!"));
            }

            String token = authHeader.substring(7);
            String username = jwtTokenUtil.extractUsername(token);

            // Находим пользователя по username
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found!"));
            }

            // Находим дизайн и открытку по ID
            Optional<BoxDesign> designOpt = boxDesignRepository.findById(request.getBoxDesignId());
            Optional<GreetingCard> cardOpt = greetingCardRepository.findById(request.getGreetingCardId());

            if (designOpt.isEmpty() || cardOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Design or Card not found!"));
            }

            User user = userOpt.get();
            BoxDesign design = designOpt.get();
            GreetingCard card = cardOpt.get();

            // Создаем новую коробку
            UserBox newBox = new UserBox();
            newBox.setUser(user); // Устанавливаем пользователя
            newBox.setBoxDesign(design);
            newBox.setGreetingCard(card);
            newBox.setCustomMessage(request.getCustomMessage());
            // finalImageUrl можно заполнить позже, после генерации изображения
            // newBox.setFinalImageUrl(...);

            // Сохраняем коробку
            UserBox savedBox = userBoxRepository.save(newBox);

            return ResponseEntity.ok(savedBox);
        } catch (Exception e) {
            // Логируйте ошибку e.printStackTrace(); или используя Logger
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Получить все коробки текущего пользователя ---
    @GetMapping("/my")
    public ResponseEntity<List<UserBox>> getMyBoxes(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }

            String token = authHeader.substring(7);
            String username = jwtTokenUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }

            // Используем объект User для поиска
            List<UserBox> boxes = userBoxRepository.findByUser(userOpt.get());
            return ResponseEntity.ok(boxes);
        } catch (Exception e) {
            // Логируйте ошибку
            return ResponseEntity.badRequest().build();
        }
    }

    // --- DTO для запроса создания коробки ---
    static class CreateBoxRequest {
        private Long boxDesignId;
        private Long greetingCardId;
        private String customMessage;

        // Getters and Setters
        public Long getBoxDesignId() { return boxDesignId; }
        public void setBoxDesignId(Long boxDesignId) { this.boxDesignId = boxDesignId; }

        public Long getGreetingCardId() { return greetingCardId; }
        public void setGreetingCardId(Long greetingCardId) { this.greetingCardId = greetingCardId; }

        public String getCustomMessage() { return customMessage; }
        public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }
    }
}