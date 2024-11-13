package com.resale.requests.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String resellerCnpj;
    private String client;
    private String orderId;
    private List<OrderItem> items;
}

