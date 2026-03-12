package com.example.ConnectDB.controller;

import com.example.ConnectDB.model.Order;
import com.example.ConnectDB.service.OrderService;
import com.example.ConnectDB.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    // ================= CREATE ORDER =================

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Long userId = authService.getCurrentUser().getId();
        Order order = orderService.createOrderFromCart(userId, request);
        return ResponseEntity.ok(mapToResponse(order));
    }

    // ================= GET MY ORDERS =================

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        Long userId = authService.getCurrentUser().getId();
        List<Order> orders = orderService.findByUserId(userId);

        List<OrderResponse> responses = orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ================= GET ORDER BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(order -> ResponseEntity.ok(mapToResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= UPDATE STATUS =================

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {

        try {
            Order updatedOrder = orderService.updateStatus(id, request.getStatus());
            return ResponseEntity.ok(mapToResponse(updatedOrder));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ================= GET ALL ORDERS =================

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestParam(required = false) String status) {

        List<Order> orders = status != null
                ? orderService.findByStatus(status)
                : orderService.findAll();

        List<OrderResponse> responses = orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ================= GET ORDER WITH ITEMS (FIXED) =================
    // 🔥 ИСПРАВЛЕНО: Теперь возвращает полноценный объект Order с загруженными items
    @GetMapping("/{id}/details")
    public ResponseEntity<Order> getOrderWithItems(@PathVariable Long id) {
        Long currentUserId = authService.getCurrentUser().getId();

        // 🔥 Используем метод, который точно загружает связанные OrderItem
        return orderService.findByIdWithItemsAndUser(id, currentUserId)
                .map(ResponseEntity::ok) // 🔥 Теперь возвращает весь Order с orderItems
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔥 Простой вариант без проверки прав (если не нужна):
    @GetMapping("/{id}/details-simple")
    public ResponseEntity<Order> getOrderWithItemsSimple(@PathVariable Long id) {
        // 🔥 Используем метод, который точно загружает связанные OrderItem
        return orderService.findByIdWithItems(id)
                .map(ResponseEntity::ok) // 🔥 Теперь возвращает весь Order с orderItems
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= MAPPER =================

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getDeliveryAddress()
        );
    }

    // ================= DTO CLASSES =================

    public static class OrderResponse {
        private Long id;
        private String status;
        private BigDecimal totalPrice;
        private String deliveryAddress;

        public OrderResponse(Long id, String status, BigDecimal totalPrice, String deliveryAddress) {
            this.id = id;
            this.status = status;
            this.totalPrice = totalPrice;
            this.deliveryAddress = deliveryAddress;
        }

        public Long getId() { return id; }
        public String getStatus() { return status; }
        public BigDecimal getTotalPrice() { return totalPrice; }
        public String getDeliveryAddress() { return deliveryAddress; }
    }

    public static class OrderRequest {
        private String deliveryAddress;
        private String deliveryDate;
        private String paymentMethod;

        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

        public String getDeliveryDate() { return deliveryDate; }
        public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class StatusRequest {
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}