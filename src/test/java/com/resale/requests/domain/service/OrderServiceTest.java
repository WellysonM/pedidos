package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.Order;
import com.resale.requests.domain.entity.OrderItem;
import com.resale.requests.domain.entity.OrderResponse;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private ResellerService resellerService;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(OrderService.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveOrderSuccess() {
        String resellerCnpj = "12345678000100";
        OrderItem orderItem = builder();
        Order order = new Order();
        order.setResellerCnpj(resellerCnpj);
        order.setItems(List.of(orderItem, orderItem));

        doNothing().when(resellerService).validateReseller(resellerCnpj);

        OrderResponse response = orderService.receiveOrder(order);

        assertNotNull(response);
        assertNotNull(response.getOrderId());
        assertEquals(order.getItems(), response.getOrderItems());

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Receiving order for reseller with CNPJ: " + resellerCnpj)), "");

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Order saved successfully with ID: " + response.getOrderId())), "");
    }

    @Test
    void testReceiveOrderResellerValidationFails() {
        String resellerCnpj = "12345678000100";
        Order order = new Order();
        order.setResellerCnpj(resellerCnpj);

        doThrow(new RuntimeException("Reseller not found")).when(resellerService).validateReseller(resellerCnpj);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.receiveOrder(order);
        });
        assertEquals("Reseller not found", exception.getMessage());

        assertTrue(logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("Error when receiving order")), "");
    }

    @Test
    void testGetOrderSuccess() {
        String resellerCnpj = "12345678000100";
        OrderItem orderItem = builder();
        Order order = new Order();
        order.setResellerCnpj(resellerCnpj);
        order.setOrderId(UUID.randomUUID().toString());
        order.setItems(List.of(orderItem, orderItem));

        orderService.receiveOrder(order);

        List<Order> fetchedOrders = orderService.getOrder(resellerCnpj);

        assertNotNull(fetchedOrders);
        assertEquals(1, fetchedOrders.size());
        assertEquals(order.getOrderId(), fetchedOrders.get(0).getOrderId());
    }

    @Test
    void testGetOrder_OrderNotFound() {
        String resellerCnpj = "12345678000100";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrder(resellerCnpj);
        });
        assertEquals("Order with id: " + resellerCnpj + " not found.", exception.getMessage());

        assertTrue(logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("Error searching for order")), "");
    }

    private OrderItem builder() {
        return OrderItem.builder()
                .product("coca-cola")
                .quantity(10)
                .build();
    }
}
