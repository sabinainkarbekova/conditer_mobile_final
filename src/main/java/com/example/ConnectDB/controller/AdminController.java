// src/main/java/com/example/ConnectDB/controller/AdminController.java
package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.User;
import com.example.ConnectDB.model.Role;
import com.example.ConnectDB.repository.CartRepository;
import com.example.ConnectDB.service.AuthService;
import com.example.ConnectDB.config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:50782"})
public class AdminController {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Вспомогательный метод для извлечения пользователя из токена и проверки роли
    private User getUserFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);
        User user = authService.findUserByUsername(username);
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Доступ запрещён. Требуется роль ADMIN.");
        }
        return user;
    }

    // ---------- АДМИН: Получить всех пользователей ----------
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            getUserFromToken(authHeader); // Проверяем, что это админ
            List<User> users = authService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    // ---------- АДМИН: Удалить пользователя ----------
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        try {
            getUserFromToken(authHeader); // Проверяем, что это админ
            String message = authService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- АДМИН: Изменить роль пользователя ----------
    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, String>> changeUserRole(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            getUserFromToken(authHeader); // Проверяем, что это админ
            String newRoleStr = request.get("role");
            Role newRole = Role.valueOf(newRoleStr.toUpperCase());
            String message = authService.changeUserRole(id, newRole);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    // Вспомогательный метод в AuthService для поиска по имени (если его нет, добавь в AuthService)
    // public User findUserByUsername(String username) {
    //     return userRepository.findByUsername(username).orElse(null);
    // }
}