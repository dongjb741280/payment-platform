package com.payment.common.result;

import com.payment.common.constant.CommonConstants;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 时间戳
     */
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(String code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(CommonConstants.SUCCESS_CODE, CommonConstants.SUCCESS_MESSAGE);
    }

    /**
     * 成功响应带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, CommonConstants.SUCCESS_MESSAGE, data);
    }

    /**
     * 成功响应带消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail() {
        return new Result<>(CommonConstants.FAIL_CODE, CommonConstants.FAIL_MESSAGE);
    }

    /**
     * 失败响应带消息
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(CommonConstants.FAIL_CODE, message);
    }

    /**
     * 失败响应带码和消息
     */
    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 系统错误响应
     */
    public static <T> Result<T> systemError() {
        return new Result<>(CommonConstants.SYSTEM_ERROR_CODE, CommonConstants.SYSTEM_ERROR_MESSAGE);
    }

    /**
     * 系统错误响应带消息
     */
    public static <T> Result<T> systemError(String message) {
        return new Result<>(CommonConstants.SYSTEM_ERROR_CODE, message);
    }

    /**
     * 参数错误响应
     */
    public static <T> Result<T> paramError() {
        return new Result<>(CommonConstants.PARAM_ERROR_CODE, CommonConstants.PARAM_ERROR_MESSAGE);
    }

    /**
     * 参数错误响应带消息
     */
    public static <T> Result<T> paramError(String message) {
        return new Result<>(CommonConstants.PARAM_ERROR_CODE, message);
    }

    /**
     * 业务错误响应
     */
    public static <T> Result<T> businessError() {
        return new Result<>(CommonConstants.BUSINESS_ERROR_CODE, CommonConstants.BUSINESS_ERROR_MESSAGE);
    }

    /**
     * 业务错误响应带消息
     */
    public static <T> Result<T> businessError(String message) {
        return new Result<>(CommonConstants.BUSINESS_ERROR_CODE, message);
    }

    /**
     * 认证错误响应
     */
    public static <T> Result<T> authError() {
        return new Result<>(CommonConstants.AUTH_ERROR_CODE, CommonConstants.AUTH_ERROR_MESSAGE);
    }

    /**
     * 认证错误响应带消息
     */
    public static <T> Result<T> authError(String message) {
        return new Result<>(CommonConstants.AUTH_ERROR_CODE, message);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return CommonConstants.SUCCESS_CODE.equals(this.code);
    }

    /**
     * 判断是否失败
     */
    public boolean isFail() {
        return !isSuccess();
    }

    /**
     * 设置请求ID
     */
    public Result<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置数据
     */
    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * 设置消息
     */
    public Result<T> message(String message) {
        this.message = message;
        return this;
    }
}
