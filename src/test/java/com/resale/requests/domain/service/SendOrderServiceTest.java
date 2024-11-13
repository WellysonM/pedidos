package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.Order;
import com.resale.requests.domain.entity.OrderItem;
import com.resale.requests.domain.entity.OrderResponse;
import com.resale.requests.domain.entity.OrderSend;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendOrderServiceTest {

    @InjectMocks
    private SendOrderService sendOrderService;

    @Mock
    private ResellerService resellerService;

    @Mock
    private OrderService orderService;

    @Mock
    private ApiService apiService;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(SendOrderService.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOrderToApi_Success() {
        String cnpj = "12345678000100";
        List<OrderItem> orderItems = List.of(
                new OrderItem("Product1", 600),
                new OrderItem("Product2", 500)
        );
        Order order = builderOrder();
        order.setItems(orderItems);

        doNothing().when(resellerService).validateReseller(cnpj);

        List<Order> orders = List.of(order);
        when(orderService.getOrder(cnpj)).thenReturn(orders);

        OrderResponse expectedResponse = new OrderResponse("12345", orderItems);
        when(apiService.sendOrderToApi(any(OrderSend.class))).thenReturn(expectedResponse);

        OrderResponse response = sendOrderService.sendOrderToApi(cnpj);

        assertNotNull(response);
        assertEquals(expectedResponse.getOrderId(), response.getOrderId());
        assertEquals(expectedResponse.getOrderItems(), response.getOrderItems());

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Send orders to API reseller cnpj: " + cnpj)), "");

        verify(apiService).sendOrderToApi(any(OrderSend.class));
    }

    @Test
    void testSendOrderToApi_InvalidReseller() {
        String cnpj = "12345678000100";
        doThrow(new RuntimeException("Invalid reseller")).when(resellerService).validateReseller(cnpj);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sendOrderService.sendOrderToApi(cnpj);
        });
        assertEquals("Invalid reseller", exception.getMessage());
        verify(resellerService).validateReseller(cnpj);
    }

    @Test
    void testSendOrderToApi_MinimumQuantityNotMet() {
        String cnpj = "12345678000100";

        doNothing().when(resellerService).validateReseller(cnpj);

        List<OrderItem> orderItems = List.of(
                new OrderItem("Product1", 400),
                new OrderItem("Product2", 500)
        );
        Order order = builderOrder();
        order.setItems(orderItems);
        List<Order> orders = List.of(order);
        when(orderService.getOrder(cnpj)).thenReturn(orders);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sendOrderService.sendOrderToApi(cnpj);
        });
        assertEquals("Order does not meet the minimum quantity of 1000 units.", exception.getMessage());
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Send orders to API reseller cnpj: " + cnpj)), "");
    }

    @Test
    void testSendOrderToApi_ApiFails() {
        String cnpj = "12345678000100";

        doNothing().when(resellerService).validateReseller(cnpj);

        List<OrderItem> orderItems = List.of(
                new OrderItem("Product1", 600),
                new OrderItem("Product2", 500)
        );
        Order order = builderOrder();
        order.setItems(orderItems);
        List<Order> orders = List.of(order);
        when(orderService.getOrder(cnpj)).thenReturn(orders);

        when(apiService.sendOrderToApi(any(OrderSend.class)))
                .thenThrow(new RuntimeException("API is unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sendOrderService.sendOrderToApi(cnpj);
        });
        assertEquals("API is unavailable", exception.getMessage());
    }

    public Order builderOrder() {
        return Order.builder()
                .resellerCnpj("12345678000100")
                .client("market")
                .items(List.of(builderOrderItem(), builderOrderItem()))
                .build();
    }

    private OrderItem builderOrderItem() {
        return OrderItem.builder()
                .product("coca-cola")
                .quantity(10)
                .build();
    }
}

