package com.resale.requests.controller;

import com.resale.requests.domain.entity.Reseller;
import com.resale.requests.domain.service.ResellerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/resellers")
public class ResellerController {

    private ResellerService resellerService;

    @PostMapping
    public ResponseEntity<String> registerReseller(@Valid @RequestBody Reseller reseller) {
        try {
            return ResponseEntity.ok(resellerService.registerReseller(reseller));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
