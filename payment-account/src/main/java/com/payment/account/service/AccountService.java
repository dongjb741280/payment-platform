package com.payment.account.service;

import com.payment.account.dto.AccountCreateRequest;
import com.payment.account.dto.AccountResponse;
import com.payment.account.dto.BalanceOperationRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public interface AccountService {

    /**
     * 创建账户
     *
     * @param request 创建请求
     * @return 账户信息
     */
    AccountResponse createAccount(AccountCreateRequest request);

    /**
     * 根据账户ID获取账户信息
     *
     * @param accountId 账户ID
     * @return 账户信息
     */
    AccountResponse getAccountById(String accountId);

    /**
     * 根据用户ID获取账户列表
     *
     * @param userId 用户ID
     * @return 账户列表
     */
    List<AccountResponse> getAccountsByUserId(String userId);

    /**
     * 根据商户ID获取账户列表
     *
     * @param merchantId 商户ID
     * @return 账户列表
     */
    List<AccountResponse> getAccountsByMerchantId(String merchantId);

    /**
     * 账户充值
     *
     * @param request 充值请求
     * @return 操作结果
     */
    boolean deposit(BalanceOperationRequest request);

    /**
     * 账户扣款
     *
     * @param request 扣款请求
     * @return 操作结果
     */
    boolean withdraw(BalanceOperationRequest request);

    /**
     * 账户冻结
     *
     * @param accountId 账户ID
     * @param amount 冻结金额
     * @return 操作结果
     */
    boolean freeze(String accountId, BigDecimal amount);

    /**
     * 账户解冻
     *
     * @param accountId 账户ID
     * @param amount 解冻金额
     * @return 操作结果
     */
    boolean unfreeze(String accountId, BigDecimal amount);

    /**
     * 获取账户余额
     *
     * @param accountId 账户ID
     * @return 账户余额信息
     */
    AccountResponse getAccountBalance(String accountId);

    /**
     * 删除账户
     *
     * @param accountId 账户ID
     */
    void deleteAccount(String accountId);
}
