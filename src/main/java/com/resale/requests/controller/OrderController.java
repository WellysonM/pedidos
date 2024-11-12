package com.resale.requests.controller;

import com.resale.requests.domain.entity.Order;
import com.resale.requests.domain.entity.OrderResponse;
import com.resale.requests.domain.service.OrderService;
import com.resale.requests.domain.service.SendOrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/resellers/orders")
public class OrderController {

    private SendOrderService sendOrderService;
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderResponse> receiveOrder(@Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.receiveOrder(order));
    }

    @PostMapping("/send")
    public ResponseEntity<OrderResponse> dispatchOrder(@RequestParam String cnpj) {
        return ResponseEntity.ok(sendOrderService.sendOrderToApi(cnpj));
    }
}
