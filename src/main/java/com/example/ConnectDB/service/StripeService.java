package com.example.ConnectDB.service;

import com.example.ConnectDB.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StripeService {

    // 🔥 ИСПРАВЛЕНО: дефис вместо точки в имени свойства
    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final OrderService orderService;

    // Конструктор с инъекцией зависимостей
    public StripeService(OrderService orderService) {
        this.orderService = orderService;
        Stripe.apiKey = secretKey;
    }

    // 🔥 Для Mobile: PaymentIntent
    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        return PaymentIntent.create(PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("source", "mobile_app")
                .build());
    }

    // 🔥 Для Web: Checkout Session
    public Session createCheckoutSession(Long amount, String currency,
                                         String successUrl, String cancelUrl,
                                         Long userId) throws StripeException {

        return Session.create(SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)

                .putMetadata("userId", userId.toString())
                .putMetadata("source", "web_app")

                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currency)
                                .setUnitAmount(amount)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Order - Crumbl App")
                                        .build())
                                .build())
                        .build())
                .build());
    }

    // 🔥 Метод для отладки: предпросмотр секрета
    public String getWebhookSecretPreview() {
        if (webhookSecret == null) return "NULL";
        if (webhookSecret.length() < 10) return "TOO_SHORT";
        return webhookSecret.substring(0, 10) + "...";
    }

    // 🔥 Обработка webhook — ИСПРАВЛЕННАЯ ВЕРСИЯ
    public void handleWebhook(String payload, String signature)
            throws SignatureVerificationException {

        System.out.println("🔍 [StripeService] Starting webhook processing");

        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            System.out.println("✅ Event type: " + event.getType());
            System.out.println("✅ Event ID: " + event.getId());

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getData().getObject();
                System.out.println("✅ Session ID: " + session.getId());
                System.out.println("🔍 All Metadata: " + session.getMetadata());

                // 🔥 Извлекаем metadata
                String userId = session.getMetadata().get("userId");
                String address = session.getMetadata().get("deliveryAddress");
                String date = session.getMetadata().get("deliveryDate");
                String totalAmount = session.getMetadata().get("totalAmount");

                // 🔥 Логируем значения для дебага
                System.out.println("🔍 Extracted values:");
                System.out.println("   userId: '" + userId + "'");
                System.out.println("   address: '" + address + "'");
                System.out.println("   date: '" + date + "'");
                System.out.println("   totalAmount: '" + totalAmount + "'");

                // 🔥 Проверяем обязательные поля
                if (userId == null || userId.trim().isEmpty()) {
                    System.out.println("❌ userId is missing or empty");
                    throw new RuntimeException("userId is required but not provided in metadata");
                }

                // 🔥 Безопасный парсинг даты
                String safeDate = null;
                if (date != null && !date.trim().isEmpty()) {
                    try {
                        // Если дата уже в формате YYYY-MM-DDTHH:mm:ss, используем как есть
                        if (date.contains("T")) {
                            safeDate = date;
                        } else {
                            // Если дата в формате YYYY-MM-DD, добавляем время
                            safeDate = date + "T00:00:00";
                        }
                        // Проверим, можно ли распарсить
                        LocalDateTime.parse(safeDate);
                    } catch (Exception e) {
                        System.out.println("⚠️ Invalid date format: " + date + ", using current date");
                        safeDate = null;
                    }
                }

                // 🔥 Безопасное извлечение суммы
                String safeTotalAmount = null;
                if (totalAmount != null && !totalAmount.trim().isEmpty()) {
                    try {
                        Long.parseLong(totalAmount); // Проверим, что это число
                        safeTotalAmount = totalAmount;
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Invalid amount format: " + totalAmount);
                        safeTotalAmount = null;
                    }
                }

                // 🔥 Создаем заказ с безопасными значениями
                Order order = orderService.createOrderFromStripe(
                        userId.trim(), // Ensure trimmed
                        address != null ? address.trim() : "Delivery address not provided",
                        safeDate, // Может быть null
                        session.getId(),
                        safeTotalAmount // Может быть null
                );

                System.out.println("✅ Order created successfully: ID=" + order.getId() +
                        ", totalPrice=" + order.getTotalPrice());

            } else if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent intent = (PaymentIntent) event.getData().getObject();
                System.out.println("✅ Payment succeeded: " + intent.getId());
            }

        } catch (SignatureVerificationException e) {
            System.out.println("❌ Signature verification failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("❌ Error in handleWebhook: " + e.getMessage());
            e.printStackTrace(); // Это важно для отладки
            throw e;
        }
    }
}