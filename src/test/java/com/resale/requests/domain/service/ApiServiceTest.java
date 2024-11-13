package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.OrderItem;
import com.resale.requests.domain.entity.OrderResponse;
import com.resale.requests.domain.entity.OrderSend;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiServiceTest {

    @InjectMocks
    private ApiService apiService;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logCaptor = LogCaptor.forClass(ApiService.class);
    }

    @Test
    void sendOrderToApiSuccess() {
        OrderSend orderSend = OrderSend.builder()
                .resellerCnpj("12345678901234")
                .items(List.of(new OrderItem("item1", 100)))
                .build();

        Random randomMock = mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(false);

        ReflectionTestUtils.setField(apiService, "random", randomMock);

        OrderResponse response = apiService.sendOrderToApi(orderSend);

        assertNotNull(response);
        assertEquals(1, response.getOrderItems().size());
        assertNotNull(response.getOrderId());

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Sending to API successful")), "");
    }

    @Test
    void sendOrderToApiFailure() {
        OrderSend orderSend = OrderSend.builder()
                .resellerCnpj("12345678901234")
                .items(List.of(new OrderItem("item1", 100)))
                .build();

        Random randomMock = mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(true);

        ReflectionTestUtils.setField(apiService, "random", randomMock);

        assertThrows(RuntimeException.class, () -> apiService.sendOrderToApi(orderSend));
    }

    @Test
    void recoverShouldStoreFailedOrder() {

        OrderSend orderSend = OrderSend.builder()
                .resellerCnpj("12345678901234")
                .items(List.of(new OrderItem("item1", 100)))
                .build();

        RuntimeException exception = new RuntimeException("API failure");

        assertThrows(RuntimeException.class, () -> apiService.recover(exception, orderSend));

        Map<String, OrderSend> failedOrders = (Map<String, OrderSend>) ReflectionTestUtils.getField(apiService, "failedOrders");
        assertNotNull(failedOrders);
        assertTrue(failedOrders.containsKey("12345678901234"));

        assertTrue(logCaptor.getWarnLogs().stream()
                .anyMatch(log -> log.contains("Sending to API failed after multiple attempts. Will retry later.")), "");
    }

    @Test
    void retryFailedOrdersSuccess() {
        OrderSend orderSend = OrderSend.builder()
                .resellerCnpj("12345678901234")
                .items(List.of(new OrderItem("item1", 100)))
                .build();

        Map<String, OrderSend> failedOrders = new ConcurrentHashMap<>();
        failedOrders.put("12345678901234", orderSend);
        ReflectionTestUtils.setField(apiService, "failedOrders", failedOrders);

        Random randomMock = mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(false);
        ReflectionTestUtils.setField(apiService, "random", randomMock);

        apiService.retryFailedOrders();

        assertFalse(failedOrders.containsKey("12345678901234"));

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Retrying order for reseller with CNPJ: 12345678901234")), "");

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Retry successful for CNPJ: 12345678901234")), "");
    }

    @Test
    void retryFailedOrdersFailure() {
        OrderSend orderSend = OrderSend.builder()
                .resellerCnpj("12345678901234")
                .items(List.of(new OrderItem("item1", 100)))
                .build();

        Random randomMock = mock(Random.class);
        when(randomMock.nextBoolean()).thenReturn(true);
        ReflectionTestUtils.setField(apiService, "random", randomMock);

        Map<String, OrderSend> failedOrders = new ConcurrentHashMap<>();
        failedOrders.put("12345678901234", orderSend);
        ReflectionTestUtils.setField(apiService, "failedOrders", failedOrders);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            apiService.sendOrderToApi(orderSend);
        });

        assertEquals("API is currently unavailable.", exception.getMessage());

        apiService.retryFailedOrders();

        assertTrue(failedOrders.containsKey("12345678901234"));

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Retrying order for reseller with CNPJ: 12345678901234")), "");

        assertTrue(logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("Retry failed for CNPJ: 12345678901234. Reason: " + exception.getMessage())), "");
    }
}
