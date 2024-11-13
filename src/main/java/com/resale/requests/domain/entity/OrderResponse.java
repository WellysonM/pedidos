package com.resale.requests.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private List<OrderItem> orderItems;
}
