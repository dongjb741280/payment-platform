package com.payment.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 账户服务启动类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.payment.account", "com.payment.common"})
public class PaymentAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentAccountApplication.class, args);
    }
}
