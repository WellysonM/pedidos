package com.resale.requests.domain.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Contacts {
    private boolean main;

    @NotBlank
    private String contact;
}
