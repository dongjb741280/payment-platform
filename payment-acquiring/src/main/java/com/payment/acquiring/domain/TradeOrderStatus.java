package com.payment.acquiring.domain;

import com.payment.common.statemachine.BaseStatus;

public enum TradeOrderStatus implements BaseStatus {
    INIT("INIT", "初始化"),
    PAYING("PAYING", "支付中"),
    SUCCESS("SUCCESS", "支付成功"),
    FAILED("FAILED", "支付失败"),
    CLOSED("CLOSED", "订单关闭");

    private final String code;
    private final String description;

    TradeOrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}


