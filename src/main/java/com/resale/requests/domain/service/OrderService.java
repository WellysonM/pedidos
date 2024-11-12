package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.Order;
import com.resale.requests.domain.entity.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private ResellerService resellerService;
    private final Map<String, List<Order>> orderMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderResponse receiveOrder(Order order) {
        logger.info("Receiving order for reseller with CNPJ: {}", order.getResellerCnpj());

        resellerService.validateReseller(order.getResellerCnpj());
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        List<Order> orders = orderMap.getOrDefault(order.getResellerCnpj(), new ArrayList<>());
        orders.add(order);
        orderMap.put(order.getResellerCnpj(), orders);

        logger.info("Order saved successfully with ID: {}", orderId);

        return OrderResponse.builder()
                .orderId(orderId)
                .orderItems(order.getItems())
                .build();
    }


    public List<Order> getOrder(String resellerCnpj) {
        try {
            List<Order> orders = orderMap.get(resellerCnpj);
            if (Objects.isNull(orders) || orders.isEmpty()) {
                throw new Exception("Order with id: " + resellerCnpj + " not found.");
            }
            return orders;
        } catch (Exception e) {
            logger.error("Error searching for order");
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}

