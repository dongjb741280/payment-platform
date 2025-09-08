package com.payment.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务启动类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.payment.user.mapper")
@ComponentScan(basePackages = {"com.payment"})
public class PaymentUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentUserApplication.class, args);
    }
}
