package com.payment.common.constant;

/**
 * 全局错误码常量
 */
public final class ErrorCodes {
    private ErrorCodes() {}

    // 通用
    public static final String SUCCESS = "0000";
    public static final String FAIL = "9999";
    public static final String SYSTEM_ERROR = "5000";
    public static final String PARAM_ERROR = "4000";
    public static final String BUSINESS_ERROR = "3000";
    public static final String AUTH_ERROR = "2000";

    // 账户模块
    public static final String ACCOUNT_EXISTS = "ACCOUNT_EXISTS";
    public static final String ACCOUNT_CREATE_FAILED = "ACCOUNT_CREATE_FAILED";
    public static final String ACCOUNT_BALANCE_CREATE_FAILED = "ACCOUNT_BALANCE_CREATE_FAILED";
    public static final String BALANCE_UPDATE_FAILED = "BALANCE_UPDATE_FAILED";
    public static final String INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";
    public static final String INSUFFICIENT_AVAILABLE_BALANCE = "INSUFFICIENT_AVAILABLE_BALANCE";
    public static final String INSUFFICIENT_FROZEN_BALANCE = "INSUFFICIENT_FROZEN_BALANCE";
    public static final String ACCOUNT_DELETE_FAILED = "ACCOUNT_DELETE_FAILED";
    public static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
    public static final String ACCOUNT_BALANCE_NOT_FOUND = "ACCOUNT_BALANCE_NOT_FOUND";

    // 商户模块
    public static final String MERCHANT_CODE_EXISTS = "MERCHANT_CODE_EXISTS";
    public static final String MERCHANT_REGISTER_FAILED = "MERCHANT_REGISTER_FAILED";
    public static final String MERCHANT_NOT_FOUND = "MERCHANT_NOT_FOUND";
    public static final String MERCHANT_UPDATE_FAILED = "MERCHANT_UPDATE_FAILED";
    public static final String MERCHANT_DELETE_FAILED = "MERCHANT_DELETE_FAILED";
    public static final String MERCHANT_ACTIVATE_FAILED = "MERCHANT_ACTIVATE_FAILED";
    public static final String MERCHANT_DEACTIVATE_FAILED = "MERCHANT_DEACTIVATE_FAILED";

    // 控制器错误码
    public static final String GET_ACCOUNT_FAILED = "GET_ACCOUNT_FAILED";
    public static final String GET_ACCOUNTS_BY_USER_FAILED = "GET_ACCOUNTS_BY_USER_FAILED";
    public static final String GET_ACCOUNTS_BY_MERCHANT_FAILED = "GET_ACCOUNTS_BY_MERCHANT_FAILED";
    public static final String ACCOUNT_DEPOSIT_FAILED = "ACCOUNT_DEPOSIT_FAILED";
    public static final String ACCOUNT_WITHDRAW_FAILED = "ACCOUNT_WITHDRAW_FAILED";
    public static final String ACCOUNT_FREEZE_FAILED = "ACCOUNT_FREEZE_FAILED";
    public static final String ACCOUNT_UNFREEZE_FAILED = "ACCOUNT_UNFREEZE_FAILED";
    public static final String GET_ACCOUNT_BALANCE_FAILED = "GET_ACCOUNT_BALANCE_FAILED";
    public static final String DELETE_ACCOUNT_FAILED = "DELETE_ACCOUNT_FAILED";
    public static final String GET_MERCHANT_FAILED = "GET_MERCHANT_FAILED";
    public static final String GET_MERCHANT_BY_CODE_FAILED = "GET_MERCHANT_BY_CODE_FAILED";
    public static final String UPDATE_MERCHANT_FAILED = "UPDATE_MERCHANT_FAILED";
    public static final String DELETE_MERCHANT_FAILED = "DELETE_MERCHANT_FAILED";
    public static final String ACTIVATE_MERCHANT_FAILED = "ACTIVATE_MERCHANT_FAILED";
    public static final String DEACTIVATE_MERCHANT_FAILED = "DEACTIVATE_MERCHANT_FAILED";

    // 用户模块
    public static final String USER_USERNAME_EXISTS = "USER_USERNAME_EXISTS";
    public static final String USER_PHONE_EXISTS = "USER_PHONE_EXISTS";
    public static final String USER_EMAIL_EXISTS = "USER_EMAIL_EXISTS";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_PASSWORD_OLD_INCORRECT = "USER_PASSWORD_OLD_INCORRECT";
    public static final String USER_REGISTER_FAILED = "USER_REGISTER_FAILED";
    public static final String USER_UPDATE_FAILED = "USER_UPDATE_FAILED";
    public static final String USER_DELETE_FAILED = "USER_DELETE_FAILED";
    public static final String USER_FREEZE_FAILED = "USER_FREEZE_FAILED";
    public static final String USER_UNFREEZE_FAILED = "USER_UNFREEZE_FAILED";

    // 支付引擎模块
    public static final String PAYMENT_CREATE_FAILED = "PAYMENT_CREATE_FAILED";
    public static final String PAYMENT_NOT_FOUND = "PAYMENT_NOT_FOUND";
    public static final String PAYMENT_STATUS_INVALID = "PAYMENT_STATUS_INVALID";
    public static final String PAYMENT_AMOUNT_INVALID = "PAYMENT_AMOUNT_INVALID";

    // 收单模块
    public static final String TRADE_ORDER_CREATE_FAILED = "TRADE_ORDER_CREATE_FAILED";
    public static final String TRADE_ORDER_NOT_FOUND = "TRADE_ORDER_NOT_FOUND";
    public static final String TRADE_ORDER_STATUS_INVALID = "TRADE_ORDER_STATUS_INVALID";
    public static final String TRADE_ORDER_CLOSE_FAILED = "TRADE_ORDER_CLOSE_FAILED";

    // 收银台模块
    public static final String CHECKOUT_PAYMENT_METHOD_INVALID = "CHECKOUT_PAYMENT_METHOD_INVALID";
    public static final String CHECKOUT_RISK_DENIED = "CHECKOUT_RISK_DENIED";
    public static final String CHECKOUT_ORDER_NOT_FOUND = "CHECKOUT_ORDER_NOT_FOUND";
    public static final String CHECKOUT_ORDER_STATUS_INVALID = "CHECKOUT_ORDER_STATUS_INVALID";
}


