package com.payment.channel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChannelGatewayApplication.class, args);
    }
}
