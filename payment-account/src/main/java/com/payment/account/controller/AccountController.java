package com.payment.account.controller;

import com.payment.common.result.Result;
import com.payment.common.constant.ErrorCodes;
import com.payment.account.dto.AccountCreateRequest;
import com.payment.account.dto.AccountResponse;
import com.payment.account.dto.BalanceOperationRequest;
import com.payment.account.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户控制器
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Api(tags = "账户管理")
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 创建账户
     */
    @ApiOperation(value = "创建账户", notes = "为用户或商户创建新账户")
    @PostMapping
    public Result<AccountResponse> createAccount(@ApiParam(value = "账户创建请求", required = true) @Validated @RequestBody AccountCreateRequest request) {
        log.info("收到创建账户请求，用户ID：{}", request.getUserId());
        
        try {
            AccountResponse response = accountService.createAccount(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建账户失败", e);
            return Result.fail(ErrorCodes.ACCOUNT_CREATE_FAILED, e.getMessage());
        }
    }

    /**
     * 根据账户ID获取账户信息
     */
    @ApiOperation(value = "根据账户ID获取账户信息", notes = "通过账户ID获取账户详细信息")
    @GetMapping("/{accountId}")
    public Result<AccountResponse> getAccountById(@ApiParam(value = "账户ID", required = true) @PathVariable String accountId) {
        log.info("获取账户信息，账户ID：{}", accountId);
        
        try {
            AccountResponse response = accountService.getAccountById(accountId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取账户信息失败", e);
            return Result.fail(ErrorCodes.GET_ACCOUNT_FAILED, e.getMessage());
        }
    }

    /**
     * 根据用户ID获取账户列表
     */
    @ApiOperation(value = "根据用户ID获取账户列表", notes = "获取指定用户的所有账户")
    @GetMapping("/user/{userId}")
    public Result<List<AccountResponse>> getAccountsByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable String userId) {
        log.info("根据用户ID获取账户列表，用户ID：{}", userId);
        
        try {
            List<AccountResponse> responses = accountService.getAccountsByUserId(userId);
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据用户ID获取账户列表失败", e);
            return Result.fail(ErrorCodes.GET_ACCOUNTS_BY_USER_FAILED, e.getMessage());
        }
    }

    /**
     * 根据商户ID获取账户列表
     */
    @ApiOperation(value = "根据商户ID获取账户列表", notes = "获取指定商户的所有账户")
    @GetMapping("/merchant/{merchantId}")
    public Result<List<AccountResponse>> getAccountsByMerchantId(@ApiParam(value = "商户ID", required = true) @PathVariable String merchantId) {
        log.info("根据商户ID获取账户列表，商户ID：{}", merchantId);
        
        try {
            List<AccountResponse> responses = accountService.getAccountsByMerchantId(merchantId);
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据商户ID获取账户列表失败", e);
            return Result.fail(ErrorCodes.GET_ACCOUNTS_BY_MERCHANT_FAILED, e.getMessage());
        }
    }

    /**
     * 账户充值
     */
    @ApiOperation(value = "账户充值", notes = "为指定账户充值")
    @PostMapping("/deposit")
    public Result<Boolean> deposit(@ApiParam(value = "充值请求", required = true) @Validated @RequestBody BalanceOperationRequest request) {
        log.info("账户充值，账户ID：{}，金额：{}", request.getAccountId(), request.getAmount());
        
        try {
            boolean result = accountService.deposit(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("账户充值失败", e);
            return Result.fail(ErrorCodes.ACCOUNT_DEPOSIT_FAILED, e.getMessage());
        }
    }

    /**
     * 账户扣款
     */
    @ApiOperation(value = "账户扣款", notes = "从指定账户扣款")
    @PostMapping("/withdraw")
    public Result<Boolean> withdraw(@ApiParam(value = "扣款请求", required = true) @Validated @RequestBody BalanceOperationRequest request) {
        log.info("账户扣款，账户ID：{}，金额：{}", request.getAccountId(), request.getAmount());
        
        try {
            boolean result = accountService.withdraw(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("账户扣款失败", e);
            return Result.fail(ErrorCodes.ACCOUNT_WITHDRAW_FAILED, e.getMessage());
        }
    }

    /**
     * 账户冻结
     */
    @ApiOperation(value = "账户冻结", notes = "冻结指定账户的指定金额")
    @PostMapping("/{accountId}/freeze")
    public Result<Boolean> freeze(@ApiParam(value = "账户ID", required = true) @PathVariable String accountId, @ApiParam(value = "冻结金额", required = true) @RequestParam BigDecimal amount) {
        log.info("账户冻结，账户ID：{}，金额：{}", accountId, amount);
        
        try {
            boolean result = accountService.freeze(accountId, amount);
            return Result.success(result);
        } catch (Exception e) {
            log.error("账户冻结失败", e);
            return Result.fail(ErrorCodes.ACCOUNT_FREEZE_FAILED, e.getMessage());
        }
    }

    /**
     * 账户解冻
     */
    @ApiOperation(value = "账户解冻", notes = "解冻指定账户的指定金额")
    @PostMapping("/{accountId}/unfreeze")
    public Result<Boolean> unfreeze(@ApiParam(value = "账户ID", required = true) @PathVariable String accountId, @ApiParam(value = "解冻金额", required = true) @RequestParam BigDecimal amount) {
        log.info("账户解冻，账户ID：{}，金额：{}", accountId, amount);
        
        try {
            boolean result = accountService.unfreeze(accountId, amount);
            return Result.success(result);
        } catch (Exception e) {
            log.error("账户解冻失败", e);
            return Result.fail(ErrorCodes.ACCOUNT_UNFREEZE_FAILED, e.getMessage());
        }
    }

    /**
     * 获取账户余额
     */
    @ApiOperation(value = "获取账户余额", notes = "获取指定账户的余额信息")
    @GetMapping("/{accountId}/balance")
    public Result<AccountResponse> getAccountBalance(@ApiParam(value = "账户ID", required = true) @PathVariable String accountId) {
        log.info("获取账户余额，账户ID：{}", accountId);
        
        try {
            AccountResponse response = accountService.getAccountBalance(accountId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取账户余额失败", e);
            return Result.fail(ErrorCodes.GET_ACCOUNT_BALANCE_FAILED, e.getMessage());
        }
    }

    /**
     * 删除账户
     */
    @ApiOperation(value = "删除账户", notes = "删除指定账户")
    @DeleteMapping("/{accountId}")
    public Result<Void> deleteAccount(@ApiParam(value = "账户ID", required = true) @PathVariable String accountId) {
        log.info("删除账户，账户ID：{}", accountId);
        
        try {
            accountService.deleteAccount(accountId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除账户失败", e);
            return Result.fail(ErrorCodes.DELETE_ACCOUNT_FAILED, e.getMessage());
        }
    }
}
