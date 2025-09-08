package com.payment.merchant.controller;

import com.payment.common.result.Result;
import com.payment.common.constant.ErrorCodes;
import com.payment.merchant.dto.MerchantRegisterRequest;
import com.payment.merchant.dto.MerchantResponse;
import com.payment.merchant.dto.MerchantUpdateRequest;
import com.payment.merchant.service.MerchantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户控制器
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Api(tags = "商户管理")
@Slf4j
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    /**
     * 注册商户
     */
    @ApiOperation(value = "注册商户", notes = "注册新商户")
    @PostMapping("/register")
    public Result<MerchantResponse> registerMerchant(@ApiParam(value = "商户注册请求", required = true) @Validated @RequestBody MerchantRegisterRequest request) {
        log.info("收到商户注册请求，商户名称：{}", request.getMerchantName());
        
        try {
            MerchantResponse response = merchantService.registerMerchant(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("商户注册失败", e);
            return Result.fail(ErrorCodes.MERCHANT_REGISTER_FAILED, e.getMessage());
        }
    }

    /**
     * 根据商户ID获取商户信息
     */
    @ApiOperation(value = "根据商户ID获取商户信息", notes = "通过商户ID获取商户详细信息")
    @GetMapping("/{merchantId}")
    public Result<MerchantResponse> getMerchantById(@ApiParam(value = "商户ID", required = true) @PathVariable String merchantId) {
        log.info("获取商户信息，商户ID：{}", merchantId);
        
        try {
            MerchantResponse response = merchantService.getMerchantById(merchantId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取商户信息失败", e);
            return Result.fail(ErrorCodes.GET_MERCHANT_FAILED, e.getMessage());
        }
    }

    /**
     * 根据商户编码获取商户信息
     */
    @ApiOperation(value = "根据商户编码获取商户信息", notes = "通过商户编码获取商户详细信息")
    @GetMapping("/code/{merchantCode}")
    public Result<MerchantResponse> getMerchantByCode(@ApiParam(value = "商户编码", required = true) @PathVariable String merchantCode) {
        log.info("根据商户编码获取商户信息，商户编码：{}", merchantCode);
        
        try {
            MerchantResponse response = merchantService.getMerchantByCode(merchantCode);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据商户编码获取商户信息失败", e);
            return Result.fail(ErrorCodes.GET_MERCHANT_BY_CODE_FAILED, e.getMessage());
        }
    }

    /**
     * 更新商户信息
     */
    @ApiOperation(value = "更新商户信息", notes = "更新指定商户的信息")
    @PutMapping("/{merchantId}")
    public Result<MerchantResponse> updateMerchant(@ApiParam(value = "商户ID", required = true) @PathVariable String merchantId, 
                                                   @ApiParam(value = "商户更新请求", required = true) @Validated @RequestBody MerchantUpdateRequest request) {
        log.info("更新商户信息，商户ID：{}", merchantId);
        
        try {
            MerchantResponse response = merchantService.updateMerchant(merchantId, request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新商户信息失败", e);
            return Result.fail(ErrorCodes.UPDATE_MERCHANT_FAILED, e.getMessage());
        }
    }

    /**
     * 删除商户
     */
    @ApiOperation(value = "删除商户", notes = "删除指定商户")
    @DeleteMapping("/{merchantId}")
    public Result<Void> deleteMerchant(@ApiParam(value = "商户ID", required = true) @PathVariable String merchantId) {
        log.info("删除商户，商户ID：{}", merchantId);
        
        try {
            merchantService.deleteMerchant(merchantId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除商户失败", e);
            return Result.fail(ErrorCodes.DELETE_MERCHANT_FAILED, e.getMessage());
        }
    }

    /**
     * 激活商户
     */
    @ApiOperation(value = "激活商户", notes = "激活指定商户")
    @PostMapping("/{merchantId}/activate")
    public Result<Void> activateMerchant(@ApiParam(value = "商户ID", required = true) @PathVariable String merchantId) {
        log.info("激活商户，商户ID：{}", merchantId);
        
        try {
            merchantService.activateMerchant(merchantId);
            return Result.success();
        } catch (Exception e) {
            log.error("激活商户失败", e);
            return Result.fail(ErrorCodes.ACTIVATE_MERCHANT_FAILED, e.getMessage());
        }
    }

    /**
     * 停用商户
     */
    @ApiOperation(value = "停用商户", notes = "停用指定商户")
    @PostMapping("/{merchantId}/deactivate")
    public Result<Void> deactivateMerchant(@ApiParam(value = "商户ID", required = true) @PathVariable String merchantId) {
        log.info("停用商户，商户ID：{}", merchantId);
        
        try {
            merchantService.deactivateMerchant(merchantId);
            return Result.success();
        } catch (Exception e) {
            log.error("停用商户失败", e);
            return Result.fail(ErrorCodes.DEACTIVATE_MERCHANT_FAILED, e.getMessage());
        }
    }
}
