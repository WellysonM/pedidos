package com.resale.requests.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {
    private String street;
    private String number;
    private String zipCode;
    private String district;
    private String city;
    private String state;
}
