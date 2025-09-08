package com.payment.common.constant;

/**
 * 公共常量类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public class CommonConstants {

    /**
     * 成功状态码
     */
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE = "操作成功";

    /**
     * 失败状态码
     */
    public static final String FAIL_CODE = "9999";
    public static final String FAIL_MESSAGE = "操作失败";

    /**
     * 系统错误状态码
     */
    public static final String SYSTEM_ERROR_CODE = "5000";
    public static final String SYSTEM_ERROR_MESSAGE = "系统错误";

    /**
     * 参数错误状态码
     */
    public static final String PARAM_ERROR_CODE = "4000";
    public static final String PARAM_ERROR_MESSAGE = "参数错误";

    /**
     * 业务错误状态码
     */
    public static final String BUSINESS_ERROR_CODE = "3000";
    public static final String BUSINESS_ERROR_MESSAGE = "业务错误";

    /**
     * 认证失败状态码
     */
    public static final String AUTH_ERROR_CODE = "2000";
    public static final String AUTH_ERROR_MESSAGE = "认证失败";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 字符编码
     */
    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";

    /**
     * 请求头常量
     */
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String MERCHANT_ID_HEADER = "X-Merchant-Id";
    public static final String SIGNATURE_HEADER = "X-Signature";
    public static final String TIMESTAMP_HEADER = "X-Timestamp";
    public static final String NONCE_HEADER = "X-Nonce";

    /**
     * 缓存键前缀
     */
    public static final String CACHE_PREFIX_USER = "user:";
    public static final String CACHE_PREFIX_MERCHANT = "merchant:";
    public static final String CACHE_PREFIX_ACCOUNT = "account:";
    public static final String CACHE_PREFIX_IDEMPOTENT = "idempotent:";

    /**
     * 缓存过期时间（秒）
     */
    public static final long CACHE_EXPIRE_TIME_1_HOUR = 3600;
    public static final long CACHE_EXPIRE_TIME_1_DAY = 86400;
    public static final long CACHE_EXPIRE_TIME_1_WEEK = 604800;

    /**
     * 业务ID生成相关常量
     */
    public static final String SYSTEM_VERSION = "1";
    public static final String DATA_VERSION = "1";
    public static final String ENV_PRODUCTION = "1";
    public static final String ENV_TEST = "0";

    /**
     * 系统标识码
     */
    public static final String SYSTEM_CODE_USER = "010";
    public static final String SYSTEM_CODE_MERCHANT = "011";
    public static final String SYSTEM_CODE_ACCOUNT = "012";
    public static final String SYSTEM_CODE_ACQUIRING = "013";
    public static final String SYSTEM_CODE_PAYMENT = "014";
    public static final String SYSTEM_CODE_CHANNEL = "015";

    /**
     * 业务类型码
     */
    public static final String BUSINESS_CODE_USER_REGISTER = "00";
    public static final String BUSINESS_CODE_MERCHANT_REGISTER = "00";
    public static final String BUSINESS_CODE_ACCOUNT_CREATE = "00";
    public static final String BUSINESS_CODE_PAYMENT = "01";
    public static final String BUSINESS_CODE_REFUND = "02";
    public static final String BUSINESS_CODE_PREAUTH = "03";
    public static final String BUSINESS_CODE_CAPTURE = "04";

    /**
     * 状态常量
     */
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_DELETED = "DELETED";
    public static final String STATUS_FROZEN = "FROZEN";

    /**
     * 交易订单状态常量
     */
    public static final String TRADE_STATUS_INIT = "INIT";
    public static final String TRADE_STATUS_PAYING = "PAYING";
    public static final String TRADE_STATUS_SUCCESS = "SUCCESS";
    public static final String TRADE_STATUS_FAILED = "FAILED";
    public static final String TRADE_STATUS_CLOSED = "CLOSED";

    /**
     * 币种常量
     */
    public static final String CURRENCY_CNY = "CNY";
    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_JPY = "JPY";

    /**
     * 支付方式常量
     */
    public static final String PAYMENT_METHOD_ALIPAY = "ALIPAY";
    public static final String PAYMENT_METHOD_WECHAT = "WECHAT";
    public static final String PAYMENT_METHOD_UNIONPAY = "UNIONPAY";
    public static final String PAYMENT_METHOD_BALANCE = "BALANCE";
    public static final String PAYMENT_METHOD_BANK_CARD = "BANK_CARD";

    /**
     * 渠道常量
     */
    public static final String CHANNEL_ALIPAY = "ALIPAY";
    public static final String CHANNEL_WECHAT = "WECHAT";
    public static final String CHANNEL_UNIONPAY = "UNIONPAY";
    public static final String CHANNEL_BALANCE = "BALANCE";

    /**
     * 账户类型常量
     */
    public static final String ACCOUNT_TYPE_USER = "USER";
    public static final String ACCOUNT_TYPE_MERCHANT = "MERCHANT";
    public static final String ACCOUNT_TYPE_PLATFORM = "PLATFORM";
    public static final String ACCOUNT_TYPE_CHANNEL = "CHANNEL";
    public static final String ACCOUNT_TYPE_PERSONAL = "PERSONAL";

    /**
     * 交易类型常量
     */
    public static final String TRANSACTION_TYPE_PAYMENT = "PAYMENT";
    public static final String TRANSACTION_TYPE_REFUND = "REFUND";
    public static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";
    public static final String TRANSACTION_TYPE_RECHARGE = "RECHARGE";
    public static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    public static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";

    /**
     * 账户流水操作类型常量
     */
    public static final String ACCOUNT_FLOW_FREEZE = "FREEZE";
    public static final String ACCOUNT_FLOW_UNFREEZE = "UNFREEZE";

    /**
     * 签名算法常量
     */
    public static final String SIGN_ALGORITHM_RSA = "RSA";
    public static final String SIGN_ALGORITHM_SHA256 = "SHA256";
    public static final String SIGN_ALGORITHM_SHA256_WITH_RSA = "SHA256withRSA";

    /**
     * 加密算法常量
     */
    public static final String ENCRYPT_ALGORITHM_AES = "AES";
    public static final String ENCRYPT_ALGORITHM_RSA = "RSA";
    public static final String ENCRYPT_ALGORITHM_DES = "DES";

    /**
     * 密钥类型常量
     */
    public static final String KEY_TYPE_MASTER = "MASTER";
    public static final String KEY_TYPE_WORKING = "WORKING";
    public static final String KEY_TYPE_DATA = "DATA";

    /**
     * 日志级别常量
     */
    public static final String LOG_LEVEL_DEBUG = "DEBUG";
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_WARN = "WARN";
    public static final String LOG_LEVEL_ERROR = "ERROR";

    /**
     * 私有构造函数，防止实例化
     */
    private CommonConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
