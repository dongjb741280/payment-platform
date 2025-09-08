package com.payment.engine.domain;

import com.payment.common.statemachine.BaseEvent;

public enum PaymentEvents implements BaseEvent {
    START_PAY("START_PAY", "发起支付"),
    CHANNEL_SUCCESS("CHANNEL_SUCCESS", "渠道成功"),
    CHANNEL_FAIL("CHANNEL_FAIL", "渠道失败"),
    ACCOUNT_SUCCESS("ACCOUNT_SUCCESS", "记账成功"),
    ACCOUNT_FAIL("ACCOUNT_FAIL", "记账失败");

    private final String code;
    private final String description;

    PaymentEvents(String code, String description) {
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


