package com.payment.acquiring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.payment.acquiring.mapper")
@ComponentScan(basePackages = {"com.payment"})
@EnableScheduling
public class PaymentAcquiringApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentAcquiringApplication.class, args);
    }
}


