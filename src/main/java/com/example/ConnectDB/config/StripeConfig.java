package com.example.ConnectDB.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Bean
    public String initStripe() {
        Stripe.apiKey = stripeSecretKey;
        return Stripe.apiKey; // возвращаем объект, чтобы Spring зарегистрировал бин
    }
}