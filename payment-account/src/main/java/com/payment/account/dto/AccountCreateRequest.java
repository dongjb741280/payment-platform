package com.payment.account.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 账户创建请求DTO
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class AccountCreateRequest {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户类型
     */
    @NotBlank(message = "账户类型不能为空")
    @Pattern(regexp = "^(PERSONAL|MERCHANT|SYSTEM)$", message = "账户类型格式不正确")
    private String accountType;

    /**
     * 币种
     */
    @NotBlank(message = "币种不能为空")
    @Pattern(regexp = "^[A-Z]{3}$", message = "币种格式不正确")
    private String currency;
}
