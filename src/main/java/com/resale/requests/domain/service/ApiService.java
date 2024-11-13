package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.OrderResponse;
import com.resale.requests.domain.entity.OrderSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Retryable(retryFor = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public OrderResponse sendOrderToApi(OrderSend orderSend) {
        if (new Random().nextBoolean()) {
            throw new RuntimeException("API is currently unavailable.");
        }
        logger.info("sending to API successful");
        return OrderResponse.builder()
                .orderId(UUID.randomUUID().toString())
                .orderItems(orderSend.getItems())
                .build();
    }

    @Recover
    public OrderResponse recover(RuntimeException e, OrderSend orderSend) {
        logger.info("Sending to API failed");
        throw new RuntimeException("Please try again in a few minutes.");
    }

}

