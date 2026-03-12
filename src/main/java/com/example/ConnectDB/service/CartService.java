package com.example.ConnectDB.service;

import com.example.ConnectDB.model.*;
import com.example.ConnectDB.model.constructorCake.CustomCake;
import com.example.ConnectDB.repository.CartItemRepository;
import com.example.ConnectDB.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CakeConstructorService customCakeService;
    private final AuthService authService;

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        log.info("Getting or creating cart for user ID: {}", userId);

        Optional<Cart> existingCart = cartRepository.findByUserId(userId);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            log.info("Found existing cart with ID: {} for user ID: {}", cart.getId(), userId);

            // Проверяем, что корзина действительно существует в базе
            if (cartRepository.existsById(cart.getId())) {
                return cart;
            } else {
                log.warn("Cart ID {} not found in database, creating new one", cart.getId());
                return createNewCart(userId);
            }
        } else {
            return createNewCart(userId);
        }
    }

    private Cart createNewCart(Long userId) {
        log.info("Creating new cart for user ID: {}", userId);
        User user = authService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Cart cart = new Cart();
        cart.setUser(user);
        Cart savedCart = cartRepository.save(cart);
        log.info("Created new cart with ID: {} for user ID: {}", savedCart.getId(), userId);
        return savedCart;
    }

    @Transactional
    public CartItem addProductToCart(Long userId, Long productId, Integer quantity) {
        try {
            log.info("Adding product ID: {} to cart for user ID: {}, quantity: {}", productId, userId, quantity);

            // Получаем или создаем корзину с явной проверкой
            Cart cart = getOrCreateCart(userId);
            log.info("Using cart ID: {} for user ID: {}", cart.getId(), userId);

            // Явно проверяем существование корзины в базе
            if (!cartRepository.existsById(cart.getId())) {
                throw new RuntimeException("Cart not found in database with ID: " + cart.getId());
            }

            // Проверяем существование продукта
            Product product = productService.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            log.info("Found product: {} with price: {}", product.getName(), product.getPrice());

            // Проверяем существующий элемент корзины
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + quantity);
                item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                CartItem savedItem = cartItemRepository.save(item);
                log.info("Updated existing cart item ID: {}, new quantity: {}", savedItem.getId(), savedItem.getQuantity());
                return savedItem;
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                newItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

                CartItem savedItem = cartItemRepository.save(newItem);
                log.info("Created new cart item ID: {} for product: {}", savedItem.getId(), product.getName());
                return savedItem;
            }
        } catch (Exception e) {
            log.error("Error adding product to cart: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add product to cart: " + e.getMessage());
        }
    }

    public CartItem addCustomCakeToCart(Long userId, CustomCake customCake, Integer quantity) {
        var cart = getOrCreateCart(userId);

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setCustomCake(customCake);
        newItem.setQuantity(quantity);
        newItem.setPrice(customCake.getTotalPrice().multiply(BigDecimal.valueOf(quantity)));

        return cartItemRepository.save(newItem);
    }

    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCartId(cart.getId());
    }

    public CartItem updateCartItemQuantity(Long userId, Long itemId, Integer quantity) {
        if (quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Проверяем принадлежность элемента корзины пользователю
        if (!item.getCart().getUser().getId().equals(userId)) { // ← исправленная проверка
            throw new RuntimeException("Cart item does not belong to user");
        }

        item.setQuantity(quantity);

        // Пересчитываем цену
        BigDecimal unitPrice;
        if (item.getProduct() != null) {
            unitPrice = item.getProduct().getPrice();
        } else {
            unitPrice = item.getCustomCake().getTotalPrice();
        }
        item.setPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));

        return cartItemRepository.save(item);
    }

    public void removeFromCart(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getUser().getId().equals(userId)) { // ← исправленная проверка
            throw new RuntimeException("Cart item does not belong to user");
        }

        cartItemRepository.delete(item);
    }

    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    public BigDecimal calculateTotal(Long userId) {
        List<CartItem> items = getCartItems(userId);
        return items.stream()
                .map(CartItem::getPrice) // ← исправлено: используем уже рассчитанную цену
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}