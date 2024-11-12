package com.resale.requests.domain.service;

import com.resale.requests.domain.entity.Reseller;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ResellerService {

    private final Map<String, Reseller> resellerMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ResellerService.class);

    public String registerReseller(Reseller reseller) {
        logger.info("Saving reseller {}", reseller);

        if (resellerMap.containsKey(reseller.getCnpj())) {
            throw new IllegalArgumentException("CNPJ already registered.");
        }
        resellerMap.put(reseller.getCnpj(), reseller);
        return "Reseller successfully registered!";
    }

    public void validateReseller(String resellerCnpj) {
        try {
            Reseller reseller = getReseller(resellerCnpj);
            if (Objects.isNull(reseller)) {
                throw new Exception("Reseller with CNPJ: " + resellerCnpj + " not found.");
            }
        } catch (Exception e) {
            logger.error("Error searching for reseller {}.", e.getMessage());
        }
    }

    private Reseller getReseller(String cnpj) {
        return resellerMap.get(cnpj);
    }
}

