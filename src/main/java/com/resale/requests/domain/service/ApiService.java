package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.OrderResponse;
import com.resale.requests.domain.entity.OrderSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private final Map<String, OrderSend> failedOrders = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Retryable(retryFor = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public OrderResponse sendOrderToApi(OrderSend orderSend) {
        if (random.nextBoolean()) {
            throw new RuntimeException("API is currently unavailable.");
        }
        logger.info("Sending to API successful");
        return OrderResponse.builder()
                .orderId(UUID.randomUUID().toString())
                .orderItems(orderSend.getItems())
                .build();
    }

    @Recover
    public OrderResponse recover(RuntimeException e, OrderSend orderSend) {
        logger.warn("Sending to API failed after multiple attempts. Will retry later.");
        failedOrders.put(orderSend.getResellerCnpj(), orderSend);
        throw new RuntimeException("Order API is temporarily down, we will automatically try again in 30 minutes.");
    }


    @Scheduled(fixedRate = 1800000)
    public void retryFailedOrders() {
        for (Map.Entry<String, OrderSend> entry : failedOrders.entrySet()) {
            String cnpj = entry.getKey();
            OrderSend orderSend = entry.getValue();

            logger.info("Retrying order for reseller with CNPJ: {}", cnpj);
            try {
                sendOrderToApi(orderSend);
                logger.info("Retry successful for CNPJ: {}", cnpj);
                failedOrders.remove(cnpj);
            } catch (RuntimeException e) {
                logger.error("Retry failed for CNPJ: {}. Reason: {}", cnpj, e.getMessage());
            }
        }
    }
}
