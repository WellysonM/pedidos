package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.Order;
import com.resale.requests.domain.entity.OrderItem;
import com.resale.requests.domain.entity.OrderResponse;
import com.resale.requests.domain.entity.OrderSend;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SendOrderService {

    private OrderService orderService;
    private ResellerService resellerService;
    private ApiService apiService;
    private static final Logger logger = LoggerFactory.getLogger(SendOrderService.class);

    public OrderResponse sendOrderToApi(String cnpj) {
        try {
            resellerService.validateReseller(cnpj);
            List<Order> orders = orderService.getOrder(cnpj);
            logger.info("Send orders to API reseller cnpj: {}", cnpj);

            int totalQuantity = orders.stream()
                    .flatMap(order -> order.getItems().stream())
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            if (totalQuantity < 1000) {
                throw new RuntimeException("Order does not meet the minimum quantity of 1000 units.");
            }

            OrderSend orderSend = OrderSend.builder()
                    .resellerCnpj(cnpj)
                    .items(orders.stream()
                            .flatMap(order -> order.getItems().stream())
                            .toList())
                    .build();

            return apiService.sendOrderToApi(orderSend);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
