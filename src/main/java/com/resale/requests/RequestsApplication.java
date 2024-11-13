package com.resale.requests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class RequestsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestsApplication.class, args);
    }
}
