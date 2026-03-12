package com.example.ConnectDB.service;

import com.example.ConnectDB.config.JwtTokenUtil;
import com.example.ConnectDB.model.Cart;
import com.example.ConnectDB.model.Role;
import com.example.ConnectDB.model.User;
import com.example.ConnectDB.repository.CartRepository;
import com.example.ConnectDB.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // =========================
    // 🔐 РЕГИСТРАЦИЯ
    // =========================
    public Map<String, Object> register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с такой почтой уже существует");
        }
        if (user.getPhoneNumber() != null && userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Пользователь с таким номером телефона уже существует");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        String token = jwtTokenUtil.generateToken(user.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", savedUser.getId()); // 🔥 Добавлено
        response.put("role", savedUser.getRole().name());

        return response;
    }


    // =========================
    // 🔐 ЛОГИН
    // =========================
    public Map<String, Object> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        String token = jwtTokenUtil.generateToken(username);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId()); // 🔥 Добавлено
        response.put("role", user.getRole().name());

        return response;
    }

    // =========================
    // 👤 ОБНОВЛЕНИЕ ПРОФИЛЯ
    // =========================
    public String updateUser(Long id, User updatedUser) {

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (updatedUser.getUsername() != null) {
            Optional<User> sameName =
                    userRepository.findByUsername(updatedUser.getUsername());

            if (sameName.isPresent() &&
                    !sameName.get().getId().equals(id)) {
                throw new RuntimeException("Имя пользователя уже занято");
            }

            existing.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getEmail() != null) {
            Optional<User> sameEmail =
                    userRepository.findByEmail(updatedUser.getEmail());

            if (sameEmail.isPresent() &&
                    !sameEmail.get().getId().equals(id)) {
                throw new RuntimeException("Email уже используется");
            }

            existing.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getPhoneNumber() != null) {
            Optional<User> samePhone =
                    userRepository.findByPhoneNumber(updatedUser.getPhoneNumber());

            if (samePhone.isPresent() &&
                    !samePhone.get().getId().equals(id)) {
                throw new RuntimeException("Номер телефона уже используется");
            }

            existing.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {
            existing.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword())
            );
        }

        userRepository.save(existing);
        return "Данные пользователя обновлены";
    }

    // =========================
    // 👑 АДМИН: ВСЕ ПОЛЬЗОВАТЕЛИ
    // =========================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================
    // 👑 АДМИН: УДАЛЕНИЕ
    // =========================
    @Transactional
    public String deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Удаляем корзину если есть
        Optional<Cart> cartOpt = cartRepository.findByUserId(user.getId());
        cartOpt.ifPresent(cartRepository::delete);

        userRepository.delete(user);

        return "Пользователь удалён";
    }

    // =========================
    // 👑 АДМИН: СМЕНА РОЛИ
    // =========================
    public String changeUserRole(Long id, Role newRole) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setRole(newRole);
        userRepository.save(user);

        return "Роль пользователя изменена";
    }

    // =========================
    // 🔎 ВСПОМОГАТЕЛЬНЫЕ
    // =========================
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(null);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // =========================
    // 👤 ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ
    // =========================
    public User getCurrentUser() {

        Object principal =
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        String username =
                (principal instanceof UserDetails userDetails)
                        ? userDetails.getUsername()
                        : principal.toString();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    // =========================
    // 🔐 ИЗВЛЕЧЕНИЕ ID ИЗ JWT
    // =========================
    public Long getUserIdFromToken(String token) {

        String username =
                jwtTokenUtil.extractUsername(token.replace("Bearer ", ""));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return user.getId();
    }
}