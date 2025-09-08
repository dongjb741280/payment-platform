package com.payment.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置类
 * 
 * 注意：SpringFox 3.0.0 与 Spring Boot 2.7+ 存在兼容性问题
 * 建议使用 SpringDoc OpenAPI 替代
 * 
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2
@Profile("!prod") // 只在非生产环境启用
public class SwaggerConfig {

    /**
     * 创建API文档
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.payment"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 创建API信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("支付系统API文档")
                .description("企业级支付平台系统API接口文档")
                .version("1.0.0")
                .contact(new Contact("Payment Platform Team", "", "team@payment.com"))
                .build();
    }
}