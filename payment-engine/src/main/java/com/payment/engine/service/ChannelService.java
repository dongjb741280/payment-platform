package com.payment.engine.service;

import java.math.BigDecimal;

public interface ChannelService {
    ChannelResult pay(String channelCode, String paymentDetailNo, BigDecimal amount, String currency);

    class ChannelResult {
        private final boolean success;
        private final String channelOrderNo;
        private final String errorCode;
        private final String errorMessage;

        public ChannelResult(boolean success, String channelOrderNo, String errorCode, String errorMessage) {
            this.success = success;
            this.channelOrderNo = channelOrderNo;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public String getChannelOrderNo() { return channelOrderNo; }
        public String getErrorCode() { return errorCode; }
        public String getErrorMessage() { return errorMessage; }
    }
}


