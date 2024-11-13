package com.resale.requests.domain.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Address {
    @NotBlank
    private String street;

    @NotBlank
    private String number;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String district;

    @NotBlank
    private String city;

    @NotBlank
    private String state;
}
