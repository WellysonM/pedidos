package com.resale.requests.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderSend {
    private String resellerCnpj;
    private List<OrderItem> items;
}
