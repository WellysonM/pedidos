package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.OrderResponse;
import com.resale.requests.domain.entity.OrderSend;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public OrderResponse sendOrderToApi(OrderSend orderSend) {
        try {
            if (new Random().nextBoolean()) {
                throw new Exception("API is currently unavailable.");
            }
            return OrderResponse.builder()
                    .orderId(UUID.randomUUID().toString())
                    .orderItems(orderSend.getItems())
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}

