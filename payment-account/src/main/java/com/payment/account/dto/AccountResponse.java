package com.payment.account.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户响应DTO
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class AccountResponse {

    /**
     * 账户ID
     */
    private String accountId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户类型
     */
    private String accountType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 账户状态
     */
    private String status;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;

    /**
     * 总余额
     */
    private BigDecimal totalBalance;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
