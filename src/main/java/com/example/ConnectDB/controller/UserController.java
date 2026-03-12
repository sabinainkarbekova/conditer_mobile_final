package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.User;
import com.example.ConnectDB.repository.UserRepository;
import com.example.ConnectDB.config.JwtTokenUtil;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:50782"})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Получение пользователя из JWT
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Нет токена!");
        }
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));
    }

    // ---------- Получение профиля ----------
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Нет токена!");
            }

            String token = authHeader.substring(7);
            String username = jwtTokenUtil.extractUsername(token);

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body("Пользователь не найден!");
            }

            User u = user.get();
            return ResponseEntity.ok(new ProfileResponse(u.getId(), u.getUsername(), u.getEmail(), u.getPhoneNumber()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Неверный токен!");
        }
    }

    // ---------- Обновление профиля ----------
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileRequest req) {
        try {
            User user = getUserFromToken(authHeader);

            if (req.getUsername() != null && !req.getUsername().isBlank()) user.setUsername(req.getUsername());
            if (req.getEmail() != null) user.setEmail(req.getEmail());
            if (req.getPhoneNumber() != null) user.setPhoneNumber(req.getPhoneNumber());

            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Профиль обновлён!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- Смена пароля ----------
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest req) {
        try {
            User user = getUserFromToken(authHeader);

            if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "❌ Старый пароль неверный"));
            }

            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "✅ Пароль успешно изменён"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- Генерация QR-кода ----------
    @GetMapping("/qrcode")
    public ResponseEntity<byte[]> getQrCode(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = getUserFromToken(authHeader);
            String qrData = "USER_ID:" + user.getId();
            byte[] qrImage = generateQRCodeImage(qrData, 200, 200);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrImage.length);

            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 🔹 Генерация QR-кода
    private byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    // ---------- DTO ----------
    static class ProfileResponse {
        public Long id;
        public String username;
        public String email;
        public String phoneNumber;

        public ProfileResponse(Long id, String username, String email, String phoneNumber) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }
    }

    static class UpdateProfileRequest {
        private String username;
        private String email;
        private String phoneNumber;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}