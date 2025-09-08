package com.payment.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 商户服务启动类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.payment.merchant", "com.payment.common"})
public class PaymentMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMerchantApplication.class, args);
    }
}
