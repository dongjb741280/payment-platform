package com.payment.acquiring.controller;

import com.payment.acquiring.dto.TradeOrderCreateRequest;
import com.payment.acquiring.entity.TradeOrder;
import com.payment.acquiring.service.TradeOrderService;
import com.payment.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "交易订单管理")
@RestController
@RequestMapping("/api/acquiring/order")
public class TradeOrderController {

    @Resource
    private TradeOrderService tradeOrderService;
    @Resource
    private MessageSource messageSource;

    @ApiOperation(value = "创建交易订单", notes = "创建新的交易订单")
    @PostMapping("/create")
    public Result<TradeOrder> create(@ApiParam(value = "交易订单创建请求", required = true) @Validated @RequestBody TradeOrderCreateRequest request) {
        TradeOrder order = tradeOrderService.createOrder(request);
        return Result.success(order);
    }

    @ApiOperation(value = "根据交易单号获取订单", notes = "通过交易单号获取订单详细信息")
    @GetMapping("/{tradeOrderNo}")
    public Result<TradeOrder> get(@ApiParam(value = "交易单号", required = true) @PathVariable String tradeOrderNo) {
        TradeOrder order = tradeOrderService.getByTradeOrderNo(tradeOrderNo);
        if (order == null) {
            return Result.businessError(messageSource.getMessage("order.not.exist", null, LocaleContextHolder.getLocale()));
        }
        return Result.success(order);
    }

    @ApiOperation(value = "根据商户信息获取订单", notes = "通过商户ID和商户订单号获取订单")
    @GetMapping("/merchant")
    public Result<TradeOrder> getByMerchant(@ApiParam(value = "商户ID", required = true) @RequestParam String merchantId,
                                            @ApiParam(value = "商户订单号", required = true) @RequestParam String merchantOrderNo) {
        TradeOrder order = tradeOrderService.getByMerchantPair(merchantId, merchantOrderNo);
        return Result.success(order);
    }

    @ApiOperation(value = "关闭交易订单", notes = "关闭指定交易订单")
    @PostMapping("/{tradeOrderNo}/close")
    public Result<Boolean> close(@ApiParam(value = "交易单号", required = true) @PathVariable String tradeOrderNo) {
        boolean ok = tradeOrderService.closeOrder(tradeOrderNo);
        if (!ok) {
            return Result.businessError(messageSource.getMessage("order.status.invalid", null, LocaleContextHolder.getLocale()));
        }
        return Result.success(ok);
    }

    @ApiOperation(value = "分页查询商户订单", notes = "分页获取指定商户的订单列表")
    @GetMapping("/page/merchant")
    public Result<?> pageByMerchant(@ApiParam(value = "商户ID", required = true) @RequestParam String merchantId,
                                    @ApiParam(value = "页码", required = true) @RequestParam int pageNum,
                                    @ApiParam(value = "每页大小", required = true) @RequestParam int pageSize) {
        return Result.success(tradeOrderService.pageByMerchant(merchantId, pageNum, pageSize));
    }
}


