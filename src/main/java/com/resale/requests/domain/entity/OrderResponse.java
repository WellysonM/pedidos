package com.resale.requests.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String orderId;
    private List<OrderItem> orderItems;
}
