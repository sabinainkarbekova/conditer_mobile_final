package com.example.ConnectDB.service;

import com.example.ConnectDB.controller.OrderController;
import com.example.ConnectDB.model.CartItem;
import com.example.ConnectDB.model.Order;
import com.example.ConnectDB.model.OrderItem;
import com.example.ConnectDB.model.Product;
import com.example.ConnectDB.model.User;
import com.example.ConnectDB.model.constructorCake.CustomCake;
import com.example.ConnectDB.repository.OrderItemRepository;
import com.example.ConnectDB.repository.OrderRepository;
import com.example.ConnectDB.repository.ProductRepository; // 🔥 Нужен для получения Product если cart пуст
import com.example.ConnectDB.repository.UserRepository;
import com.example.ConnectDB.repository.CartItemRepository; // 🔥 Нужен для получения из корзины
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService; // Предполагается, что этот сервис существует
    private final AuthService userService; // Предполагается, что этот сервис существует
    private final UserRepository userRepository;
    private final ProductRepository productRepository; // 🔥 Для случая, если корзина пуста
    private final CartItemRepository cartItemRepository; // 🔥 Для получения из корзины

    public Order createOrderFromCart(Long userId, OrderController.OrderRequest request) {
        List<CartItem> cartItems = cartService.getCartItems(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryDate(LocalDateTime.parse(request.getDeliveryDate()));
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("pending");

        // Calculate total price
        BigDecimal total = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);

        // Create order items from cart items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setCustomCake(cartItem.getCustomCake());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemRepository.save(orderItem);
        }

        // Clear cart after order creation
        cartService.clearCart(userId);

        return savedOrder;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    public Optional<Order> findByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id);
    }
    public Optional<Order> findByIdWithItemsAndUser(Long id, Long userId) {
        return orderRepository.findByIdWithItemsAndUser(id, userId);
    }

    public Order createOrderFromStripe(
            String userIdStr,
            String deliveryAddress,
            String deliveryDateStr,
            String paymentIntentId,
            String totalAmountCents) {  // 🔥 Добавили параметр: сумма в центах

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid userId format: " + userIdStr);
            throw new IllegalArgumentException("Invalid userId format: " + userIdStr);
        }

        // 🔥 Проверяем существование пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMsg = "User not found: ID=" + userId;
                    System.out.println("❌ " + errorMsg);
                    throw new RuntimeException(errorMsg);
                });

        // 🔥 Безопасный парсинг даты
        LocalDateTime deliveryDate = null;
        if (deliveryDateStr != null && !deliveryDateStr.trim().isEmpty()) {
            try {
                if (deliveryDateStr.contains("T")) {
                    deliveryDate = LocalDateTime.parse(deliveryDateStr);
                } else {
                    deliveryDate = LocalDateTime.parse(deliveryDateStr + "T00:00:00");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Invalid date format: " + deliveryDateStr + ", using default");
                deliveryDate = LocalDateTime.now().plusDays(1);
            }
        } else {
            deliveryDate = LocalDateTime.now().plusDays(1);
        }

        // 🔥 Рассчитываем общую сумму (приоритет: переданная сумма > сумма из корзины)
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (totalAmountCents != null && !totalAmountCents.trim().isEmpty()) {
            try {
                Long amountInCents = Long.parseLong(totalAmountCents);
                // Конвертируем центы → рубли/тенге
                totalPrice = new BigDecimal(amountInCents).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid totalAmount format: " + totalAmountCents);
                // Попробуем получить из корзины как fallback
                totalPrice = getTotalPriceFromCart(userId);
            }
        } else {
            // Fallback: получаем из корзины
            totalPrice = getTotalPriceFromCart(userId);
        }

        // 🔥 Создаём заказ
        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress != null && !deliveryAddress.isEmpty()
                ? deliveryAddress
                : "Delivery address not provided");
        order.setDeliveryDate(deliveryDate);
        order.setPaymentMethod("stripe");
        order.setStatus("pending");  // ✅ Stripe уже подтвердил оплату
        order.setPaymentIntentId(paymentIntentId);
        order.setTotalPrice(totalPrice);  // ✅ Устанавливаем сумму!

        Order savedOrder = orderRepository.save(order);

        // 🔥 Копируем items из корзины в заказ (только если корзина не пуста)
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setCustomCake(cartItem.getCustomCake());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                orderItemRepository.save(orderItem);
            }

            // 🔥 Очищаем корзину после успешного создания заказа
            cartService.clearCart(userId);
        } else {
            System.out.println("⚠️ Cart is empty for user " + userId + ", creating order without items");
            // Если корзина пуста, можно решить, создавать ли заказ вообще или бросить исключение
            // В данном случае, заказ создаётся, но без items
        }

        System.out.println("✅ Order created: ID=" + savedOrder.getId() +
                ", totalPrice=" + savedOrder.getTotalPrice() +
                ", items=" + (cartItems.isEmpty() ? 0 : cartItems.size()));

        return savedOrder;
    }

    // 🔥 Вспомогательный метод для получения цены из корзины
    private BigDecimal getTotalPriceFromCart(Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            System.out.println("⚠️ Cart is empty, using default price 0");
            return BigDecimal.ZERO;
        }

        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("💰 Calculated total from cart: " + totalPrice);
        return totalPrice;
    }

    // 🔥 Перегрузка для обратной совместимости
    public Order createOrderFromStripe(
            String userIdStr,
            String deliveryAddress,
            String deliveryDateStr,
            String paymentIntentId) {
        return createOrderFromStripe(userIdStr, deliveryAddress, deliveryDateStr, paymentIntentId, null);
    }

    public Order updateStatus(Long id, String status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}