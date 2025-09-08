package com.payment.account.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 余额操作请求DTO
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class BalanceOperationRequest {

    /**
     * 账户ID
     */
    @NotBlank(message = "账户ID不能为空")
    private String accountId;

    /**
     * 操作金额
     */
    @NotNull(message = "操作金额不能为空")
    @DecimalMin(value = "0.01", message = "操作金额必须大于0")
    private BigDecimal amount;

    /**
     * 交易类型
     */
    @NotBlank(message = "交易类型不能为空")
    private String transactionType;

    /**
     * 备注
     */
    private String remark;
}
