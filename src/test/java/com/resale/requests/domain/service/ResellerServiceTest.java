package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.Contacts;
import com.resale.requests.domain.entity.Reseller;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ResellerServiceTest {

    @InjectMocks
    private ResellerService resellerService;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(ResellerService.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterResellerSuccess() {
        Reseller reseller = new Reseller();
        reseller.setCnpj("12345678000100");
        reseller.setPhoneNumbers(List.of("11 987654321"));
        reseller.setContacts(List.of(Contacts.builder()
                        .main(true)
                        .contact("John Doe")
                .build()));

        String result = resellerService.registerReseller(reseller);

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Saving reseller " + reseller)), "");
        assertEquals("Reseller successfully registered!", result);
        assertDoesNotThrow(() -> resellerService.validateReseller("12345678000100"));
    }

    @Test
    void testRegisterResellerCnpjAlreadyRegistered() {
        Reseller reseller = new Reseller();
        reseller.setCnpj("12345678000100");
        reseller.setPhoneNumbers(List.of("11 987654321"));
        reseller.setContacts(List.of(Contacts.builder()
                .main(true)
                .contact("John Doe")
                .build()));

        resellerService.registerReseller(reseller);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resellerService.registerReseller(reseller);
        });
        assertEquals("CNPJ already registered.", exception.getMessage());
    }

    @Test
    void testRegisterResellerInvalidPhoneNumber() {
        Reseller reseller = new Reseller();
        reseller.setCnpj("12345678000100");
        reseller.setPhoneNumbers(List.of("12345"));
        reseller.setContacts(List.of(Contacts.builder()
                .main(true)
                .contact("John Doe")
                .build()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resellerService.registerReseller(reseller);
        });
        assertEquals("Invalid phone number", exception.getMessage());
    }

    @Test
    void testRegisterResellerNoMainContact() {
        Reseller reseller = new Reseller();
        reseller.setCnpj("12345678000100");
        reseller.setPhoneNumbers(List.of("12345"));
        reseller.setContacts(List.of(Contacts.builder()
                .main(false)
                .contact("John Doe")
                .build()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resellerService.registerReseller(reseller);
        });
        assertEquals("There must be a primary contact name", exception.getMessage());
    }

    @Test
    void testValidateResellerNotFound() {
        String nonExistentCnpj = "99999999000199";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resellerService.validateReseller(nonExistentCnpj);
        });

        assertTrue(logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("Error searching for reseller " + exception.getMessage())), "");

        assertEquals("Reseller with CNPJ: 99999999000199 not found.", exception.getMessage());
    }
}
