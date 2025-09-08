package com.payment.checkout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.payment"})
public class PaymentCheckoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentCheckoutApplication.class, args);
    }
}



