package com.payment.engine.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.payment.common.result.Result;
import com.payment.engine.entity.TradeOrderLite;
import com.payment.engine.mapper.TradeOrderLiteMapper;
import com.payment.engine.service.PaymentService;
import com.payment.engine.service.CacheService;
import com.payment.engine.config.CacheProperties;
import com.payment.engine.util.CacheKeyBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "支付引擎管理")
@RestController
@RequestMapping("/api/engine")
public class EngineController {

    @Resource
    private PaymentService paymentService;
    @Resource
    private TradeOrderLiteMapper tradeOrderLiteMapper;
    @Resource
    private MessageSource messageSource;
    @Resource
    private CacheService cacheService;
    @Resource
    private CacheProperties cacheProperties;
    @Resource
    private CacheKeyBuilder cacheKeyBuilder;

    @ApiOperation(value = "受理支付", notes = "支付引擎受理支付请求")
    @PostMapping("/accept")
    public Result<String> accept(@ApiParam(value = "交易单号", required = true) @RequestParam String tradeOrderNo,
                                 @ApiParam(value = "支付方式", required = true) @RequestParam String paymentMethod) {
        String cacheKey = cacheKeyBuilder.tradeLite(tradeOrderNo);
        TradeOrderLite order = cacheService.get(cacheKey, TradeOrderLite.class);
        if (order == null) {
            order = tradeOrderLiteMapper.selectOne(new LambdaQueryWrapper<TradeOrderLite>()
                    .select(TradeOrderLite::getTradeOrderNo, TradeOrderLite::getAmount, TradeOrderLite::getCurrency, TradeOrderLite::getStatus)
                    .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo));
            if (order != null) {
                cacheService.set(cacheKey, order, cacheProperties.getTradeLiteTtlSeconds());
            }
        }
        if (order == null) {
            // 短期负缓存，防穿透
            cacheService.set(cacheKey, new TradeOrderLite(), 30);
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        String paymentMainNo = paymentService.accept(tradeOrderNo, paymentMethod);
        return Result.success(paymentMainNo);
    }

    @ApiOperation(value = "查询支付结果", notes = "查询指定交易单的支付结果")
    @GetMapping("/result")
    public Result<String> result(@ApiParam(value = "交易单号", required = true) @RequestParam String tradeOrderNo) {
        String cacheKey = cacheKeyBuilder.tradeLite(tradeOrderNo);
        TradeOrderLite order = cacheService.get(cacheKey, TradeOrderLite.class);
        if (order == null) {
            order = tradeOrderLiteMapper.selectOne(new LambdaQueryWrapper<TradeOrderLite>()
                    .select(TradeOrderLite::getTradeOrderNo, TradeOrderLite::getStatus)
                    .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo));
            if (order != null) {
                cacheService.set(cacheKey, order, cacheProperties.getTradeLiteTtlSeconds());
            }
        }
        if (order == null) {
            // 短期负缓存，防穿透
            cacheService.set(cacheKey, new TradeOrderLite(), 30);
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        String status = paymentService.result(tradeOrderNo);
        // 更新缓存中的状态
        order.setStatus(status);
        cacheService.set(cacheKey, order, cacheProperties.getTradeLiteTtlSeconds());
        return Result.success(status);
    }
}


