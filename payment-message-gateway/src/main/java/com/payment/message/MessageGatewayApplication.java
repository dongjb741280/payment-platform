package com.payment.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MessageGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageGatewayApplication.class, args);
    }
}
