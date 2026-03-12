package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.CartItem;
import com.example.ConnectDB.model.User;
import com.example.ConnectDB.model.CartItem;
import com.example.ConnectDB.model.Cart;
import com.example.ConnectDB.model.Product;
import com.example.ConnectDB.model.constructorCake.CustomCake;
import com.example.ConnectDB.service.AuthService;
import com.example.ConnectDB.service.CartService;
import com.example.ConnectDB.service.CakeConstructorService;
import com.example.ConnectDB.service.CustomCakeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ConnectDB.service.CartService;
import com.example.ConnectDB.repository.ProductRepository;
import com.example.ConnectDB.repository.CartRepository;
import com.example.ConnectDB.service.ProductService;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthService authService;
    private final CakeConstructorService constructorService;
    private final CartRepository cartRepository;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        Long userId = authService.getCurrentUser().getId();
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/custom-cake")
    public ResponseEntity<CartItem> addCustomCakeToCart(
            @RequestBody CustomCakeRequest request,
            @RequestHeader("Authorization") String token
    ) {
        // Получаем ID пользователя из токена
        Long userId = authService.getUserIdFromToken(token);

        // Создаём кастомный торт через CakeConstructorService
        CustomCake customCake = constructorService.createCustomCake(request);

        // Добавляем торт в корзину
        CartItem cartItem = cartService.addCustomCakeToCart(userId, customCake, 1);

        return ResponseEntity.ok(cartItem);
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<CartItem> addProductToCart(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        Long userId = authService.getCurrentUser().getId();
        CartItem item = cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {

        Long userId = authService.getCurrentUser().getId();
        CartItem item = cartService.updateCartItemQuantity(userId, itemId, quantity);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        Long userId = authService.getCurrentUser().getId();
        cartService.removeFromCart(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        Long userId = authService.getCurrentUser().getId();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getCartTotal() {
        Long userId = authService.getCurrentUser().getId();
        BigDecimal total = cartService.calculateTotal(userId);
        return ResponseEntity.ok(Map.of("total", total));
    }
    @GetMapping("/diagnostic")
    public ResponseEntity<Map<String, Object>> diagnostic() {
        Long userId = authService.getCurrentUser().getId();

        Map<String, Object> diagnostic = new HashMap<>();
        diagnostic.put("userId", userId);

        try {
            // Проверяем существование пользователя
            Optional<User> user = authService.findById(userId);
            diagnostic.put("userExists", user.isPresent());

            if (user.isPresent()) {
                diagnostic.put("username", user.get().getUsername());
            }

            // Проверяем существование корзины
            boolean cartExists = cartRepository.existsByUserId(userId);
            diagnostic.put("cartExists", cartExists);

            if (cartExists) {
                Optional<Cart> cart = cartRepository.findByUserId(userId);
                diagnostic.put("cartId", cart.map(Cart::getId).orElse(null));
                diagnostic.put("cartInDatabase", cart.isPresent());

                if (cart.isPresent()) {
                    boolean cartExistsInDb = cartRepository.existsById(cart.get().getId());
                    diagnostic.put("cartExistsInDatabaseById", cartExistsInDb);
                }
            }

            // Получаем все корзины из базы для отладки
            List<Cart> allCarts = cartRepository.findAll();
            List<Map<String, Object>> cartsInfo = allCarts.stream()
                    .map(c -> Map.<String, Object>of(
                            "id", c.getId(),
                            "userId", c.getUser().getId(),
                            "username", c.getUser().getUsername()
                    ))
                    .toList();
            diagnostic.put("allCartsInDatabase", cartsInfo);

            diagnostic.put("status", "SUCCESS");
        } catch (Exception e) {
            diagnostic.put("status", "ERROR");
            diagnostic.put("error", e.getMessage());
        }

        return ResponseEntity.ok(diagnostic);
    }

    @GetMapping("/products-check")
    public ResponseEntity<Map<String, Object>> checkProducts() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Product> allProducts = productService.findAll();
            result.put("totalProducts", allProducts.size());
            result.put("products", allProducts.stream()
                    .map(p -> Map.of(
                            "id", p.getId(),
                            "name", p.getName(),
                            "price", p.getPrice(),
                            "inStock", p.getInStock()
                    ))
                    .toList());
            result.put("status", "SUCCESS");
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/test-add-product")
    public ResponseEntity<Map<String, Object>> testAddProduct() {
        Map<String, Object> result = new HashMap<>();

        try {
            Long userId = authService.getCurrentUser().getId();

            // Пробуем добавить продукт с ID=1
            CartItem item = cartService.addProductToCart(userId, 1L, 1);

            result.put("status", "SUCCESS");
            result.put("message", "Product added to cart");
            result.put("cartItemId", item.getId());
            result.put("productName", item.getProduct().getName());

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}