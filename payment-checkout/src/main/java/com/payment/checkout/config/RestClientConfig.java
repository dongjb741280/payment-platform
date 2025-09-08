package com.payment.checkout.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConfigurationProperties(prefix = "engine")
    public ServiceProps engineProps() {
        return new ServiceProps();
    }

    @Bean
    @ConfigurationProperties(prefix = "acquiring")
    public ServiceProps acquiringProps() {
        return new ServiceProps();
    }

    public static class ServiceProps {
        private String baseUrl;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }
}
