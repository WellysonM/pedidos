package com.resale.requests.domain.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Reseller {

    @NotBlank
    private String cnpj;

    @NotBlank
    private String businessName;

    @NotBlank
    private String tradeName;

    @NotBlank
    @Email
    private String email;

    private List<String> phoneNumbers;

    private List<Contacts> contacts;

    private List<Address> deliveryAddresses;
}
