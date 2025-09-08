package com.payment.engine.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.payment.common.result.Result;
import com.payment.engine.entity.TradeOrderLite;
import com.payment.engine.mapper.TradeOrderLiteMapper;
import com.payment.engine.service.PaymentService;
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

    @ApiOperation(value = "受理支付", notes = "支付引擎受理支付请求")
    @PostMapping("/accept")
    public Result<String> accept(@ApiParam(value = "交易单号", required = true) @RequestParam String tradeOrderNo,
                                 @ApiParam(value = "支付方式", required = true) @RequestParam String paymentMethod) {
        TradeOrderLite order = tradeOrderLiteMapper.selectOne(new LambdaQueryWrapper<TradeOrderLite>()
                .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo));
        if (order == null) {
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        String paymentMainNo = paymentService.accept(tradeOrderNo, paymentMethod);
        return Result.success(paymentMainNo);
    }

    @ApiOperation(value = "查询支付结果", notes = "查询指定交易单的支付结果")
    @GetMapping("/result")
    public Result<String> result(@ApiParam(value = "交易单号", required = true) @RequestParam String tradeOrderNo) {
        TradeOrderLite order = tradeOrderLiteMapper.selectOne(new LambdaQueryWrapper<TradeOrderLite>()
                .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo));
        if (order == null) {
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        String status = paymentService.result(tradeOrderNo);
        return Result.success(status);
    }
}


