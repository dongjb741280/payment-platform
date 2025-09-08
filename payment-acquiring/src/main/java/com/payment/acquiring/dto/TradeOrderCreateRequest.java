package com.payment.acquiring.dto;

import com.payment.common.constant.CommonConstants;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TradeOrderCreateRequest {
    @NotBlank
    private String merchantId;
    @NotBlank
    private String merchantOrderNo;
    private String userId;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    private String currency = CommonConstants.CURRENCY_CNY;
    private String subject;
    private String body;
    private String notifyUrl;
    private String returnUrl;
}



