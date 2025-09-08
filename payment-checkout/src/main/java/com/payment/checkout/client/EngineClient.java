package com.payment.checkout.client;

import com.payment.checkout.config.RestClientConfig.ServiceProps;
import com.payment.common.result.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;

@Component
public class EngineClient {

    @Resource
    private RestTemplate restTemplate;

    @Resource(name = "engineProps")
    private ServiceProps engineProps;

    public String accept(String tradeOrderNo, String paymentMethod) {
        String url = UriComponentsBuilder.fromHttpUrl(engineProps.getBaseUrl())
                .path("/api/engine/accept")
                .queryParam("tradeOrderNo", tradeOrderNo)
                .queryParam("paymentMethod", paymentMethod)
                .toUriString();
        Result response = restTemplate.postForObject(url, null, Result.class);
        return response != null ? (String) response.getData() : null;
    }

    public String result(String tradeOrderNo) {
        String url = UriComponentsBuilder.fromHttpUrl(engineProps.getBaseUrl())
                .path("/api/engine/result")
                .queryParam("tradeOrderNo", tradeOrderNo)
                .toUriString();
        Result response = restTemplate.getForObject(url, Result.class);
        return response != null ? (String) response.getData() : null;
    }
}


