package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.constructorCake.*;
import com.example.ConnectDB.service.CakeConstructorService;
import com.example.ConnectDB.service.CustomCakeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/constructor")
@CrossOrigin(origins = "*")
public class CakeConstructorController {

    @Autowired
    private CakeConstructorService constructorService;

    // Получение всех доступных компонентов
    @GetMapping("/cake-types")
    public ResponseEntity<List<CakeType>> getAllCakeTypes() {
        return ResponseEntity.ok(constructorService.getAllCakeTypes());
    }

    @GetMapping("/cake-layers")
    public ResponseEntity<List<CakeLayer>> getAllCakeLayers() {
        return ResponseEntity.ok(constructorService.getAllCakeLayers());
    }

    @GetMapping("/cake-creams")
    public ResponseEntity<List<CakeCream>> getAllCakeCreams() {
        return ResponseEntity.ok(constructorService.getAllCakeCreams());
    }

    @GetMapping("/cake-fillings")
    public ResponseEntity<List<CakeFilling>> getAllCakeFillings() {
        return ResponseEntity.ok(constructorService.getAllCakeFillings());
    }

    @GetMapping("/cake-coatings")
    public ResponseEntity<List<CakeCoating>> getAllCakeCoatings() {
        return ResponseEntity.ok(constructorService.getAllCakeCoatings());
    }

    @GetMapping("/coating-colors")
    public ResponseEntity<List<CoatingColor>> getAllCoatingColors() {
        return ResponseEntity.ok(constructorService.getAllCoatingColors());
    }

    @GetMapping("/decorations")
    public ResponseEntity<List<Decoration>> getAllDecorations() {
        return ResponseEntity.ok(constructorService.getAllDecorations());
    }

    @GetMapping("/candles")
    public ResponseEntity<List<Candle>> getAllCandles() {
        return ResponseEntity.ok(constructorService.getAllCandles());
    }

    // Создание кастомного торта
    @PostMapping("/create-cake")
    public ResponseEntity<CustomCake> createCustomCake(@RequestBody CustomCakeRequest request) {
        try {
            CustomCake customCake = constructorService.createCustomCake(request);
            return ResponseEntity.ok(customCake);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Получение тортов пользователя
    @GetMapping("/user-cakes/{userId}")
    public ResponseEntity<List<CustomCake>> getUserCakes(@PathVariable Long userId) {
        return ResponseEntity.ok(constructorService.getUserCustomCakes(userId));
    }

    // Получение конкретного торта
    @GetMapping("/cake/{id}")
    public ResponseEntity<CustomCake> getCustomCake(@PathVariable Long id) {
        Optional<CustomCake> cake = constructorService.getCustomCakeById(id);
        return cake.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Удаление торта
    @DeleteMapping("/cake/{id}")
    public ResponseEntity<String> deleteCustomCake(@PathVariable Long id) {
        try {
            constructorService.deleteCustomCake(id);
            return ResponseEntity.ok("Торт успешно удален");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при удалении торта");
        }
    }
    @GetMapping("/cake-texts")
    public ResponseEntity<List<CakeText>> getAllCakeTexts() {
        return ResponseEntity.ok(constructorService.getAllCakeTexts());
    }
}