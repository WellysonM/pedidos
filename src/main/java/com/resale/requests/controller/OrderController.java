package com.resale.requests.controller;

import com.resale.requests.domain.entity.Order;
import com.resale.requests.domain.service.OrderService;
import com.resale.requests.domain.service.SendOrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/resellers/orders")
public class OrderController {

    private SendOrderService sendOrderService;
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<?> receiveOrder(@Valid @RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.receiveOrder(order));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOrder(@RequestParam String cnpj) {
        try {
            return ResponseEntity.ok(sendOrderService.sendOrderToApi(cnpj));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
