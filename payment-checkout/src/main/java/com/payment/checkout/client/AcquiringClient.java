package com.payment.checkout.client;

import com.payment.checkout.config.RestClientConfig.ServiceProps;
import com.payment.common.result.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class AcquiringClient {

    @Resource
    private RestTemplate restTemplate;

    @Resource(name = "acquiringProps")
    private ServiceProps acquiringProps;

    public Map getOrderByTradeOrderNo(String tradeOrderNo) {
        String url = UriComponentsBuilder.fromHttpUrl(acquiringProps.getBaseUrl())
                .path("/api/acquiring/order/")
                .path(tradeOrderNo)
                .toUriString();
        Result response = restTemplate.getForObject(url, Result.class);
        return response != null ? (Map) response.getData() : null;
    }

    public Map pageByMerchant(String merchantId, int pageNum, int pageSize) {
        String url = UriComponentsBuilder.fromHttpUrl(acquiringProps.getBaseUrl())
                .path("/api/acquiring/order/page/merchant")
                .queryParam("merchantId", merchantId)
                .queryParam("pageNum", pageNum)
                .queryParam("pageSize", pageSize)
                .toUriString();
        Result response = restTemplate.getForObject(url, Result.class);
        return response != null ? (Map) response.getData() : null;
    }

    public Boolean closeOrder(String tradeOrderNo) {
        String url = UriComponentsBuilder.fromHttpUrl(acquiringProps.getBaseUrl())
                .path("/api/acquiring/order/")
                .path(tradeOrderNo)
                .path("/close")
                .toUriString();
        Result response = restTemplate.postForObject(url, null, Result.class);
        return response != null ? (Boolean) response.getData() : Boolean.FALSE;
    }
}


