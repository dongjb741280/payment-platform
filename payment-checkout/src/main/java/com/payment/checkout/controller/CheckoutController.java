package com.payment.checkout.controller;

import com.payment.checkout.dto.PaymentMethodDTO;
import com.payment.checkout.service.EngineRouteService;
import com.payment.checkout.service.PaymentMethodService;
import com.payment.checkout.service.RiskService;
import com.payment.checkout.client.EngineClient;
import com.payment.checkout.client.AcquiringClient;
import com.payment.common.result.Result;
import com.payment.common.constant.CommonConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import com.payment.checkout.service.CacheService;
import com.payment.checkout.util.CacheKeyBuilder;
import com.payment.checkout.config.CacheProperties;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "收银台管理")
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Resource
    private PaymentMethodService paymentMethodService;

    @Resource
    private RiskService riskService;

    @Resource
    private EngineRouteService engineRouteService;
    @Resource
    private EngineClient engineClient;
    @Resource
    private AcquiringClient acquiringClient;
    @Resource
    private MessageSource messageSource;
    @Resource
    private CacheService cacheService;
    @Resource
    private CacheKeyBuilder cacheKeyBuilder;
    @Resource
    private CacheProperties cacheProperties;

    @ApiOperation(value = "咨询支付方式", notes = "获取指定交易单可用的支付方式")
    @GetMapping("/consult")
    public Result<List<PaymentMethodDTO>> consult(@ApiParam(value = "交易单号", required = true) @RequestParam String tradeOrderNo) {
        String key = cacheKeyBuilder.consult(tradeOrderNo);
        @SuppressWarnings("unchecked")
        List<PaymentMethodDTO> methods = (List<PaymentMethodDTO>) cacheService.get(key, List.class);
        if (methods == null) {
            methods = paymentMethodService.consult(tradeOrderNo);
            cacheService.set(key, methods, cacheProperties.getConsultTtlSeconds());
        }
        return Result.success(methods);
    }

    @ApiOperation(value = "受理支付", notes = "受理指定交易单的支付请求")
    @PostMapping("/accept")
    public Result<String> accept(@ApiParam(value = "交易单号", required = true) @RequestParam String tradeOrderNo,
                                 @ApiParam(value = "支付方式", required = true) @RequestParam String paymentMethod,
                                 @ApiParam(value = "商户ID", required = false) @RequestParam(required = false) String merchantId) {
        // 1) 校验交易单存在
        var order = acquiringClient.getOrderByTradeOrderNo(tradeOrderNo);
        if (order == null) {
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        // 1.1) 可选：校验商户归属
        if (merchantId != null) {
            Object mid = order.get("merchantId");
            if (mid == null || !merchantId.equals(mid.toString())) {
                return Result.businessError(messageSource.getMessage("order.owner.mismatch", null, LocaleContextHolder.getLocale()));
            }
        }
        // 2) 校验状态合法：INIT 或 PAYING 才可受理
        Object status = order.get("status");
        if (status == null || !(CommonConstants.TRADE_STATUS_INIT.equals(status) || CommonConstants.TRADE_STATUS_PAYING.equals(status))) {
            return Result.businessError(messageSource.getMessage("order.status.invalid", null, LocaleContextHolder.getLocale()));
        }
        if (!riskService.passBasicCheck(tradeOrderNo, paymentMethod)) {
            return Result.businessError(messageSource.getMessage("risk.denied", null, LocaleContextHolder.getLocale()));
        }
        String engineResult = engineRouteService.accept(tradeOrderNo, paymentMethod);
        return Result.success(engineResult);
    }

    @ApiOperation(value = "轮询支付结果", notes = "轮询指定交易单的支付结果")
    @GetMapping("/poll")
    public Result<String> poll(@ApiParam(value = "交易单号", required = true) @RequestParam String tradeOrderNo) {
        String key = cacheKeyBuilder.paymentStatus(tradeOrderNo);
        String result = cacheService.get(key, String.class);
        if (result == null) {
            result = engineClient.result(tradeOrderNo);
            if (result != null) {
                cacheService.set(key, result, cacheProperties.getPaymentStatusTtlSeconds());
            }
        }
        return Result.success(result);
    }

    @ApiOperation(value = "分页查询商户订单", notes = "分页获取指定商户的订单列表")
    @GetMapping("/merchant/orders")
    public Result<?> pageMerchantOrders(@ApiParam(value = "商户ID", required = true) @RequestParam String merchantId,
                                        @ApiParam(value = "页码", required = true) @RequestParam int pageNum,
                                        @ApiParam(value = "每页大小", required = true) @RequestParam int pageSize) {
        return Result.success(acquiringClient.pageByMerchant(merchantId, pageNum, pageSize));
    }

    @ApiOperation(value = "获取订单详情", notes = "获取指定交易单的详细信息")
    @GetMapping("/order/{tradeOrderNo}")
    public Result<?> getOrderDetail(@ApiParam(value = "交易单号", required = true) @PathVariable String tradeOrderNo,
                                    @ApiParam(value = "商户ID", required = false) @RequestParam(required = false) String merchantId) {
        String key = cacheKeyBuilder.order(tradeOrderNo);
        Map<String, Object> order = cacheService.get(key, Map.class);
        if (order == null) {
            order = acquiringClient.getOrderByTradeOrderNo(tradeOrderNo);
            if (order != null) {
                cacheService.set(key, order, cacheProperties.getOrderTtlSeconds());
            } else {
                cacheService.set(key, new java.util.HashMap<>(), cacheProperties.getNegativeTtlSeconds());
            }
        }
        if (order == null) {
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        if (merchantId != null) {
            Object mid = order.get("merchantId");
            if (mid == null || !merchantId.equals(mid.toString())) {
                return Result.businessError(messageSource.getMessage("order.owner.mismatch", null, LocaleContextHolder.getLocale()));
            }
        }
        return Result.success(order);
    }

    @ApiOperation(value = "关闭订单", notes = "关闭指定交易单")
    @PostMapping("/order/{tradeOrderNo}/close")
    public Result<Boolean> closeOrder(@ApiParam(value = "交易单号", required = true) @PathVariable String tradeOrderNo,
                                      @ApiParam(value = "商户ID", required = false) @RequestParam(required = false) String merchantId) {
        var order = acquiringClient.getOrderByTradeOrderNo(tradeOrderNo);
        if (order == null) {
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        if (merchantId != null) {
            Object mid = order.get("merchantId");
            if (mid == null || !merchantId.equals(mid.toString())) {
                return Result.businessError(messageSource.getMessage("order.owner.mismatch", null, LocaleContextHolder.getLocale()));
            }
        }
        Boolean ok = acquiringClient.closeOrder(tradeOrderNo);
        return Result.success(ok);
    }
}


