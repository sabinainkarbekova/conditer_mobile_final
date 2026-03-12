// src/main/java/com/example/ConnectDB/controller/PaymentController.java
package com.example.ConnectDB.controller;

import com.example.ConnectDB.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
// 🔥 ИСПРАВЛЕННЫЙ ИМПОРТ:
import com.stripe.param.checkout.SessionCreateParams;  // ✅ checkout, не billingportal!
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    // 🔥 WEB: создать Checkout Session

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {

        try {
            Long amount = ((Number) request.get("amount")).longValue();
            String currency = (String) request.getOrDefault("currency", "usd");
            String successUrl = (String) request.get("successUrl");
            String cancelUrl = (String) request.get("cancelUrl");

            // 🔥 Извлекаем metadata
            String userId = request.get("userId") != null ? request.get("userId").toString() : "";
            String deliveryAddress = request.get("deliveryAddress") != null ? request.get("deliveryAddress").toString() : "";
            String deliveryDate = request.get("deliveryDate") != null ? request.get("deliveryDate").toString() : "";
            String totalAmount = request.get("totalAmount") != null ? request.get("totalAmount").toString() : "0"; // 🔥 Сумма

            SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(currency)
                                    .setUnitAmount(amount)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Order - Crumbl App")
                                            .build())
                                    .build())
                            .build());

            // 🔥 Добавляем metadata (включая totalAmount)
            if (userId != null && !userId.isEmpty()) sessionBuilder.putMetadata("userId", userId);
            if (deliveryAddress != null && !deliveryAddress.isEmpty()) sessionBuilder.putMetadata("deliveryAddress", deliveryAddress);
            if (deliveryDate != null && !deliveryDate.isEmpty()) sessionBuilder.putMetadata("deliveryDate", deliveryDate);
            if (totalAmount != null && !totalAmount.isEmpty()) sessionBuilder.putMetadata("totalAmount", totalAmount); // 🔥
            sessionBuilder.putMetadata("source", "web_app");

            Session session = Session.create(sessionBuilder.build());
            return ResponseEntity.ok(Map.of("url", session.getUrl()));

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/webhook/test")
    @CrossOrigin(origins = "*")  // 🔥 Разрешаем CORS для теста
    public ResponseEntity<?> testWebhookEndpoint() {
        System.out.println("🧪 [Test] Webhook endpoint is reachable!");
        return ResponseEntity.ok(Map.of("status", "ok", "timestamp", System.currentTimeMillis()));
    }
    // 🔥 Webhook
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {

        System.out.println("🚨 WEBHOOK HIT! Time: " + System.currentTimeMillis());
        System.out.println("🔍 Signature header: " + signature);
        System.out.println("🔍 Payload preview: " +
                (payload.length() > 100 ? payload.substring(0, 100) + "..." : payload));

        try {
            stripeService.handleWebhook(payload, signature);
            System.out.println("✅ Webhook processed successfully");
            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            System.out.println("❌ SIGNATURE FAILED: " + e.getMessage());
            System.out.println("🔍 Expected secret starts with: " +
                    (stripeService.getWebhookSecretPreview())); // добавь этот метод
            return ResponseEntity.status(400).body(Map.of("error", "Invalid signature"));

        } catch (Exception e) {
            System.out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}