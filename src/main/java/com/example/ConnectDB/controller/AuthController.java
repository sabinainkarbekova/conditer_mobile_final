package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.User;
import com.example.ConnectDB.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ---------- РЕГИСТРАЦИЯ ----------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Вызываем метод, который возвращает Map
            Map<String, Object> response = authService.register(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- ЛОГИН ----------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            // Вызываем метод, который возвращает Map
            Map<String, Object> response = authService.login(user.getUsername(), user.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- ОБНОВЛЕНИЕ ДАННЫХ ----------
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            String message = authService.updateUser(id, user);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}