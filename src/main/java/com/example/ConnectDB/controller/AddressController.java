// src/main/java/com/example/ConnectDB/controller/AddressController.java
package com.example.ConnectDB.controller;

import java.util.stream.Collectors;
import com.example.ConnectDB.model.Address;
import com.example.ConnectDB.model.User;
import com.example.ConnectDB.model.UserDeliveryAddress;
import com.example.ConnectDB.model.Role; // 🔹 Импортируем Role
import com.example.ConnectDB.repository.AddressRepository;
import com.example.ConnectDB.repository.UserDeliveryAddressRepository;
import com.example.ConnectDB.repository.UserRepository;
import com.example.ConnectDB.config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // 🔥 Добавлено
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = {"http://localhost:50782"})
public class AddressController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository; // 🔥 Добавлено

    @Autowired
    private UserDeliveryAddressRepository userDeliveryAddressRepository; // 🔥 Добавлено

        // 🔥 DTO для объединения данных
        // 🔥 DTO для объединения данных
        public static class AddressResponse {
            private final Long id;
            private final String street;
            private final String city;
            private final String country;
            private final String postalCode;
            private final String type; // "DELIVERY" или "PICKUP"
            private final Boolean isDefault; // только для DELIVERY

            public AddressResponse(Long id, String street, String city, String country, String postalCode, String type, Boolean isDefault) {
                this.id = id;
                this.street = street;
                this.city = city;
                this.country = country;
                this.postalCode = postalCode;
                this.type = type;
                this.isDefault = isDefault;
            }

            // Getters
            public Long getId() { return id; }
            public String getStreet() { return street; }
            public String getCity() { return city; }
            public String getCountry() { return country; }
            public String getPostalCode() { return postalCode; }
            public String getType() { return type; }
            public Boolean getIsDefault() { return isDefault; }
        }
    // --- Admin Endpoints ---
    @PostMapping("/admin/pickup") // Создать точку самовывоза
    public ResponseEntity<Address> createPickupPoint(@RequestHeader("Authorization") String authHeader, @RequestBody Address address) {
        validateAdminAccess(authHeader); // 🔹 Проверяем роль админа

        Address newAddress = new Address();
        newAddress.setAddressType(Address.AddressType.PICKUP);
        newAddress.setStreet(address.getStreet());
        newAddress.setCity(address.getCity());
        newAddress.setPostalCode(address.getPostalCode());
        newAddress.setCountry(address.getCountry());
        newAddress.setActive(true);

        Address savedAddress = addressRepository.save(newAddress);
        return ResponseEntity.ok(savedAddress);
    }

    // src/main/java/com/example/ConnectDB/controller/AddressController.java
// ...
    // src/main/java/com/example/ConnectDB/controller/AddressController.java
// ...
    @PutMapping("/admin/pickup/{id}") // Обновить точку самовывоза (включая статус isActive)
    public ResponseEntity<Address> updatePickupPoint(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Address addressDetails) {
        validateAdminAccess(authHeader); // Проверяем роль админа

        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Адрес не найден"));

        if (!existingAddress.getAddressType().equals(Address.AddressType.PICKUP)) {
            throw new RuntimeException("Можно обновлять только точки самовывоза");
        }

        // Обновляем основные поля из тела запроса
        existingAddress.setStreet(addressDetails.getStreet());
        existingAddress.setCity(addressDetails.getCity());
        existingAddress.setPostalCode(addressDetails.getPostalCode());
        existingAddress.setCountry(addressDetails.getCountry());

        // --- Вот эта часть была обновлена ---
        // Обновляем статус активности, если он указан в запросе
        if (addressDetails.isActive() != null) { // <--- ИСПРАВЛЕНО: isActive() вместо getActive()
            existingAddress.setActive(addressDetails.isActive()); // <--- ИСПРАВЛЕНО: isActive() вместо setActive(getActive())
        }
        // --- Конец обновленной части ---

        Address updatedAddress = addressRepository.save(existingAddress);
        return ResponseEntity.ok(updatedAddress);
    }
// ...

    @DeleteMapping("/admin/pickup/{id}") // Удалить точку самовывоза
    public ResponseEntity<String> deletePickupPoint(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        validateAdminAccess(authHeader); // 🔹 Проверяем роль админа

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Адрес не найден"));

        if (address.getAddressType().equals(Address.AddressType.PICKUP)) {
            address.setActive(false);
            addressRepository.save(address);
        } else {
            throw new RuntimeException("Можно удалять только точки самовывоза");
        }

        return ResponseEntity.ok("Точка самовывоза успешно удалена (деактивирована)");
    }

    // 🔥 Новый эндпоинт: получить ВСЕ точки самовывоза (активные и неактивные)
// 🔥 Новый эндпоинт: получить ВСЕ точки самовывоза (активные и неактивные)
// 🔥 Новый эндпоинт: получить ВСЕ точки самовывоза (активные и неактивные)
    @GetMapping("/admin/pickup/all")
    public ResponseEntity<List<Address>> getAllPickupPoints(@RequestHeader("Authorization") String authHeader) {
        validateAdminAccess(authHeader);

        List<Address> addresses = addressRepository.findByAddressType(Address.AddressType.PICKUP);
        return ResponseEntity.ok(addresses);
    }

    // --- User Endpoints ---
    @GetMapping("/public/pickup") // Получить доступные точки самовывоза
    public ResponseEntity<List<Address>> getPickupPoints() {
        // 🔹 Этот эндпоинт публичный, не требует авторизации
        List<Address> addresses = addressRepository.findByAddressTypeAndIsActiveTrue(Address.AddressType.PICKUP);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/user/delivery") // Получить мои адреса доставки
    public ResponseEntity<List<UserDeliveryAddress>> getUserDeliveryAddresses(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        List<UserDeliveryAddress> addresses = userDeliveryAddressRepository.findByUserIdWithAddress(userId); // ✅
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/user/delivery/add") // Добавить адрес доставки
    public ResponseEntity<UserDeliveryAddress> addUserDeliveryAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long addressId) {
        Long userId = extractUserIdFromToken(authHeader);

        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getAddressType() == Address.AddressType.DELIVERY && a.isActive())
                .orElseThrow(() -> new RuntimeException("Неверный адрес доставки"));

        UserDeliveryAddress userAddr = new UserDeliveryAddress();
        userAddr.setUserId(userId);
        userAddr.setAddress(address);
        userAddr.setIsDefault(false);

        UserDeliveryAddress saved = userDeliveryAddressRepository.save(userAddr);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/user/delivery/{id}") // Удалить адрес доставки
    public ResponseEntity<String> removeUserDeliveryAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        Long userId = extractUserIdFromToken(authHeader);

        UserDeliveryAddress addr = userDeliveryAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Адрес не найден"));

        if (!addr.getUserId().equals(userId)) {
            throw new RuntimeException("Нет прав на удаление этого адреса");
        }

        userDeliveryAddressRepository.delete(addr);
        return ResponseEntity.ok("Адрес доставки успешно удален");
    }

    @PostMapping("/user/delivery/create")
    public ResponseEntity<UserDeliveryAddress> createUserDeliveryAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Address newAddressData) {

        Long userId = extractUserIdFromToken(authHeader);

        // 1. Создаём новый адрес типа DELIVERY
        Address newAddress = new Address();
        newAddress.setAddressType(Address.AddressType.DELIVERY); // ✅ ENUM
        newAddress.setStreet(newAddressData.getStreet());
        newAddress.setCity(newAddressData.getCity());
        newAddress.setPostalCode(newAddressData.getPostalCode());
        newAddress.setCountry(newAddressData.getCountry());
        newAddress.setActive(true);

        Address savedAddress = addressRepository.save(newAddress); // ✅ addressRepository

        // 2. Создаём связь с пользователем
        UserDeliveryAddress userAddr = new UserDeliveryAddress();
        userAddr.setUserId(userId);
        userAddr.setAddress(savedAddress);
        userAddr.setIsDefault(false);

        UserDeliveryAddress result = userDeliveryAddressRepository.save(userAddr); // ✅ userDeliveryAddressRepository

        return ResponseEntity.ok(result);
    }

    // 🔥 НОВЫЙ МЕТОД: Установить адрес по умолчанию
    @Transactional
    @PutMapping("/user/delivery/{id}/set-default")
    public ResponseEntity<String> setUserDeliveryAddressAsDefault(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        Long userId = extractUserIdFromToken(authHeader);

        // Проверяем, принадлежит ли адрес пользователю
        UserDeliveryAddress addr = userDeliveryAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Адрес не найден"));

        if (!addr.getUserId().equals(userId)) {
            throw new RuntimeException("Нет прав на доступ к этому адресу");
        }

        // 🔥 Сбрасываем is_default у всех адресов пользователя
        userDeliveryAddressRepository.resetAllToFalseByUserId(userId);

        // 🔥 Устанавливаем текущий как default
        addr.setIsDefault(true);
        userDeliveryAddressRepository.save(addr); // Сохраняем обновлённый адрес

        return ResponseEntity.ok("Адрес установлен как основной");
    }

    @PutMapping("/user/select-pickup/{addressId}")
    public ResponseEntity<String> selectPickupAddress(@RequestHeader("Authorization") String authHeader, @PathVariable Long addressId) {
        Long userId = extractUserIdFromToken(authHeader);

        // Проверяем, что это точка самовывоза и активна
        Address pickupAddress = addressRepository.findById(addressId)
                .filter(a -> a.getAddressType() == Address.AddressType.PICKUP && a.isActive())
                .orElseThrow(() -> new RuntimeException("Точка самовывоза не найдена или неактивна"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setSelectedPickupAddressId(addressId);
        userRepository.save(user);

        return ResponseEntity.ok("Точка самовывоза выбрана");
    }

    // 🔥 НОВЫЙ МЕТОД: Получить выбранную точку самовывоза
    @GetMapping("/user/selected-pickup")
    public ResponseEntity<Address> getSelectedPickupAddress(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Long selectedId = user.getSelectedPickupAddressId();
        if (selectedId == null) {
            return ResponseEntity.noContent().build(); // Ничего не выбрано
        }

        Address pickupAddress = addressRepository.findById(selectedId)
                .filter(a -> a.getAddressType() == Address.AddressType.PICKUP && a.isActive())
                .orElseThrow(() -> new RuntimeException("Выбранная точка самовывоза больше не существует"));

        return ResponseEntity.ok(pickupAddress);
    }

    // 🔥 НОВЫЙ МЕТОД: Получить все адреса (доставки + самовывоз)
    // 🔥 НОВЫЙ МЕТОД: Получить все адреса (доставки + самовывоз)
    @GetMapping("/user/addresses-with-pickup")
    public ResponseEntity<List<AddressResponse>> getUserAddressesWithPickup(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);

        // Получаем все адреса доставки
        List<UserDeliveryAddress> deliveryAddresses = userDeliveryAddressRepository.findByUserId(userId);
        List<AddressResponse> response = deliveryAddresses.stream()
                .map(da -> new AddressResponse(
                        da.getId(),
                        da.getAddress().getStreet(),
                        da.getAddress().getCity(),
                        da.getAddress().getCountry(),
                        da.getAddress().getPostalCode(),
                        "DELIVERY",
                        da.getIsDefault()
                ))
                .collect(Collectors.toList());

        // Получаем выбранную точку самовывоза
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Long selectedPickupId = user.getSelectedPickupAddressId();

        if (selectedPickupId != null) {
            Address pickupAddress = addressRepository.findById(selectedPickupId)
                    .filter(a -> a.getAddressType() == Address.AddressType.PICKUP && a.isActive())
                    .orElse(null);

            if (pickupAddress != null) {
                response.add(new AddressResponse(
                        pickupAddress.getId(),
                        pickupAddress.getStreet(),
                        pickupAddress.getCity(),
                        pickupAddress.getCountry(),
                        pickupAddress.getPostalCode(),
                        "PICKUP",
                        null // Для точки самовывоза isDefault не имеет смысла
                ));
            }
        }

        return ResponseEntity.ok(response);
    }
    // 🔹 Извлечение userId из токена (для обычных пользователей)
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Нет токена!");
        }
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"))
                .getId();
    }

    // 🔹 Проверка роли администратора (повторяет логику из AdminController)
    private void validateAdminAccess(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Нет токена!");
        }
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        if (user.getRole() != Role.ADMIN) { // 🔹 Предполагается, что у User есть поле Role role;
            throw new RuntimeException("Доступ запрещён. Требуется роль ADMIN.");
        }
    }
}