package com.payment.engine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.payment.engine.mapper")
@ComponentScan(basePackages = {"com.payment"})
@EnableScheduling
public class PaymentEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentEngineApplication.class, args);
    }
}


